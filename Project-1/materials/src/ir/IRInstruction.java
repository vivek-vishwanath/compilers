package ir;

import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

public class IRInstruction {

    public OpCode opCode;

    public IROperand[] operands;

    public int irLineNumber;

    public boolean critical;

    public IRInstruction() {}

    public IRInstruction(OpCode opCode, IROperand[] operands, int irLineNumber) {
        this.opCode = opCode;
        this.operands = operands;
        this.irLineNumber = irLineNumber;
    }

    public String getVarOperand(int idx) {
        return idx < operands.length && operands[idx] instanceof IRVariableOperand ? ((IRVariableOperand) operands[idx]).getName() : null;
    }

    @Override
    public String toString() {
        return irLineNumber + ": " + String.format("%s %s", opCode, String.join(", ", getVarOperand(0), getVarOperand(1), getVarOperand(2)));
    }
}
