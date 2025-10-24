package ir;

import backend.Block;
import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.Register;
import ir.datatype.IRType;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class IRFunction {

    public String name;

    public IRType returnType;

    public List<IRVariableOperand> parameters;

    public List<IRVariableOperand> variables;

    public List<IRInstruction> irInstructions;

    public HashMap<String, IRInstruction> labelMap = new HashMap<>();

    public ArrayList<Block> blocks = new ArrayList<>();

    public ArrayList<MIPSInstruction> mipsInstructions = new ArrayList<>();

    public IRFunction(String name, IRType returnType,
                      List<IRVariableOperand> parameters, List<IRVariableOperand> variables,
                      List<IRInstruction> instructions) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.variables = variables;
        this.irInstructions = instructions;
        if (instructions != null)
            buildBlocks();
    }

    private HashSet<IRInstruction> findLeaders() {
        HashSet<IRInstruction> leaders = new HashSet<>();
        for (int i = 0; i < irInstructions.size(); i++) {
            IRInstruction inst = irInstructions.get(i);
            IROperand operand = (inst.opCode == IRInstruction.OpCode.CALLR) ? inst.operands[1] : inst.operands[0];
            switch (inst.opCode) {
                case LABEL:
                    leaders.add(inst);
                    break;
                case GOTO:
                    leaders.add(labelMap.get(operand.toString()));
                    break;
                case BREQ:
                case BRNEQ:
                case BRLT:
                case BRGT:
                case BRGEQ:
                    if (i + 1 < irInstructions.size())
                        leaders.add(irInstructions.get(i + 1));
                    leaders.add(labelMap.get(operand.toString()));
                    break;
                case CALL:
                case CALLR:
                    if (i + 1 < irInstructions.size())
                        leaders.add(irInstructions.get(i + 1));
                    break;
                default:
                    break;
            }
        }
        return leaders;
    }

    public void buildBlocks() {
        HashSet<IRInstruction> leaders = findLeaders();
        Block block = new Block();
        for (IRInstruction inst : irInstructions) {
            if (leaders.contains(inst)) {
                blocks.add(block);
                block = new Block();
            }
            inst.block = block;
            block.irInst.add(inst);
        }
        blocks.add(block);
    }

    public void compileToMIPS() {
        String label = null;
        for (int i = 0; i < Math.min(parameters.size(), 4); i++) {
            IRVariableOperand op = parameters.get(i);
            Register.Physical.putReg(op, "$a" + i);
        }
        for (IRInstruction instruction : irInstructions) {
            for (int i = 0; i < instruction.operands.length; i++) {
                IROperand operand = instruction.operands[i];
                if (operand instanceof IRLabelOperand) {
                    String n = name + "_" + operand;
                    instruction.operands[i] = new IRLabelOperand(n, instruction);
                }
            }
            if (instruction.opCode == IRInstruction.OpCode.LABEL) {
                label = instruction.operands[0].toString();
                continue;
            }
            IRInstruction.Selector selector = instruction.new Selector(label, name);
            ArrayList<MIPSInstruction> list = selector.compile();
            list.forEach(it -> System.out.println("\t\t" + it));
            mipsInstructions.addAll(list);
            label = null;
        }
    }

    public void naiveAlloc() {
        HashMap<IRVariableOperand, Integer> stackLocations = new HashMap<>();
        Register fp = Register.Physical.get("$fp");
        Register sp = Register.Physical.get("$sp");
        Register ra = Register.Physical.get("$ra");
        int stackSize = 4 * Register.numRegs();
        int stackEdge = stackSize - 4;
        Imm stackOff = new Imm("" + -stackSize, Imm.ImmType.INT);
        mipsInstructions.add(0, new MIPSInstruction(MIPSOp.SW, null, fp, new Addr(new Imm("-8", Imm.ImmType.INT), sp)));
        mipsInstructions.add(1, new MIPSInstruction(MIPSOp.ADDI, null, fp, sp, new Imm("-8", Imm.ImmType.INT)));
        mipsInstructions.add(2, new MIPSInstruction(MIPSOp.SW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
        mipsInstructions.add(3, new MIPSInstruction(MIPSOp.ADDI, null, sp, fp, stackOff));
        for (int i = 0; i < mipsInstructions.size(); i++) {
            MIPSInstruction instruction = mipsInstructions.get(i);
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
                Register.Physical physical = Register.Physical.get("$t" + (j + 1));
                instruction.operands.set(n + j, physical);
                Imm offset = new Imm("" + stackIdx, Imm.ImmType.INT);
                Addr address = new Addr(offset, sp);
                // insert right before current instruction
                String label = null;
                if (mipsInstructions.get(i).label != null) {
                    label = mipsInstructions.get(i).label;
                    mipsInstructions.get(i).label = null;
                }
                mipsInstructions.add(i, new MIPSInstruction(MIPSOp.LW, label, instruction.block, physical, address));
                i++; // move i down by 1
            }
            if (write instanceof Register.Virtual vreg) {
                Integer stackIdx = stackLocations.getOrDefault(vreg.var, null);
                if (stackIdx == null) {
                    stackIdx = stackEdge;
                    stackLocations.put(vreg.var, stackEdge);
                    stackEdge -= 4;
                }
                Register.Physical physical = Register.Physical.get("$t0");
                instruction.operands.set(0, physical);
                Imm offset = new Imm("" + stackIdx, Imm.ImmType.INT);
                Addr address = new Addr(offset, sp);
                // add after current instruction
                mipsInstructions.add(i + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                i++; // point i to sw instruction and i++ in for loop will skip over it
            }
        }
        mipsInstructions.add(new MIPSInstruction(MIPSOp.ADDI, name + "_teardown", sp, fp, new Imm("8", Imm.ImmType.INT)));
        mipsInstructions.add(new MIPSInstruction(MIPSOp.LW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
        mipsInstructions.add(new MIPSInstruction(MIPSOp.LW, null, fp, new Addr(new Imm("0", Imm.ImmType.INT), fp)));
        mipsInstructions.add(new MIPSInstruction(MIPSOp.JR, null, Register.Physical.get("$ra")));
    }

    public void intraBlockAlloc() {
        MStack stack = new MStack();
        stack.buildup();
        for (Block block : blocks) {
            block.intraBlockAlloc(stack);
        }
        stack.teardown();
    }

    public void print(FileWriter writer, boolean byBlock) {
        if (byBlock)
            blocks.forEach(it -> it.print(writer));
        else
            mipsInstructions.forEach(it -> {
                try {
                    if (it.label != null) {
                        writer.write(it.label + ":\n");
                    }
                    writer.write("\t\t" + it + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }


    public class MStack {

        HashMap<IRVariableOperand, Integer> locations = new HashMap<>();
        int stackSize = 4 * Register.numRegs();
        int edge = stackSize - 4;

        Register fp = Register.Physical.get("$fp");
        Register sp = Register.Physical.get("$sp");
        Register ra = Register.Physical.get("$ra");

        public void buildup() {
            Block block = new Block();
            Imm stackOff = new Imm("" + -stackSize, Imm.ImmType.INT);
            block.mipsInst.add(0, new MIPSInstruction(MIPSOp.SW, null, fp, new Addr(new Imm("-8", Imm.ImmType.INT), sp)));
            block.mipsInst.add(1, new MIPSInstruction(MIPSOp.ADDI, null, fp, sp, new Imm("-8", Imm.ImmType.INT)));
            block.mipsInst.add(2, new MIPSInstruction(MIPSOp.SW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
            block.mipsInst.add(3, new MIPSInstruction(MIPSOp.ADDI, null, sp, fp, stackOff));
            blocks.addFirst(block);
        }

        public void teardown() {
            Block block = new Block();
            block.mipsInst.add(new MIPSInstruction(MIPSOp.ADDI, name + "_teardown", sp, fp, new Imm("8", Imm.ImmType.INT)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, fp, new Addr(new Imm("0", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.JR, null, Register.Physical.get("$ra")));
            blocks.add(block);
        }

        public Addr get(Register.Virtual vreg) {
            Integer stackIdx = locations.getOrDefault(vreg.var, null);
            if (stackIdx == null) {
                stackIdx = edge;
                locations.put(vreg.var, edge);
                edge -= 4;
            }
            Imm offset = new Imm("" + stackIdx, Imm.ImmType.INT);
            return new Addr(offset, Register.Physical.get("$sp"));
        }
    }
}
