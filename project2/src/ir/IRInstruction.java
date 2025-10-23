package ir;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
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

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
    }

    public boolean isALU() {
        return opCode == OpCode.ADD || opCode == OpCode.SUB || opCode == OpCode.MULT || opCode == OpCode.DIV ||
                opCode == OpCode.AND || opCode == OpCode.OR;
    }

    public boolean isBranch() {
        return opCode == OpCode.BREQ || opCode == OpCode.BRNEQ || opCode == OpCode.BRLT || opCode == OpCode.BRGT ||
                opCode == OpCode.BRGEQ;
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

    public static final int REGISTER_COUNT = 32;

    static Register v0 = Register.Physical.get("$v0");
    static Register ra = Register.Physical.get("$ra");

    public ArrayList<MIPSInstruction> compile(String label, String functionName) {
        ArrayList<MIPSInstruction> list = new ArrayList<>();
        switch (opCode) {
            case ASSIGN -> {
                if (operands[1] instanceof IRConstantOperand)
                    list.add(new MIPSInstruction(MIPSOp.LI, label, Register.Virtual.issueVar(operands[0]), new Imm(operands[1].toString(), Imm.ImmType.INT)));
                else
                    list.add(new MIPSInstruction(MIPSOp.MOVE, label, Register.Virtual.issueVar(operands[0]), Register.Virtual.issueVar(operands[1])));
            }
            case ADD, SUB, MULT, DIV, AND, OR -> {
                if (operands[1] instanceof IRConstantOperand) {
                    IROperand temp = operands[1];
                    operands[1] = operands[2];
                    operands[2] = temp;
                }
                if (operands[2] instanceof IRConstantOperand) {
                    MIPSOperand sr = Register.Virtual.getVar(operands[1]);
                    list.add(new MIPSInstruction(aluiMap.get(opCode), label, Register.Virtual.issueVar(operands[0]), sr, new Imm(operands[2].toString(), Imm.ImmType.INT)));
                } else {
                    MIPSOperand sr1 = Register.Virtual.getVar(operands[1]);
                    MIPSOperand sr2 = Register.Virtual.getVar(operands[2]);
                    list.add(new MIPSInstruction(aluMap.get(opCode), label, Register.Virtual.issueVar(operands[0]), sr1, sr2));
                }
            }
            case BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                Register r1, r2;
                boolean b1 = operands[1] instanceof IRConstantOperand;
                boolean b2 = operands[2] instanceof IRConstantOperand;
                if (b1) {
                    r1 = Register.Virtual.issueTemp();
                    list.add(new MIPSInstruction(MIPSOp.LI, label, r1, new Imm(operands[1].toString(), Imm.ImmType.INT)));
                } else r1 = Register.Virtual.getVar(operands[1]);
                if (b2) {
                    r2 = Register.Virtual.issueTemp();
                    list.add(new MIPSInstruction(MIPSOp.LI, label, r2, new Imm(operands[2].toString(), Imm.ImmType.INT)));
                } else r2 = Register.Virtual.getVar(operands[2]);
                list.add(new MIPSInstruction(branchMap.get(opCode), b1 || b2 ? null : label, r1, r2, new Addr(operands[0].toString())));
            }
            case GOTO -> list.add(new MIPSInstruction(MIPSOp.J, label, new Addr(operands[0].toString())));
            case RETURN -> {
                if (operands[0] instanceof IRConstantOperand) {
                    list.add(new MIPSInstruction(MIPSOp.LI, label, v0, new Imm(operands[0].toString(), Imm.ImmType.INT)));
                } else {
                    list.add(new MIPSInstruction(MIPSOp.LA, label, v0, Register.Virtual.getVar(operands[0])));
                }
                list.add(new MIPSInstruction(MIPSOp.J, null, new Addr(functionName + "_teardown")));
            }
            case CALL -> {
                if (operands[0] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[0]).getName().equals("puti")) {
                    list.add(new MIPSInstruction(MIPSOp.LI, label, v0, new Imm("1", Imm.ImmType.INT)));
                    if (operands[1] instanceof IRConstantOperand)
                        list.add(new MIPSInstruction(MIPSOp.LI, null, Register.Physical.get("$a0"), new Imm(operands[1].toString(), Imm.ImmType.INT)));
                    else if (operands[1] instanceof IRVariableOperand) {
                        list.add(new MIPSInstruction(MIPSOp.MOVE, null, Register.Physical.get("$a0"), Register.Virtual.getVar(operands[1])));
                    }
                    list.add(new MIPSInstruction(MIPSOp.SYSCALL, null));
                } else if (operands[0] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[0]).getName().equals("putc")) {
                    list.add(new MIPSInstruction(MIPSOp.LI, label, v0, new Imm("11", Imm.ImmType.INT)));
                    if (operands[1] instanceof IRConstantOperand)
                        list.add(new MIPSInstruction(MIPSOp.LI, null, Register.Physical.get("$a0"), new Imm(operands[1].toString(), Imm.ImmType.INT)));
                    else if (operands[1] instanceof IRVariableOperand) {
                        list.add(new MIPSInstruction(MIPSOp.MOVE, null, Register.Physical.get("$a0"), Register.Virtual.issueVar(operands[1])));
                    }
                    list.add(new MIPSInstruction(MIPSOp.SYSCALL, null));
                } else {
                    for (int i = 1; i < operands.length; i++)
                        list.add(new MIPSInstruction(MIPSOp.MOVE, i == 1 ? label : null, Register.Physical.get("$a" + (i - 1)), Register.Virtual.issueVar(operands[i])));
                    list.add(new MIPSInstruction(MIPSOp.JAL, operands.length > 1 ? null : label, new Addr(operands[0].toString())));
                }
            }
            case CALLR -> {
                if (operands[1] instanceof IRFunctionOperand && ((IRFunctionOperand) operands[1]).getName().equals("geti")) {
                    list.add(new MIPSInstruction(MIPSOp.LI, label, v0, new Imm("5", Imm.ImmType.INT)));
                    list.add(new MIPSInstruction(MIPSOp.SYSCALL, null));
                    list.add(new MIPSInstruction(MIPSOp.MOVE, null, Register.Virtual.issueVar(operands[0]), v0));
                } else {
                    for (int i = 2; i < operands.length; i++)
                        list.add(new MIPSInstruction(MIPSOp.MOVE, i == 2 ? label : null, Register.Physical.get("$a" + (i - 2)), Register.Virtual.issueVar(operands[i])));
                    list.add(new MIPSInstruction(MIPSOp.JAL, operands.length > 2 ? null : label, new Addr(operands[1].toString())));
                    list.add(new MIPSInstruction(MIPSOp.MOVE, null, Register.Virtual.issueVar(operands[0]), v0));
                }
            }
            case null, default -> {
            }
        }
        return list;
    }

}
