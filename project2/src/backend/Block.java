package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.operand.Register;
import ir.IRInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

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

    public void intraBlockAlloc() {
        HashSet<Register.Virtual> vRegSet = new HashSet<>();
        for (int i = 0; i < mipsInst.size(); i++) {
            Register.Virtual[] reads = (Register.Virtual[]) mipsInst.get(i).getReads();
            for (Register.Virtual vReg : reads) {
                vReg.end = i;
                vReg.readCount++;
                vRegSet.add(vReg);
            }
            Register.Virtual write = (Register.Virtual) mipsInst.get(i).getWrite();
            if (write != null) {
                write.start = i;
                write.end = i;
            }
        }
        ArrayList<Register.Virtual> vRegList = new ArrayList<>(vRegSet);
        for (int i = 0; i < vRegList.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (vRegList.get(i).start < vRegList.get(j).end && vRegList.get(i).end > vRegList.get(j).start) {
                    vRegList.get(i).concurrentAlives.add(vRegList.get(j));
                    vRegList.get(j).concurrentAlives.add(vRegList.get(i));
                }
            }
        }
        Collections.sort(vRegList);
        HashSet<Register.Physical> phsicalRegisterList = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            phsicalRegisterList.add(Register.Physical.get("$t" + i));
        }
        for (int i = 0; i < vRegList.size(); i++) {
            HashSet<Register.Physical> usedList = new HashSet<>();
            for (int j = 0; j < vRegList.size(); j++) {
                if (vRegList.get(i).concurrentAlives.contains(vRegList.get(j))) {
                    if (!vRegList.get(j).isSpilled) usedList.add(vRegList.get(i).physicalReg);
                }
            }
            HashSet<Register.Physical> copy = new HashSet<>(phsicalRegisterList);
            copy.removeAll(usedList);
            if (copy.size() > 0) {
                vRegList.get(i).physicalReg = copy.iterator().next();
            }
        }
    }
}
