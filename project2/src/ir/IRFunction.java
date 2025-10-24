package ir;

import backend.Block;
import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.Register;
import ir.datatype.IRArrayType;
import ir.datatype.IRType;
import ir.operand.IRLabelOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
        Block block = new Block();
        for (int i = 0; i < Math.min(parameters.size(), 4); i++) {
            IRVariableOperand op = parameters.get(i);
            Register.Physical.putReg(op, "$a" + i);
            block.mipsInst.add(new MIPSInstruction(MIPSOp.MOVE, null, block, Register.Virtual.issueVar(op), Register.Physical.get("$a" + i)));
        }
        if (!block.mipsInst.isEmpty()) {
            blocks.add(0, block);
            mipsInstructions.addAll(block.mipsInst);
        }
        for (int i = 4; i < parameters.size(); i++) {
            IRVariableOperand op = parameters.get(i);
            Register.Virtual.issueVar(op);
        }
        for (IRVariableOperand op : variables) {
            if (op.type instanceof IRArrayType && !parameters.contains(op)) {
                block = new Block();
                block.mipsInst.add(new MIPSInstruction(MIPSOp.LI, null, block, Register.Physical.get("$a0"), new Imm("" + ((IRArrayType) op.type).getSize(), Imm.ImmType.INT)));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.LI, null, block, Register.Physical.get("$v0"), new Imm("9", Imm.ImmType.INT)));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.SYSCALL, null, block));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.MOVE, null, block, Register.Virtual.issueVar(op), Register.Physical.get("$v0")));
                blocks.add(0, block);
                mipsInstructions.addAll(block.mipsInst);
            }
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
                if (label != null) {
                    Register.Physical zero = Register.Physical.get("$zero");
                    MIPSInstruction inst = new MIPSInstruction(MIPSOp.ADD, label, instruction.block, zero, zero, zero);
                    mipsInstructions.add(inst);
                    instruction.block.mipsInst.add(inst);
                }
                label = instruction.operands[0].toString();
                continue;
            }
            IRInstruction.Selector selector = instruction.new Selector(label, name);
            selector.compile();
//            selector.list.forEach(it -> System.out.println("\t\t" + it));
            mipsInstructions.addAll(selector.list);
            label = null;
        }
        if (label != null) {
            Register.Physical zero = Register.Physical.get("$zero");
            block = new Block();
            MIPSInstruction inst = new MIPSInstruction(MIPSOp.ADD, label, block, zero, zero, zero);
            mipsInstructions.add(inst);
            block.mipsInst.add(inst);
            blocks.add(block);
        }
    }

    // start $t# register allocation at number "start"
    public void naiveAlloc(MStack stack, int start) {
        for (int i = 0; i < mipsInstructions.size(); i++) {
            MIPSInstruction instruction = mipsInstructions.get(i);
            Register[] reads = instruction.getReads();
            Register write = instruction.getWrite();
            for (int j = 0; j < reads.length; j++) {
                Register read = reads[j];
                if (!(read instanceof Register.Virtual vreg)) continue;
                Addr address = stack.get(vreg);
                Register.Physical physical = Register.Physical.get("$t" + (start + j));
                for (int k = 0; k < instruction.operands.size(); k++) {
                    if (instruction.operands.get(k) == read) {
                        instruction.operands.set(k, physical);
                        break;
                    }
                    if (instruction.operands.get(k) instanceof Addr addr) {
                        if (addr.register == read) {
                            if (addr.mode == Addr.Mode.BASE_OFFSET) {
                                Addr newAddr = new Addr(addr.constant, physical);
                                instruction.operands.set(k, newAddr);
                            } else {
                                instruction.operands.set(k, new Addr(physical));
                            }
                            break;
                        }
                    }
                }
                String label = null;
                if (mipsInstructions.get(i).label != null) {
                    label = mipsInstructions.get(i).label;
                    mipsInstructions.get(i).label = null;
                }
                mipsInstructions.add(i, new MIPSInstruction(MIPSOp.LW, label, instruction.block, physical, address));
                if (instruction.block != null) {
                    int idx = instruction.block.mipsInst.indexOf(instruction);
                    instruction.block.mipsInst.add(idx, new MIPSInstruction(MIPSOp.LW, label, instruction.block, physical, address));
                }
                i++; // move i down by 1
            }
            if (write instanceof Register.Virtual vreg) {
                Addr address = stack.get(vreg);
                Register.Physical physical = Register.Physical.get("$t" + start);
                instruction.operands.set(0, physical);
                // add after current instruction
                mipsInstructions.add(i + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                if (instruction.block != null) {
                    int idx = instruction.block.mipsInst.indexOf(instruction);
                    instruction.block.mipsInst.add(idx + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                }
                i++; // point i to sw instruction and i++ in for loop will skip over it
            }
        }
    }

    public void allocate(boolean naive) {
        MStack stack = new MStack();
        stack.buildup();
        if (naive) {
            naiveAlloc(stack, 0);
        } else {
            for (Block block : blocks) {
                block.intraBlockAlloc(stack);
            }
            naiveAlloc(stack, 8);
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
            for (int i = 4; i < parameters.size(); i++) {
                IRVariableOperand op = parameters.get(i);
                locations.put(op, stackSize + (i - 4) * 4);
            }
            Block block = new Block();
            Imm stackOff = new Imm("" + ((4 - Math.max(parameters.size(), 4)) * 4 - stackSize), Imm.ImmType.INT);
            block.mipsInst.add(0, new MIPSInstruction(MIPSOp.SW, null, block, fp, new Addr(new Imm("-8", Imm.ImmType.INT), sp)));
            block.mipsInst.add(1, new MIPSInstruction(MIPSOp.ADDI, null, block, fp, sp, new Imm("-8", Imm.ImmType.INT)));
            block.mipsInst.add(2, new MIPSInstruction(MIPSOp.SW, null, block, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
            block.mipsInst.add(3, new MIPSInstruction(MIPSOp.ADDI, null, block, sp, fp, stackOff));
            blocks.add(0, block);
            mipsInstructions.addAll(0, block.mipsInst);
        }

        public void teardown() {
            Block block = new Block();
            block.mipsInst.add(new MIPSInstruction(MIPSOp.ADDI, name + "_teardown", sp, fp, new Imm("8", Imm.ImmType.INT)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, fp, new Addr(new Imm("0", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.JR, null, Register.Physical.get("$ra")));
            blocks.add(block);
            mipsInstructions.addAll(block.mipsInst);
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
