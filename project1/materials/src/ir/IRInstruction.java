package ir;

import dce.Block;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.*;

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
        return switch (opCode) {
            case ASSIGN, ADD, SUB, MULT, DIV, AND, OR, CALLR, ARRAY_LOAD -> operands[0].toString();
            case ARRAY_STORE -> operands[1].toString();
            default -> null;
        };
    }

    public ArrayList<String> getSources() {
        int start, end;
        switch(opCode) {
            case ASSIGN: // 2 cases? 
                start = 1;
                end = operands.length - 1;
                break;
            case ADD, SUB, MULT, DIV, AND, OR, BREQ, BRGEQ, BRGT, BRLT, BRNEQ, ARRAY_LOAD:
                start = 1;
                end = 2;
                break;
            case RETURN:{
                start = 0;
                end = 0;
                break;
            }
            case CALL: {
                start = 1;
                end = operands.length - 1;
                break;
            }
            case CALLR: {
                start = 2;
                end = operands.length - 1;
                break;
            }
            // code ...
            case ARRAY_STORE:
                ArrayList<String> list = new ArrayList<>();
                if (operands[0] instanceof IRVariableOperand) list.add(operands[0].toString());
                if (operands[2] instanceof IRVariableOperand) list.add(operands[2].toString());
                return list;

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

    public HashMap<String, ArrayList<IRInstruction>> getReachingDefinitions() {
        Block block = this.block;
        HashMap<String, ArrayList<IRInstruction>> reaching = new HashMap<>();
        for (IRInstruction i : block.in) {
            if (getSources().contains(i.getDestination())) {
                ArrayList<IRInstruction> list = reaching.computeIfAbsent(i.getDestination(), k -> new ArrayList<>());
                list.add(i);
            }
        }
        for (IRInstruction i : block.instructions) {
            if (i == this) break;
            String dest = i.getDestination();
            if (dest == null) continue;
            if (getSources().contains(dest))
                reaching.put(dest, new ArrayList<>(Collections.singletonList(i)));
        }
        return reaching;
    }
}
