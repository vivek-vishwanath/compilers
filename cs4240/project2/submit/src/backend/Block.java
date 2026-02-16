package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
import backend.interpreter.mips.operand.Register.Virtual;
import backend.interpreter.mips.operand.Register.Virtual.Range;
import ir.IRFunction;
import ir.IRInstruction;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Block {

    public ArrayList<IRInstruction> irInst = new ArrayList<>();
    public ArrayList<MIPSInstruction> mipsInst = new ArrayList<>();

    int id = Block.globalId++;

    public Block next1;
    public Block next2;

    HashSet<Virtual> defs = new HashSet<>();
    HashSet<Virtual> uses = new HashSet<>();
    public HashSet<Virtual> liveIns = new HashSet<>();
    public HashSet<Virtual> liveOuts = new HashSet<>();

    static int globalId = 0;

    public String toString() {
        return "Block_" + id + " [" + (next1 == null ? "" : next1.id) + " , " + (next2 == null ? "" : next2.id) + "]";
    }

    public void intraBlockAlloc(IRFunction.MStack stack) {
        HashSet<Virtual> vRegSet = new HashSet<>();
        for (MIPSInstruction mipsInstruction : mipsInst) {
            Register[] reads = mipsInstruction.getReads();
            Register write = mipsInstruction.getWrite();
            for (Register reg : reads) {
                if (reg instanceof Virtual vReg) {
                    vReg.reset();
                }
            }
            if (write instanceof Virtual vReg) {
                vReg.reset();
            }
        }
        for (int i = 0; i < mipsInst.size(); i++) {
            Register[] reads = mipsInst.get(i).getReads();
            for (Register reg : reads) {
                if (!(reg instanceof Virtual vReg)) continue;
                if (vReg.start == null) {
                    vReg.start = new Virtual.Snapshot(i, false);
                    vReg.noWrite = true;
                }
                vReg.end = new Virtual.Snapshot(i, true);
                vReg.readCount++;
                vRegSet.add(vReg);
            }
            Register write = mipsInst.get(i).getWrite();
            if (write instanceof Virtual vReg) {
                vReg.start = new Virtual.Snapshot(i, true);
                vReg.end = new Virtual.Snapshot(i + 1, false);
                vRegSet.add(vReg);
            }
        }
        if (vRegSet.isEmpty()) return;
        ArrayList<Virtual> vRegList = new ArrayList<>(vRegSet);
        for (int i = 0; i < vRegList.size(); i++) {
            for (int j = 0; j < i; j++) {
                Virtual reg1 = vRegList.get(i);
                Virtual reg2 = vRegList.get(j);
                if (reg1.start.compareTo(reg2.end) < 0 && reg1.end.compareTo(reg2.start) > 0) {
                    reg1.concurrentAlives.add(reg2);
                    reg2.concurrentAlives.add(reg1);
                }
            }
        }
        Collections.sort(vRegList);
        HashSet<Register.Physical> physicalRegs = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            physicalRegs.add(Register.Physical.get("$t" + i));
        }
        for (int i = 0; i < vRegList.size(); i++) {
            Virtual reg1 = vRegList.get(i);
            HashSet<Register.Physical> usedList = new HashSet<>();
            for (int j = 0; j < i; j++) {
                Virtual reg2 = vRegList.get(j);
                if (reg1.concurrentAlives.contains(reg2)) {
                    if (!reg2.isSpilled)
                        usedList.add(reg2.physicalReg);
                }
            }
            ArrayList<Register.Physical> copy = new ArrayList<>(physicalRegs);
            copy.removeAll(usedList);
            if (!copy.isEmpty()) {
                reg1.physicalReg = copy.get(0);
            } else {
                reg1.isSpilled = true;
            }
        }
        for (MIPSInstruction inst : mipsInst) {
            List<MIPSOperand> operands = inst.operands;
            for (int i = 0; i < operands.size(); i++) {
                if (operands.get(i) instanceof Virtual vreg && !vreg.isSpilled) {
                    operands.set(i, vreg.physicalReg);
                }
                if (operands.get(i) instanceof Addr addr && addr.register instanceof Virtual vreg && !vreg.isSpilled) {
                    if (addr.mode == Addr.Mode.BASE_OFFSET) {
                        operands.set(i, new Addr(addr.constant, vreg.physicalReg));
                    } else if (addr.mode == Addr.Mode.REGISTER) {
                        operands.set(i, new Addr(vreg.physicalReg));
                    }
                }
            }
        }
        vRegList.sort(Comparator.comparing(v -> -(v.start.idx * 2 + (v.start.after ? 1 : 0))));
        for (Virtual vreg : vRegList) {
            if (vreg.noWrite) {
                String label = mipsInst.get(vreg.start.idx).label;
                mipsInst.get(vreg.start.idx).label = null;
                mipsInst.add(vreg.start.idx, new MIPSInstruction(MIPSOp.LW, label, this, vreg.physicalReg, stack.get(vreg)));
            } else {
                if (vreg.var != null && !vreg.isSpilled)
                    mipsInst.add(vreg.start.idx + 1, new MIPSInstruction(MIPSOp.SW, null, this, vreg.physicalReg, stack.get(vreg)));
            }
        }
    }

    public ArrayList<Virtual> livenessAlloc() {
        HashSet<Virtual> vRegSet = new HashSet<>();
        for (Virtual vreg : liveIns) {
            vRegSet.add(vreg);
            vreg.ranges.add(new Range(new Virtual.Snapshot(0, false), null));
        }
        for (int i = 0; i < mipsInst.size(); i++) {
            Register[] reads = mipsInst.get(i).getReads();
            for (Register reg : reads) {
                if (!(reg instanceof Virtual vReg)) continue;
                Range last = vReg.ranges.get(vReg.ranges.size() - 1);
                last.end = new Virtual.Snapshot(i, true);
                vReg.readCount++;
            }
            Register write = mipsInst.get(i).getWrite();
            if (write instanceof Virtual vReg) {
                vReg.ranges.add(new Range(new Virtual.Snapshot(i, true), null));
                vRegSet.add(vReg);
            }
        }
        for (Virtual vreg : liveOuts) {
            Range last = vreg.ranges.get(vreg.ranges.size() - 1);
            last.end = new Virtual.Snapshot(mipsInst.size(), false);
        }
        if (vRegSet.isEmpty()) return new ArrayList<>();
        ArrayList<Virtual> vRegList = new ArrayList<>(vRegSet);
        for (int i = 0; i < vRegList.size(); i++) {
            for (int j = 0; j < i; j++) {
                Virtual reg1 = vRegList.get(i);
                Virtual reg2 = vRegList.get(j);
                int a = 0, b = 0;
                while (a < reg1.ranges.size() && b < reg2.ranges.size()) {
                    Range r1 = reg1.ranges.get(a);
                    Range r2 = reg2.ranges.get(b);
                    if (r1.end == null || r1.end.compareTo(r2.start) < 0) {
                        a++;
                    } else if (r2.end == null || r1.start.compareTo(r2.end) > 0) {
                        b++;
                    } else {
                        reg1.concurrentAlives.add(reg2);
                        reg2.concurrentAlives.add(reg1);
                        break;
                    }
                }
            }
        }
        Collections.sort(vRegList);
        for (Register.Virtual vreg : vRegList) {
            vreg.ranges.clear();
        }
        return vRegList;
    }

    public void print(FileWriter writer) {
        mipsInst.forEach(it -> {
            try {
                if (it.label != null) {
                    writer.write(it.label + ":\n");
                }
                writer.write("\t\t" + it + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void computeUseDef() {
        HashSet<Virtual> killed = new HashSet<>();
        for (MIPSInstruction inst : mipsInst) {
            Register dest = inst.getWrite();
            Register[] sources = inst.getReads();
            if (dest instanceof Virtual)
                defs.add((Virtual) dest);
            for (Register source : sources) {
                if (source instanceof Virtual && !killed.contains((Virtual) source))
                    uses.add((Virtual) source);
            }
            if (dest instanceof Virtual)
                killed.add((Virtual) dest);
        }
    }

    public void clearLiveSets() {
        liveIns.clear();
        liveOuts.clear();
    }

    public boolean livenessAnalysis() {
        HashSet<Virtual> oldIn = liveIns;
        HashSet<Virtual> oldOut = liveOuts;
        liveIns = new HashSet<>();
        liveOuts = new HashSet<>();

        if (next1 != null) liveOuts.addAll(next1.liveIns);
        if (next2 != null) liveOuts.addAll(next2.liveIns);
        liveIns.addAll(liveOuts);
        liveIns.removeAll(defs);
        liveIns.addAll(uses);

        return oldIn.equals(liveIns) && oldOut.equals(liveOuts);
    }
}
