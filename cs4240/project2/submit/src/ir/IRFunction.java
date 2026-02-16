package ir;

import backend.Block;
import backend.Generator;
import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
import backend.interpreter.mips.operand.Register.Physical;
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

    public static final int NUM_PHYSICAL = 8;

    public IRFunction(String name, IRType returnType,
                      List<IRVariableOperand> parameters, List<IRVariableOperand> variables,
                      List<IRInstruction> instructions) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.variables = variables;
        this.irInstructions = instructions;
    }

    private HashSet<IRInstruction> findLeaders() {
        HashSet<IRInstruction> leaders = new HashSet<>();
        for (int i = 0; i < irInstructions.size(); i++) {
            IRInstruction inst = irInstructions.get(i);
            IROperand operand = (inst.opCode == IRInstruction.OpCode.CALLR) ? inst.operands[1] : inst.operands[0];
            switch (inst.opCode) {
                case LABEL:
                    inst.branch1 = (i + 1 < irInstructions.size()) ? irInstructions.get(i + 1) : null;
                    leaders.add(inst);
                    break;
                case GOTO:
                    inst.branch1 = labelMap.get(operand.toString());
                    leaders.add(labelMap.get(operand.toString()));
                    break;
                case BREQ:
                case BRNEQ:
                case BRLT:
                case BRGT:
                case BRGEQ:
                    inst.branch1 = (i + 1 < irInstructions.size()) ? irInstructions.get(i + 1) : null;
                    inst.branch2 = labelMap.get(operand.toString());
                    if (i + 1 < irInstructions.size())
                        leaders.add(irInstructions.get(i + 1));
                    leaders.add(labelMap.get(operand.toString()));
                    break;
                case CALL:
                case CALLR:
                    inst.branch1 = (i + 1 < irInstructions.size()) ? irInstructions.get(i + 1) : null;
                    if (i + 1 < irInstructions.size())
                        leaders.add(irInstructions.get(i + 1));
                    break;
                default:
                    inst.branch1 = (i + 1 < irInstructions.size()) ? irInstructions.get(i + 1) : null;
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

    public void buildCFG() {
        for (Block block : blocks) {
            if (block.irInst.isEmpty()) continue;
            IRInstruction last = block.irInst.get(block.irInst.size() - 1);
            block.next1 = last.branch1 == null ? null : last.branch1.block;
            block.next2 = last.branch2 == null ? null : last.branch2.block;
        }
    }

    public void compileToMIPS() {
        String label = null;
        Block block = new Block();
        for (int i = 0; i < Math.min(parameters.size(), 4); i++) {
            IRVariableOperand op = parameters.get(i);
            Physical.putReg(op, "$a" + i);
            block.mipsInst.add(new MIPSInstruction(MIPSOp.MOVE, null, block, Register.Virtual.issueVar(op), Physical.get("$a" + i)));
        }
        if (!block.mipsInst.isEmpty()) {
            block.next1 = blocks.get(0);
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
                block.mipsInst.add(new MIPSInstruction(MIPSOp.LI, null, block, Physical.get("$a0"), new Imm("" + ((IRArrayType) op.type).getSize(), Imm.ImmType.INT)));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.ADD, null, block, Physical.get("$a0"), Physical.get("$a0"), Physical.get("$a0")));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.ADD, null, block, Physical.get("$a0"), Physical.get("$a0"), Physical.get("$a0")));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.LI, null, block, Physical.get("$v0"), new Imm("9", Imm.ImmType.INT)));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.SYSCALL, null, block));
                block.mipsInst.add(new MIPSInstruction(MIPSOp.MOVE, null, block, Register.Virtual.issueVar(op), Physical.get("$v0")));
                block.next1 = blocks.get(0);
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
                    Physical zero = Physical.get("$zero");
                    MIPSInstruction inst = new MIPSInstruction(MIPSOp.ADD, label, instruction.block, zero, zero, zero);
                    mipsInstructions.add(inst);
                    instruction.block.mipsInst.add(inst);
                }
                label = instruction.operands[0].toString();
                continue;
            }
            IRInstruction.Selector selector = instruction.new Selector(label, name);
            selector.compile();
            mipsInstructions.addAll(selector.list);
            label = null;
        }
        if (label != null) {
            Physical zero = Physical.get("$zero");
            block = new Block();
            MIPSInstruction inst = new MIPSInstruction(MIPSOp.ADD, label, block, zero, zero, zero);
            mipsInstructions.add(inst);
            block.mipsInst.add(inst);
            blocks.add(block);
        }
    }

    // start $t# register allocation at number "start"
    public void naiveAlloc(MStack stack, int start) {
        for (Block block : blocks) {

            for (int i = 0; i < block.mipsInst.size(); i++) {
                MIPSInstruction instruction = block.mipsInst.get(i);
                Register[] reads = instruction.getReads();
                Register write = instruction.getWrite();
                for (int j = 0; j < reads.length; j++) {
                    Register read = reads[j];
                    if (!(read instanceof Register.Virtual vreg)) continue;
                    Addr address = stack.get(vreg);
                    Physical physical = Physical.get("$t" + (start + j));
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
                    if (block.mipsInst.get(i).label != null) {
                        label = block.mipsInst.get(i).label;
                        block.mipsInst.get(i).label = null;
                    }
                    if (instruction.block != null) {
                        int idx = instruction.block.mipsInst.indexOf(instruction);
                        instruction.block.mipsInst.add(idx, new MIPSInstruction(MIPSOp.LW, label, instruction.block, physical, address));
                    }
                    i++; // move i down by 1
                }
                if (write instanceof Register.Virtual vreg) {
                    Addr address = stack.get(vreg);
                    Physical physical = Physical.get("$t" + start);
                    instruction.operands.set(0, physical);
                    // add after current instruction
                    block.mipsInst.add(i + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                    if (instruction.block != null) {
                        int idx = instruction.block.mipsInst.indexOf(instruction);
                        instruction.block.mipsInst.add(idx + 1, new MIPSInstruction(MIPSOp.SW, null, instruction.block, physical, address));
                    }
                    i++; // point i to sw instruction and i++ in for loop will skip over it
                }
            }
        }
    }

    public void livenessAnalysis() {
        HashMap<String, Register.Virtual> map = new HashMap<>();
        for (Block block : blocks) {
            for (MIPSInstruction inst : block.mipsInst) {
                for (int i = 0; i < inst.operands.size(); i++) {
                    if (inst.operands.get(i) instanceof Register.Virtual vreg && vreg.var != null) {
                        Register.Virtual vreg2 = map.get(vreg.var.toString());
                        if (vreg2 != null) {
                            inst.operands.set(i, vreg2);
                        } else {
                            map.put(vreg.var.toString(), vreg);
                        }
                    }
                }
            }
        }
        for (Block block : blocks) block.computeUseDef();
        boolean done = false;
        while (!done) {
            done = true;
            for (Block block : blocks) {
                done &= block.livenessAnalysis();
            }
        }
        ArrayList<Register.Virtual> vRegList = new ArrayList<>();
        for (Block block : blocks) {
            for (MIPSInstruction mipsInstruction : block.mipsInst) {
                Register[] reads = mipsInstruction.getReads();
                Register write = mipsInstruction.getWrite();
                for (Register reg : reads) {
                    if (reg instanceof Register.Virtual vReg) vReg.reset();
                }
                if (write instanceof Register.Virtual vReg) vReg.reset();
            }
        }
        for (Block block : blocks)
            vRegList.addAll(block.livenessAlloc());
        for (Register.Virtual vreg : vRegList) {
            vreg.backupAlives = new HashSet<>(vreg.concurrentAlives);
        }
        chaitinBriggsAlloc(vRegList, new MStack());
    }

    public void chaitinBriggsAlloc(ArrayList<Register.Virtual> vRegList, MStack stack) {
        vRegList = new ArrayList<>(vRegList.stream().distinct().toList());
        Collections.sort(vRegList);
        ArrayList<Physical> physicalRegs = new ArrayList<>();
        for (int i = 0; i < NUM_PHYSICAL; i++) {
            physicalRegs.add(Physical.get("$t" + i));
        }
        Stack<Register.Virtual> spillStack = new Stack<>();
        boolean done = false;
        while (true) {
            while (!done) {
                done = true;
                for (int i = 0; i < vRegList.size(); i++) {
                    Register.Virtual vreg = vRegList.get(i);
                    if (vreg.concurrentAlives.size() < NUM_PHYSICAL) {
                        spillStack.push(vreg);
                        for (Register.Virtual concurrent : vreg.concurrentAlives) {
                            concurrent.concurrentAlives.remove(vreg);
                        }
                        vRegList.remove(vreg);
                        done = false;
                    }
                }
            }
            if (vRegList.isEmpty()) break;
            Register.Virtual spill = vRegList.remove(vRegList.size() - 1);
            spill.isSpilled = true;
            spillStack.push(spill);
            for (Register.Virtual concurrent : spill.concurrentAlives) {
                concurrent.concurrentAlives.remove(spill);
            }
        }

        while (!spillStack.isEmpty()) {
            Register.Virtual vreg = spillStack.pop();
            ArrayList<Physical> copy = new ArrayList<>(physicalRegs);
            for (Register.Virtual concurrent : vreg.backupAlives) {
                if (concurrent.physicalReg != null) copy.remove(concurrent.physicalReg);
            }
            if (!(vreg.isSpilled = copy.isEmpty())) {
                vreg.physicalReg = copy.get(0);
            }
        }
        for (Block block : blocks) {
            for (MIPSInstruction inst : block.mipsInst) {
                List<MIPSOperand> operands = inst.operands;
                for (int i = 0; i < operands.size(); i++) {
                    if (operands.get(i) instanceof Register.Virtual vreg && !vreg.isSpilled) {
                        operands.set(i, vreg.physicalReg);
                    }
                    if (operands.get(i) instanceof Addr addr && addr.register instanceof Register.Virtual vreg && !vreg.isSpilled) {
                        if (addr.mode == Addr.Mode.BASE_OFFSET) {
                            operands.set(i, new Addr(addr.constant, vreg.physicalReg));
                        } else if (addr.mode == Addr.Mode.REGISTER) {
                            operands.set(i, new Addr(vreg.physicalReg));
                        }
                    }
                }
            }
        }
        naiveAlloc(stack, 8);
        for (Block block : blocks) {
            ArrayList<MIPSInstruction> mipsInst = block.mipsInst;
            for (int i = 0; i < mipsInst.size(); i++) {
                MIPSInstruction inst = mipsInst.get(i);
                if (inst.op == MIPSOp.JAL) {
                    HashSet<Physical> defs = new HashSet<>();
                    HashSet<Physical> uses = new HashSet<>();
                    HashSet<Physical> killed = new HashSet<>();
                    for (int j = 0; j < i; j++) {
                        Physical write = (Physical) mipsInst.get(j).getWrite();
                        if (write != null) defs.add(write);
                    }
                    for (int j = i + 1; j < mipsInst.size(); j++) {
                        Register[] reads = mipsInst.get(j).getReads();
                        Register write = mipsInst.get(j).getWrite();
                        for (Register read : reads) {
                            if (!killed.contains(read)) uses.add((Physical) read);
                        }
                        if (write instanceof Physical phys) killed.add(phys);
                    }
                    for (Register.Virtual vreg : block.liveIns) {
                        defs.add(vreg.physicalReg);
                    }
                    for (Register.Virtual vreg : block.liveOuts) {
                        uses.add(vreg.physicalReg);
                    }
                    defs.retainAll(uses);
                    int n = 0;
                    for (Physical phys : defs) {
                        if (phys != null && phys.toString().startsWith("$t")) {
                            mipsInst.add(i + 1, new MIPSInstruction(MIPSOp.LW, null, phys, new Addr(new Imm(String.valueOf(n * 4), Imm.ImmType.INT), Physical.get("$sp"))));
                            n++;
                        }
                    }
                    mipsInst.add(i + 1 + n, new MIPSInstruction(MIPSOp.ADDI, null, Physical.get("$sp"), Physical.get("$sp"), new Imm("" + (n * 4), Imm.ImmType.INT)));
                    n = 0;
                    for (Physical phys : defs) {
                        if (phys != null && phys.toString().startsWith("$t")) {
                            mipsInst.add(i, new MIPSInstruction(MIPSOp.SW, null, phys, new Addr(new Imm(String.valueOf(n * 4), Imm.ImmType.INT), Physical.get("$sp"))));
                            n++;
                        }
                    }
                    mipsInst.add(i, new MIPSInstruction(MIPSOp.ADDI, null, Physical.get("$sp"), Physical.get("$sp"), new Imm("-" + (n * 4), Imm.ImmType.INT)));
                    i += n * 2 + 2;

                }
            }
        }

    }

    public void allocate(Generator.Mode mode) {
        MStack stack = new MStack();
        stack.buildup();
        switch (mode) {
            case NAIVE -> naiveAlloc(stack, 0);
            case GREEDY -> {
                for (Block block : blocks) {
                    block.intraBlockAlloc(stack);
                }
                naiveAlloc(stack, 8);
            }
            case CHAITIN_BRIGGS -> livenessAnalysis();
        }
        stack.teardown();
    }

    public void print(FileWriter writer, Generator.Mode mode) {
        if (mode == Generator.Mode.NAIVE)
            blocks.forEach(it -> it.print(writer));
        else if (mode == Generator.Mode.GREEDY)
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
        else if (mode == Generator.Mode.CHAITIN_BRIGGS) {
            blocks.forEach(it -> it.print(writer));
        }
    }


    public class MStack {

        HashMap<IRVariableOperand, Integer> locations = new HashMap<>();
        int stackSize = 4 * Register.numRegs();
        int edge = stackSize - 4;

        Register fp = Physical.get("$fp");
        Register sp = Physical.get("$sp");
        Register ra = Physical.get("$ra");

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
            block.next1 = blocks.get(0);
            blocks.add(0, block);
            mipsInstructions.addAll(0, block.mipsInst);
        }

        public void teardown() {
            Block block = new Block();
            block.mipsInst.add(new MIPSInstruction(MIPSOp.ADDI, name + "_teardown", sp, fp, new Imm("8", Imm.ImmType.INT)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, ra, new Addr(new Imm("4", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.LW, null, fp, new Addr(new Imm("0", Imm.ImmType.INT), fp)));
            block.mipsInst.add(new MIPSInstruction(MIPSOp.JR, null, Physical.get("$ra")));
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
            return new Addr(offset, Physical.get("$sp"));
        }
    }
}
