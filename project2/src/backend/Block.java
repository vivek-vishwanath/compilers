package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.MIPSOperand;
import backend.interpreter.mips.operand.Register;
import ir.IRFunction;
import ir.IRInstruction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Block {

    public ArrayList<IRInstruction> irInst = new ArrayList<>();
    public ArrayList<MIPSInstruction> mipsInst = new ArrayList<>();

    int id = Block.globalId++;

    Block next1;
    Block next2;

    static int globalId = 0;

    public String toString() {
        return "Block_" + id + " [" + (next1 == null ? "" : next1.id) + " , " + (next2 == null ? "" : next2.id) + "]";
    }

    public void intraBlockAlloc(IRFunction.MStack stack) {
        HashSet<Register.Virtual> vRegSet = new HashSet<>();
        for (MIPSInstruction mipsInstruction : mipsInst) {
            Register[] reads = mipsInstruction.getReads();
            Register write = mipsInstruction.getWrite();
            for (Register reg : reads) {
                if (reg instanceof Register.Virtual vReg) {
                    vReg.reset();
                }
            }
            if (write instanceof Register.Virtual vReg) {
                vReg.reset();
            }
        }
        for (int i = 0; i < mipsInst.size(); i++) {
            Register[] reads = mipsInst.get(i).getReads();
            for (Register reg : reads) {
                if (!(reg instanceof Register.Virtual vReg)) continue;
                if (vReg.start == null) {
                    vReg.start = new Register.Virtual.Snapshot(i, false);
                    vReg.noWrite = true;
                }
                vReg.end = new Register.Virtual.Snapshot(i, true);
                vReg.readCount++;
                vRegSet.add(vReg);
            }
            Register write = mipsInst.get(i).getWrite();
            if (write instanceof Register.Virtual vReg) {
                vReg.start = new Register.Virtual.Snapshot(i, true);
                vReg.end = new Register.Virtual.Snapshot(i + 1, false);
                vRegSet.add(vReg);
            }
        }
        if (vRegSet.isEmpty()) return;
        ArrayList<Register.Virtual> vRegList = new ArrayList<>(vRegSet);
        for (int i = 0; i < vRegList.size(); i++) {
            for (int j = 0; j < i; j++) {
                Register.Virtual reg1 = vRegList.get(i);
                Register.Virtual reg2 = vRegList.get(j);
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
            Register.Virtual reg1 = vRegList.get(i);
            HashSet<Register.Physical> usedList = new HashSet<>();
            for (int j = 0; j < i; j++) {
                Register.Virtual reg2 = vRegList.get(j);
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
                if (operands.get(i) instanceof Register.Virtual vreg) {
                    operands.set(i, vreg.physicalReg);
                }
                if (operands.get(i) instanceof Addr addr && addr.register instanceof Register.Virtual vreg) {
                    if (addr.mode == Addr.Mode.BASE_OFFSET) {
                        operands.set(i, new Addr(addr.constant, vreg.physicalReg));
                    } else if (addr.mode == Addr.Mode.REGISTER) {
                        operands.set(i, new Addr(vreg.physicalReg));
                    }
                }
            }
        }
        vRegList.sort(Comparator.comparing(v -> -(v.start.idx * 2 + (v.start.after ? 1 : 0))));
        for (Register.Virtual vreg : vRegList) {
            if (vreg.noWrite) {
                String label = mipsInst.get(vreg.start.idx).label;
                mipsInst.get(vreg.start.idx).label = null;
                mipsInst.add(vreg.start.idx, new MIPSInstruction(MIPSOp.LW, label, this, vreg.physicalReg, stack.get(vreg)));
            } else {
                if (vreg.var != null)
                    mipsInst.add(vreg.start.idx + 1, new MIPSInstruction(MIPSOp.SW, null, this, vreg.physicalReg, stack.get(vreg)));
            }
        }
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
}
