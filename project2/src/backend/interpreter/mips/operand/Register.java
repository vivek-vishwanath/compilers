package backend.interpreter.mips.operand;

import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class Register extends MIPSOperand {

    public int idx;

    private Register(int idx) {
        this.idx = idx;
    }

    public abstract String name();

    public static HashMap<IRVariableOperand, Register> map = new HashMap<>();

    public static void clear() {
        map = new HashMap<>();
    }

    public static int numRegs() {
        return map.entrySet().stream().filter(it -> it.getValue() instanceof Virtual).collect(Collectors.toSet()).size();
    }

    public static Register getVar(IROperand var) {
        Register r = map.getOrDefault(var, null);
        return r;
    }

    public static class Virtual extends Register implements Comparable<Register.Virtual> {

        public IRVariableOperand var;
        public Snapshot start;
        public Snapshot end;
        public int readCount;
        public HashSet<Register.Virtual> concurrentAlives;
        public boolean isSpilled;
        public boolean noWrite;
        public Register.Physical physicalReg;

        public void reset() {
            start = null;
            end = null;
            readCount = 0;
            concurrentAlives = new HashSet<>();
            isSpilled = false;
            noWrite = false;
            physicalReg = null;
        }

        public Virtual(int idx) {
            super(idx);
            reset();
        }
        private static ArrayList<Virtual> vregs = new ArrayList<>();
        private static int globalIdx = 0;

        public static Virtual issueTemp() {
            Virtual vreg = new Virtual(globalIdx++);
            vregs.add(vreg);
            return vreg;
        }

        public static Virtual issueVar(IROperand var) {
            Virtual reg = issueTemp();
            reg.var = (IRVariableOperand) var;
            map.put(reg.var, reg);
            return reg;
        }

        @Override
        public String name() {
            return "$" + idx;
        }

        @Override
        public String toString() {
            return name();
        }

        @Override
        public int compareTo(Register.Virtual that) {
            return that.readCount - this.readCount;
        }

        public static class Snapshot implements Comparable<Snapshot> {
            public int idx;
            public boolean after;

            public Snapshot(int idx, boolean after) {
                this.idx = idx;
                this.after = after;
            }

            @Override
            public int compareTo(Snapshot other) {
                return Integer.compare(this.idx * 2 + (this.after ? 1 : 0), other.idx * 2 + (other.after ? 1 : 0));
            }
        }
    }

    public static class Physical extends Register {

        String name;

        private Physical(int idx, String name) {
            super(idx);
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        public static void putReg(IRVariableOperand name, Physical reg) {
            map.put(name, reg);
        }

        public static void putReg(IRVariableOperand name, String reg) {
            putReg(name, get(reg));
        }

        public static Physical get(String name) {
            for (int i = 0; i < 32; i++) {
                if (name.equals(registers[i].name)) return registers[i];
            }
            throw new IllegalArgumentException("Invalid physical register name: " + name);
        }

        public static Physical[] registers = new Physical[32];

        public static String[] regNames = {
                "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
                "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra"
        };

        static {
            for (int i = 0; i < 32; i++) registers[i] = new Physical(i, "$" + regNames[i]);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
