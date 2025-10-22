package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.Register;
import backend.interpreter.mips.operand.Imm.ImmType;
import ir.*;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Generator {

    public static void main(String[] args) throws IOException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        FileWriter writer = new FileWriter(args[1]);

        writer.write(".text\n\nj main\n\n");

        for (IRFunction function : program.functions) {
            IRInstruction.vregCount = 32;
            ArrayList<MIPSInstruction> instructions = new ArrayList<>();
            writer.write(function.name + ":\n");
            String label = null;
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
                ArrayList<MIPSInstruction> list = instruction.compile(label);
                list.forEach(it -> System.out.println("\t\t" + it));
                instructions.addAll(list);
                label = null;
            }
            allocate(instructions, IRInstruction.vregCount);
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
        writer.write("\tli $v0, 10\n\tsyscall");
        writer.close();

    }

    // call function on a function-by-function basis
    public static void allocate(ArrayList<MIPSInstruction> list, int numVRegisters) {
        // ignore non virtual registers (a's, v's, etc.)
        Register stack = new Register("$sp");
        Imm stackOff = new Imm("" + -4*numVRegisters, ImmType.INT);
        list.addFirst(new MIPSInstruction(MIPSOp.ADDI, list.get(1).label, stack, stack, stackOff));
        for (int i = 0; i < list.size(); i++) {
            MIPSInstruction instruction = list.get(i);
            instruction.virtualToPhysical();
            Register[] reads = instruction.getReads();
            Register write = instruction.getWrite();
            for (Register read : reads) {
                // only t regs were once virtual and need a mapping on the stack
                if (read.isNotT()) continue;
                Imm offset = new Imm("" + 4 * (Integer.parseInt(read.oldName.substring(1)) - IRInstruction.REGISTER_COUNT), ImmType.INT);
                Addr address = new Addr(offset, stack);
                // insert right before current instruction
                String label = null;
                if (list.get(i).label != null) {
                    label = list.get(i).label;
                    list.get(i).label = null;
                }
                list.add(i, new MIPSInstruction(MIPSOp.LW, label, read, address));
                i++; // move i down by 1
            }
            if (write != null) {
                if (write.isNotT()) continue;
                Imm offset = new Imm("" + 4 * (Integer.parseInt(write.oldName.substring(1)) - IRInstruction.REGISTER_COUNT), ImmType.INT);
                Addr address = new Addr(offset, stack);
                // add after current instruction
                list.add(i + 1, new MIPSInstruction(MIPSOp.SW, null, write, address));
                i++; // point i to sw instruction and i++ in for loop will skip over it
            }
        }
    }
}
