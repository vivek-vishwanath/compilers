package ir;

import dce.Block;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.ArrayList;
import java.util.Arrays;

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

    public IRInstruction branch1, branch2;

    public Block block;

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
    }

    @Override
    public String toString() {
        return String.format("%d: %s %s", irLineNumber, opCode, Arrays.toString(operands));
    }

    public String getDestination() {
        switch(opCode) {
            case OpCode.ASSIGN:
            case OpCode.ADD:
            case OpCode.SUB:
            case OpCode.MULT:
            case OpCode.DIV:
            case OpCode.AND:
            case OpCode.OR:
            case OpCode.CALLR:
            case OpCode.ARRAY_LOAD:
                return operands[0].toString();
            case OpCode.ARRAY_STORE:
                return operands[1].toString();
            default:
                return null;
        }
    }

    public ArrayList<String> getSources() {
        int start = 0, end = 0;
        switch(opCode) {
            case OpCode.ASSIGN: // 2 cases? 
                start = 1;
                end = operands.length - 1;
                break;
            case OpCode.ADD:
            case OpCode.SUB:
            case OpCode.MULT:
            case OpCode.DIV:
            case OpCode.AND:
            case OpCode.OR:
            case OpCode.BREQ:
            case OpCode.BRGEQ:
            case OpCode.BRGT:
            case OpCode.BRLT:
            case OpCode.BRNEQ:
                start = 1;
                end = 2;
                break;
            case OpCode.RETURN:{
                start = 0;
                end = 0;
                break;
            }
            case OpCode.CALL: {
                start = 1;
                end = operands.length - 1;
                break;
            }
            case OpCode.CALLR: {
                start = 2;
                end = operands.length - 1;
                break;
            }
            // code ...
            case OpCode.ARRAY_STORE:
                ArrayList<String> list = new ArrayList<>();
                if (operands[0] instanceof IRVariableOperand) list.add(operands[0].toString());
                if (operands[2] instanceof IRVariableOperand) list.add(operands[2].toString());
                return list;

            case OpCode.ARRAY_LOAD:
                start = 1;
                end = 2;
                break;

            default:
                return new ArrayList<>();
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (operands[i] instanceof IRVariableOperand) {
                list.add(operands[i].toString());
            }
        }
        return list;
    }
}
