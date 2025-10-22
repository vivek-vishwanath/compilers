package ir;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
import ir.operand.IRConstantOperand;
import ir.operand.IROperand;

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

    static HashMap<String, Integer> virtualRegs = new HashMap<>();
    public static int vregCount = 32;

    static Register v0 = new Register("$v0");
    static Register ra = new Register("$ra");

    public Register getVRegister(IROperand operand) {
        String identifier = operand.toString();
        if (!virtualRegs.containsKey(identifier)) {
            virtualRegs.put(identifier, vregCount++);
        }
        return new Register("$" + virtualRegs.get(identifier));
    }

    public Register issueVRegister(IROperand operand) {
        String identifier = operand.toString();
        virtualRegs.put(identifier, vregCount);
        return new Register("$" + vregCount++);
    }

    public ArrayList<MIPSInstruction> compile(String label) {
        ArrayList<MIPSInstruction> list = new ArrayList<>();
        switch (opCode) {
            case ASSIGN -> {
                if (operands[1] instanceof IRConstantOperand)
                    list.add(new MIPSInstruction(MIPSOp.LI, label, issueVRegister(operands[0]), new Imm(operands[1].toString(), Imm.ImmType.INT)));
                else
                    list.add(new MIPSInstruction(MIPSOp.MOVE, label, issueVRegister(operands[0]), getVRegister(operands[1])));
            }
            case ADD, SUB, MULT, DIV, AND, OR -> {
                if (operands[1] instanceof IRConstantOperand) {
                    IROperand temp = operands[1];
                    operands[1] = operands[2];
                    operands[2] = temp;
                }
                if (operands[2] instanceof IRConstantOperand) {
                    MIPSOperand sr = getVRegister(operands[1]);
                    list.add(new MIPSInstruction(aluiMap.get(opCode), label, issueVRegister(operands[0]), sr, new Imm(operands[2].toString(), Imm.ImmType.INT)));
                } else {
                    MIPSOperand sr1 = getVRegister(operands[1]);
                    MIPSOperand sr2 = getVRegister(operands[2]);
                    list.add(new MIPSInstruction(aluMap.get(opCode), label, issueVRegister(operands[0]), sr1, sr2));
                }
            }
            case BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                Register r1, r2;
                boolean b1 = operands[1] instanceof IRConstantOperand;
                boolean b2 = operands[2] instanceof IRConstantOperand;
                if (b1) {
                    r1 = new Register("$" + vregCount++);
                    list.add(new MIPSInstruction(MIPSOp.LI, label, r1, new Imm(operands[1].toString(), Imm.ImmType.INT)));
                } else r1 = getVRegister(operands[1]);
                if (b2) {
                    r2 = new Register("$" + vregCount++);
                    list.add(new MIPSInstruction(MIPSOp.LI, label, r2, new Imm(operands[2].toString(), Imm.ImmType.INT)));
                } else r2 = getVRegister(operands[2]);
                list.add(new MIPSInstruction(branchMap.get(opCode), b1 || b2 ? null : label, r1, r2, new Addr(operands[0].toString())));
            }
            case GOTO -> list.add(new MIPSInstruction(MIPSOp.J, label, new Addr(operands[0].toString())));
            case RETURN -> {
                if (operands[0] instanceof IRConstantOperand) {
                    list.add(new MIPSInstruction(MIPSOp.LI, label, v0, new Imm(operands[0].toString(), Imm.ImmType.INT)));
                } else {
                    list.add(new MIPSInstruction(MIPSOp.LA, label, v0, getVRegister(operands[0])));
                }
                list.add(new MIPSInstruction(MIPSOp.JR, null, ra));
            }
            case CALL -> {
                for (int i = 1; i < operands.length; i++)
                    list.add(new MIPSInstruction(MIPSOp.MOVE, i == 1 ? label : null, new Register("$a" + (i - 1)), getVRegister(operands[i])));
                list.add(new MIPSInstruction(MIPSOp.JAL, null, new Addr(operands[0].toString())));
            }
            case CALLR -> {
                for (int i = 2; i < operands.length; i++)
                    list.add(new MIPSInstruction(MIPSOp.MOVE, i == 2 ? label : null, new Register("$a" + (i - 2)), getVRegister(operands[i])));
                list.add(new MIPSInstruction(MIPSOp.JAL, null, new Addr(operands[1].toString())));
                list.add(new MIPSInstruction(MIPSOp.MOVE, null, issueVRegister(operands[0]), v0));
            }
            case null, default -> {
            }
        }
        return list;
    }

}
