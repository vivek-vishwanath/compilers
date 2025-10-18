package backend.interpreter.mips.operand;

import static backend.interpreter.mips.operand.Imm.ImmType.DOUBLE_FLOAT;
import static backend.interpreter.mips.operand.Imm.ImmType.SINGLE_FLOAT;

public class Imm extends MIPSOperand {

    private String val;
    private ImmType type;

    public Imm(String val, ImmType type) {
        this.val = val;
        this.type = type;
    }

    public int getInt() {
        if (this.val.startsWith("0x")) return Integer.parseInt(this.val.substring(2), 16);
        else return Integer.parseInt(this.val);
    }

    public enum ImmType {
        INT, SINGLE_FLOAT, DOUBLE_FLOAT;
    }
    public float getSingle() {
        if (type == SINGLE_FLOAT) {
            return Float.parseFloat(val);
        }
        throw new IllegalArgumentException();
    }

    public double getDouble() {
        if (type == DOUBLE_FLOAT) {
            return Double.parseDouble(val);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return val;
    }
}
