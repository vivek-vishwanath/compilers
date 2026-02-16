package dce;

import ir.IRInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Block {

    public ArrayList<IRInstruction> instructions = new ArrayList<>();

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
    HashMap<String, ArrayList<IRInstruction>> kill = new HashMap<>();
    public HashSet<IRInstruction> in = new HashSet<>();
    HashSet<IRInstruction> out = new HashSet<>();

    void buildGen() {
        for (IRInstruction instruction : instructions) {
            if (Definitions.definitionInstructions.contains(instruction.opCode)) {
                gen.put(instruction.operands[0].toString(), instruction);
            }
        }
    }

    void putKill(String variable, IRInstruction inst) {
        if (!kill.containsKey(variable)) {
            kill.put(variable, new ArrayList<>());
        }
        kill.get(variable).add(inst);
    }

    public boolean iterate() {
        HashSet<IRInstruction> temp = out;
        out = new HashSet<>();
        for (IRInstruction  entry : in) {
            if (!kill.containsKey(entry.getDestination())) {
                out.add(entry);
            }
        }
        for (HashMap.Entry<String, IRInstruction> entry : gen.entrySet()) {
            out.add(entry.getValue());
        }
        if (next1 != null) next1.in.addAll(out);
        if (next2 != null) next2.in.addAll(out);
        return out.equals(temp);
    }

    static int globalId = 0;

    public String toString() {
        return "Block_" + id + " [" + (next1 == null ? "" : next1.id) + " , " + (next2 == null ? "" : next2.id) + "]";
    }
}
