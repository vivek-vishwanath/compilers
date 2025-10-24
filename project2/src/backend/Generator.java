package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.MemLayout;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.Register;
import backend.interpreter.mips.operand.Imm.ImmType;
import ir.*;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Generator {

    public static void main(String[] args) throws IOException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        FileWriter writer = new FileWriter(args[1]);

        writer.write(".data\n\tSTACK: .word " + (long) MemLayout.STACK + "\n\n");
        writer.write(".text\n");
        writer.write("\tlw $sp, STACK\n");
        writer.write("\tmove $fp, $sp\n");
        writer.write("\tjal main\n\tli $v0, 10\n\tsyscall\n\n");

        for (IRFunction function : program.functions) {
            ArrayList<MIPSInstruction> instructions = new ArrayList<>();
            writer.write(function.name + ":\n");
            String label = null;
            for (int i = 0; i < Math.min(function.parameters.size(), 4); i++) {
                IRVariableOperand op = function.parameters.get(i);
                Register.Physical.putReg(op, "$a" + i);
            }
            for (IRInstruction instruction : function.instructions) {
                for (int i = 0; i < instruction.operands.length; i++) {
                    IROperand operand = instruction.operands[i];
                    if (operand instanceof IRLabelOperand) {
                        String name = function.name + "_" + operand;
                        instruction.operands[i] = new IRLabelOperand(name, instruction);
                    }
                }
                if (instruction.opCode == IRInstruction.OpCode.LABEL) {
                    label = instruction.operands[0].toString();
                    continue;
                }
                IRInstruction.Selector selector = instruction.new Selector(label, function.name);
                ArrayList<MIPSInstruction> list = selector.compile();
                list.forEach(it -> System.out.println("\t\t" + it));
                instructions.addAll(list);
                label = null;
            }
            allocate(instructions, function.name);
            instructions.forEach(it -> {
                try {
                    if (it.label != null) {
                        writer.write(it.label + ":\n");
                    }
                    writer.write("\t\t" + it + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.write("\n");
            writer.flush();
        }
        writer.close();

    }

    // call function on a function-by-function basis
    public static void allocate(ArrayList<MIPSInstruction> list, String functionName) {
        HashMap<IRVariableOperand, Integer> stackLocations = new HashMap<>();
        Register fp = Register.Physical.get("$fp");
        Register sp = Register.Physical.get("$sp");
        Register ra = Register.Physical.get("$ra");
        int stackSize = 4 * Register.numRegs();
        int stackEdge = stackSize - 4;
        Imm stackOff = new Imm("" + -stackSize, ImmType.INT);
        list.add(0, new MIPSInstruction(MIPSOp.SW, null, null, fp, new Addr(new Imm("-8", ImmType.INT), sp)));
        list.add(1, new MIPSInstruction(MIPSOp.ADDI, null, null, fp, sp, new Imm("-8", ImmType.INT)));
        list.add(2, new MIPSInstruction(MIPSOp.SW, null, null, ra, new Addr(new Imm("4", ImmType.INT), fp)));
        list.add(3, new MIPSInstruction(MIPSOp.ADDI, null, null, sp, fp, stackOff));
        for (int i = 0; i < list.size(); i++) {
            MIPSInstruction instruction = list.get(i);
            Register[] reads = instruction.getReads();
            Register write = instruction.getWrite();
            int n = 0;
            if (reads.length > 0)
                while (instruction.operands.get(n) != reads[0]) n++;
            for (int j = 0; j < reads.length; j++) {
                Register read = reads[j];
                // only t regs were once virtual and need a mapping on the stack
                if (!(read instanceof Register.Virtual vreg)) continue;
                Integer stackIdx = stackLocations.getOrDefault(vreg.var, null);
                if (stackIdx == null) {
                    stackIdx = stackEdge;
                    stackLocations.put(vreg.var, stackEdge);
                    stackEdge -= 4;
                }
                Register.Physical physical = Register.Physical.get("$t" + (j+1));
                instruction.operands.set(n + j, physical);
                Imm offset = new Imm("" + stackIdx, ImmType.INT);
                Addr address = new Addr(offset, sp);
                // insert right before current instruction
                String label = null;
                if (list.get(i).label != null) {
                    label = list.get(i).label;
                    list.get(i).label = null;
                }
                list.add(i, new MIPSInstruction(MIPSOp.LW, label, instruction.block, physical, address));
                i++; // move i down by 1
            }
            if (write != null) {
                if (write instanceof Register.Virtual vreg) {
                    Integer stackIdx = stackLocations.getOrDefault(vreg.var, null);
                    if (stackIdx == null) {
                        stackIdx = stackEdge;
                        stackLocations.put(vreg.var, stackEdge);
                        stackEdge -= 4;
                    }
                    Register.Physical physical = Register.Physical.get("$t0");
                    instruction.operands.set(0, physical);
                    Imm offset = new Imm("" + stackIdx, ImmType.INT);
                    Addr address = new Addr(offset, sp);
                    // add after current instruction
                    list.add(i + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                    i++; // point i to sw instruction and i++ in for loop will skip over it
                }
            }
        }
        list.add(new MIPSInstruction(MIPSOp.ADDI, functionName + "_teardown", null, sp, fp, new Imm("8", ImmType.INT)));
        list.add(new MIPSInstruction(MIPSOp.LW, null, null, ra, new Addr(new Imm("4", ImmType.INT), fp)));
        list.add(new MIPSInstruction(MIPSOp.LW, null, null, fp, new Addr(new Imm("0", ImmType.INT), fp)));
        list.add(new MIPSInstruction(MIPSOp.JR, null, null, Register.Physical.get("$ra")));
    }

    public static void intraBlockAlloc(ArrayList<MIPSInstruction> block) {
        HashSet<Register.Virtual> vRegSet = new HashSet<>(); 
        for (int i = 0; i < block.size(); i++) {
            Register.Virtual[] reads = (Register.Virtual[])block.get(i).getReads();
            for (Register.Virtual vReg : reads) {
                vReg.end = i;
                vReg.readCount++;
                vRegSet.add(vReg);
            }   
            Register.Virtual write = (Register.Virtual)block.get(i).getWrite();
            if (write != null) {
                write.start = i;
                write.end = i;
            }
        }
        ArrayList<Register.Virtual> vRegList = new ArrayList<>(vRegSet);
        for (int i = 0; i < vRegList.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (vRegList.get(i).start < vRegList.get(j).end && vRegList.get(i).end > vRegList.get(j).start) {
                    vRegList.get(i).concurrentAlives.add(vRegList.get(j));
                    vRegList.get(j).concurrentAlives.add(vRegList.get(i));
                }
            }
        }
        Collections.sort(vRegList);
        HashSet<Register.Physical> phsicalRegisterList = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            phsicalRegisterList.add(Register.Physical.get("$t" + i));
        }
        for (int i = 0; i < vRegList.size(); i++) {
            HashSet<Register.Physical> usedList = new HashSet<>();
            for (int j = 0; j < vRegList.size(); j++) {
                if (vRegList.get(i).concurrentAlives.contains(vRegList.get(j))) {
                    if (!vRegList.get(j).isSpilled) usedList.add(vRegList.get(i).physicalReg);
                }
            }
            HashSet<Register.Physical> copy = new HashSet<>(phsicalRegisterList);
            copy.removeAll(usedList);
            if (copy.size() > 0) {
                vRegList.get(i).physicalReg = copy.iterator().next();
            }
        }
    }
}
