package main.java.mips.operand;

public class Register extends MIPSOperand {

    public String name;
    public boolean isVirtual;

    public Register(String name) {
        this(name, true);
    }

    public Register(String name, boolean isVirtual) {
        this.name = name;
        this.isVirtual = isVirtual;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Register && ((Register) other).name.equals(name);
    }
}
