package ir;

import backend.Block;
import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
import ir.datatype.IRArrayType;
import ir.operand.*;

import java.util.ArrayList;
import java.util.HashMap;

public class IRInstruction {

    public enum OpCode {
        ASSIGN,
        ADD, SUB, MULT, DIV, AND, OR,
        GOTO,
        BREQ, BRNEQ, BRLT, BRGT, BRGEQ,
        RETURN,
        CALL, CALLR,
        ARRAY_STORE, ARRAY_LOAD,
        LABEL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public OpCode opCode;

    public IROperand[] operands;

    public int irLineNumber;

    public Block block;

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
    }

    public String getDestination() {
        return switch (opCode) {
            case ASSIGN, ADD, SUB, MULT, DIV, AND, OR, CALLR, ARRAY_LOAD -> operands[0].toString();
            case ARRAY_STORE -> operands[1].toString();
            default -> null;
        };
    }

    static HashMap<OpCode, MIPSOp> aluMap = new HashMap<>() {{
        put(OpCode.ADD, MIPSOp.ADD);
        put(OpCode.SUB, MIPSOp.SUB);
        put(OpCode.MULT, MIPSOp.MUL);
        put(OpCode.DIV, MIPSOp.DIV);
        put(OpCode.AND, MIPSOp.AND);
        put(OpCode.OR, MIPSOp.OR);
    }};

    static HashMap<OpCode, MIPSOp> aluiMap = new HashMap<>() {{
        put(OpCode.ADD, MIPSOp.ADDI);
        put(OpCode.AND, MIPSOp.ANDI);
        put(OpCode.OR, MIPSOp.ORI);
    }};

    static HashMap<OpCode, MIPSOp> branchMap = new HashMap<>() {{
        put(OpCode.BREQ, MIPSOp.BEQ);
        put(OpCode.BRNEQ, MIPSOp.BNE);
        put(OpCode.BRLT, MIPSOp.BLT);
        put(OpCode.BRGT, MIPSOp.BGT);
        put(OpCode.BRGEQ, MIPSOp.BGE);
    }};

    static Register v0 = Register.Physical.get("$v0");

    public class Selector {

        private String label;
        private String functionName;
        ArrayList<MIPSInstruction> list;

        public Selector(String label, String functionName) {
            this.label = label;
            this.functionName = functionName;
            this.list = new ArrayList<>();
        }

        private void append(MIPSOp op, String label, Block block, MIPSOperand... operands) {
            MIPSInstruction instruction = new MIPSInstruction(op, label, block, operands);
            list.add(instruction);
            block.mipsInst.add(instruction);
        }

        private Register initializeArrayOrDoNothing(IRVariableOperand arr) {
            Register register = Register.getVar(arr);
//            (IRArrayType)(arr.type)
            if (register != null) return register;
            append(MIPSOp.LI, label, block, Register.Physical.get("$a0"), new Imm((IRArrayType)(arr.type).getSize(), Imm.ImmType.INT));
            // insert sbrk syscall here
            return Register.Virtual.issueVar(arr);
        }

        public void compileCall(int start) {
            for (int i = start + 4; i < operands.length; i++) {
                Addr addr = new Addr(new Imm("" + (3 - i) * 4, Imm.ImmType.INT), Register.Physical.get("$sp"));
                if (operands[i] instanceof IRConstantOperand) {
                    Register.Physical a0 = Register.Physical.get("$a0");
                    append(MIPSOp.LI, null, block, a0, new Imm(operands[i].toString(), Imm.ImmType.INT));
                    append(MIPSOp.SW, null, block, a0, addr);
                } else {
                    append(MIPSOp.SW, null, block, Register.Virtual.getVar(operands[i]), addr);
                }
            }
            for (int i = start; i < Math.min(operands.length, start + 4); i++) {
                if (operands[i] instanceof IRVariableOperand)
                    append(MIPSOp.MOVE, i == start ? label : null, block, Register.Physical.get("$a" + (i-start)), Register.Virtual.getVar(operands[i]));
                else if (operands[i] instanceof IRConstantOperand)
                    append(MIPSOp.LI, i == start ? label : null, block, Register.Physical.get("$a" + (i-start)), new Imm(operands[i].toString(), Imm.ImmType.INT));
            }
            append(MIPSOp.JAL, operands.length == 0 ? label : null, block, new Addr(operands[1].toString()));
        }

