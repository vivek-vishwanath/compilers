package ir.operand;

import ir.IRInstruction;
import ir.datatype.IRType;

public class IRVariableOperand extends IROperand {

    public IRType type;

    public IRVariableOperand(IRType type, String name, IRInstruction parent) {
        super(name, parent);
        this.type = type;
    }

    public String getName() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRVariableOperand && ((IRVariableOperand) obj).value.equals(value);
    }
}
