package backend.interpreter.mips.operand;

public class Register extends MIPSOperand {

    public int idx;

    public String oldName;

    public Register(int idx) {
        this.idx = idx;
    }

    public Register(String name) {
        setRegister(name);
    }

    public void setRegister(String name) {
        if (name.charAt(0) == '$') {
            try {
                this.idx = Integer.parseInt(name.substring(1));
                assert this.idx >= 32;
            } catch (NumberFormatException e) {
                assert name.matches("^\\$(zero|at|v[0-1]|a[0-3]|t[0-9]|s[0-7]|k[0-1]|gp|sp|fp|ra)$");
                this.idx = getIdx(name);
            }
        }
    }

    public static int getIdx(String name) {
        for (int i = 0; i < 32; i++) {
            if (("$" + registers[i]).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isVirtual() {
        return this.idx >= 32;
    }

    public String name() {
        return toString();
    }

    @Override
    public String toString() {
        return idx < 32 ? "$" + registers[idx] : "$" + idx;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Register r && r.idx == idx;
    }

    public boolean isNotT() {
        return idx < 8 || idx > 15 && idx < 24 || idx > 25;
    }

    public static String[] registers = {
            "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
    "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
    "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra"
    };
}
