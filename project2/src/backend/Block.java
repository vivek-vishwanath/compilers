package backend;

import backend.interpreter.mips.MIPSInstruction;
import ir.IRInstruction;

import java.util.ArrayList;

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
}
