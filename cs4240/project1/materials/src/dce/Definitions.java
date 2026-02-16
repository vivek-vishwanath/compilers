package dce;

import java.util.*;

import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;

public class Definitions {
    public static final HashSet<IRInstruction.OpCode> definitionInstructions = new HashSet<>(Arrays.asList(
        IRInstruction.OpCode.ASSIGN, IRInstruction.OpCode.ADD, IRInstruction.OpCode.SUB, IRInstruction.OpCode.MULT,
        IRInstruction.OpCode.DIV, IRInstruction.OpCode.AND, IRInstruction.OpCode.OR, IRInstruction.OpCode.CALLR,
        IRInstruction.OpCode.ARRAY_LOAD, IRInstruction.OpCode.ARRAY_STORE));

    public static void buildGen(ArrayList<Block> cfg) {
        for (Block block : cfg) block.buildGen();
    }

    public static void buildKill(IRProgram program) {
        for (IRFunction function : program.functions) {
            computeKillAll(function);
        }
    }

    private static void computeKillAll(IRFunction function) {
        for (int i = 0; i < function.instructions.size(); i++) {
            IRInstruction i1 = function.instructions.get(i);
            for (int j = i + 1; j < function.instructions.size(); j++) {
                IRInstruction i2 = function.instructions.get(j);
                boolean b = isDefinition(i1);
                boolean c = isDefinition(i2);
                boolean d = b && c;
                boolean e = i1.operands[0].toString().equals(i2.operands[0].toString());
                boolean f = d && e;
                if (f) {
                    i1.block.putKill(i1.operands[0].toString(), i2);
                    i2.block.putKill(i1.operands[0].toString(), i1);
                }
            }
        }
    }

    private static boolean isDefinition(IRInstruction instruction) {
        return definitionInstructions.contains(instruction.opCode);
    }
}