        public void compile() {
            switch (opCode) {
                case ASSIGN -> {
                    if (((IRVariableOperand)operands[0]).type instanceof IRArrayType) {
                        Register base = initializeArrayOrDoNothing((IRVariableOperand)operands[0]);
                        for (int i = 0; i < Integer.parseInt(operands[1].toString()); i++) {
                            // insert sw instruction
                            Imm offset = new Imm("" + 4*i, Imm.ImmType.INT);
                            Addr address = new Addr(offset, base);
                            append(MIPSOp.SW, label, block, Register.Virtual.issueVar(operands[2]), address);
                        }
                    }
                    else if (operands[1] instanceof IRConstantOperand)
                        append(MIPSOp.LI, label, block, Register.Virtual.issueVar(operands[0]), new Imm(operands[1].toString(), Imm.ImmType.INT));
                    else
                        append(MIPSOp.MOVE, label, block, Register.Virtual.issueVar(operands[0]), Register.getVar(operands[1]));
                }
                case ADD, SUB, MULT, DIV, AND, OR -> {
                    if (operands[1] instanceof IRConstantOperand) {
                        IROperand temp = operands[1];
                        operands[1] = operands[2];
                        operands[2] = temp;
                    }
                    if (operands[2] instanceof IRConstantOperand) {
                        MIPSOperand sr = Register.Virtual.getVar(operands[1]);
                        append(aluiMap.get(opCode), label, block, Register.Virtual.issueVar(operands[0]), sr, new Imm(operands[2].toString(), Imm.ImmType.INT));
                    } else {
                        MIPSOperand sr1 = Register.getVar(operands[1]);
                        MIPSOperand sr2 = Register.getVar(operands[2]);
                        append(aluMap.get(opCode), label, block, Register.Virtual.issueVar(operands[0]), sr1, sr2);
                    }
                }
                case BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                    Register r1, r2;
                    boolean b1 = operands[1] instanceof IRConstantOperand;
                    boolean b2 = operands[2] instanceof IRConstantOperand;
                    if (b1) {
                        r1 = Register.Virtual.issueTemp();
                        append(MIPSOp.LI, label, block, r1, new Imm(operands[1].toString(), Imm.ImmType.INT));
                    } else r1 = Register.Virtual.getVar(operands[1]);
                    if (b2) {
                        r2 = Register.Virtual.issueTemp();
                        append(MIPSOp.LI, label, block, r2, new Imm(operands[2].toString(), Imm.ImmType.INT));
                    } else r2 = Register.Virtual.getVar(operands[2]);
                    append(branchMap.get(opCode), b1 || b2 ? null : label, block, r1, r2, new Addr(operands[0].toString()));
                }
                case GOTO -> append(MIPSOp.J, label, block, new Addr(operands[0].toString()));
                case RETURN -> {
                    if (operands[0] instanceof IRConstantOperand) {
                        append(MIPSOp.LI, label, block, v0, new Imm(operands[0].toString(), Imm.ImmType.INT));
                    } else if (operands[0] instanceof IRVariableOperand) {
                        append(MIPSOp.MOVE, label, block, v0, Register.Virtual.getVar(operands[0]));
                    }
                    append(MIPSOp.J, null, block, new Addr(functionName + "_teardown"));
                }
                case CALL -> {
                    if (operands[0] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[0]).getName().equals("puti")) {
                        append(MIPSOp.LI, label, block, v0, new Imm("1", Imm.ImmType.INT));
                        if (operands[1] instanceof IRConstantOperand)
                            append(MIPSOp.LI, null, block, Register.Physical.get("$a0"), new Imm(operands[1].toString(), Imm.ImmType.INT));
                        else if (operands[1] instanceof IRVariableOperand) {
                            append(MIPSOp.MOVE, null, block, Register.Physical.get("$a0"), Register.Virtual.getVar(operands[1]));
                        }
                        append(MIPSOp.SYSCALL, null, block);
                    } else if (operands[0] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[0]).getName().equals("putc")) {
                        append(MIPSOp.LI, label, block, v0, new Imm("11", Imm.ImmType.INT));
                        if (operands[1] instanceof IRConstantOperand)
                            append(MIPSOp.LI, null, block, Register.Physical.get("$a0"), new Imm(operands[1].toString(), Imm.ImmType.INT));
                        else if (operands[1] instanceof IRVariableOperand) {
                            append(MIPSOp.MOVE, null, block, Register.Physical.get("$a0"), Register.Virtual.issueVar(operands[1]));
                        }
                        append(MIPSOp.SYSCALL, null, block);
                    } else {
                        compileCall(1);
                    }
                }
                case CALLR -> {
                    if (operands[1] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[1]).getName().equals("geti")) {
                        append(MIPSOp.LI, label, block, v0, new Imm("5", Imm.ImmType.INT));
                        append(MIPSOp.SYSCALL, null, block);
                    } else {
                        compileCall(2);
                    }
                    append(MIPSOp.MOVE, null, block, Register.Virtual.issueVar(operands[0]), v0);
                }
                case ARRAY_LOAD -> {
                    Register base = initializeArrayOrDoNothing((IRVariableOperand)operands[1]);
                    Imm offset = new Imm("" + 4*Integer.parseInt(operands[2].toString()), Imm.ImmType.INT);
                    Addr address = new Addr(offset, base);
                    append(MIPSOp.LW, label, block, Register.Virtual.issueVar(operands[0]), address);
                }
                case ARRAY_STORE -> {
                    Register base = initializeArrayOrDoNothing((IRVariableOperand)operands[1]);
                    Imm offset = new Imm("" + 4*Integer.parseInt(operands[2].toString()), Imm.ImmType.INT);
                    Addr address = new Addr(offset, base);
                    append(MIPSOp.SW, label, block, Register.Virtual.getVar(operands[0]), address);
                }
                case null, default -> {}
            }
        }
    }

}
