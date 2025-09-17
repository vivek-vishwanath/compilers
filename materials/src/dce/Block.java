package dce;

import ir.IRInstruction;

import java.util.ArrayList;

public class Block {

    ArrayList<IRInstruction> instructions = new ArrayList<>();

    public IRInstruction leader() {
        return instructions.get(0);
    }

    public IRInstruction branch() {
        return instructions.get(instructions.size() - 1);
    }

    int id = Block.globalId++;

    Block next1;
    Block next2;

    static int globalId = 0;

    public String toString() {
        return "Block_" + id + " [" + (next1 == null ? "" : next1.id) + " , " + (next2 == null ? "" : next2.id) + "]";
    }
}
