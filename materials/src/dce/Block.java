package dce;

import ir.IRInstruction;

import java.util.ArrayList;
import java.util.HashMap;

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

    HashMap<String, IRInstruction> gen = new HashMap<>();
    HashMap<String, IRInstruction> kill = new HashMap<>();
    HashMap<String, IRInstruction> in = new HashMap<>();
    HashMap<String, IRInstruction> out = new HashMap<>();

    public boolean iterate() {
        HashMap<String, IRInstruction> temp = out;
        out = new HashMap<>();
        for (HashMap.Entry<String, IRInstruction> entry : in.entrySet()) {
            if (!kill.containsKey(entry.getKey())) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        out.putAll(gen);
        return out.equals(temp);
    }

    static int globalId = 0;

    public String toString() {
        return "Block_" + id + " [" + (next1 == null ? "" : next1.id) + " , " + (next2 == null ? "" : next2.id) + "]";
    }
}
