package dce;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import ir.IRFunction;
import ir.IRInstruction;

public class Definitions {
    public static final HashSet<IRInstruction.OpCode> definitionInstructions = new HashSet<>(Arrays.asList(
        IRInstruction.OpCode.ASSIGN, IRInstruction.OpCode.ADD, IRInstruction.OpCode.SUB, IRInstruction.OpCode.MULT,
        IRInstruction.OpCode.DIV, IRInstruction.OpCode.AND, IRInstruction.OpCode.OR, IRInstruction.OpCode.CALLR,
        IRInstruction.OpCode.ARRAY_LOAD, IRInstruction.OpCode.ARRAY_STORE));
    

    private static HashMap<String, IRInstruction> computeGen(Block basicBlock) {
        HashMap<String, IRInstruction> gen = new HashMap<>();
        for (IRInstruction instruction : basicBlock.instructions) {
            if (definitionInstructions.contains(instruction.opCode)) {
                gen.put(instruction.operands[0].toString(), instruction);
            }
        }
        return gen;
    }

    private static void computeKillAll(IRFunction function) {
        for (IRInstruction instruction1 : function.instructions) {
            for (IRInstruction instruction2 : function.instructions) {
                if (isDefinition(instruction1) && isDefinition(instruction2) 
                && instruction1.operands[0].toString().equals(instruction2.operands[0].toString())) {
                    instruction1.block.kill.get(instruction1.operands[0].toString()).add(instruction2);
                    instruction2.block.kill.get(instruction1.operands[0].toString()).add(instruction1);
                }
            }
        }
    }

    private static boolean isDefinition(IRInstruction instruction) {
        return definitionInstructions.contains(instruction);
    }
}
