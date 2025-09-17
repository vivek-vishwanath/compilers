package dce;

import ir.*;
import ir.operand.IROperand;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        HashSet<IRInstruction> leaders = findLeaders(program);
        ArrayList<Block> blocks = buildBlocks(program, leaders);
        buildCFG(blocks);
        System.out.println(blocks);
    }

    private static void optimizeProgram(IRProgram program) {
        for (IRFunction function : program.functions) {
            optimizeFunction(function);
        }
    }

    //
    private static void optimizeFunction(IRFunction function) {
        HashSet<IRInstruction> marked = new HashSet<>();
        LinkedList<IRInstruction> workList = getCriticalInstructions(function, marked);
        while (!workList.isEmpty()) {
            IRInstruction instruction = workList.pop();
            addReachingDefinitions(instruction, marked, workList);
        }
        for (IRInstruction instruction : function.instructions) {
            if (!marked.contains(instruction)) {
                function.instructions.remove(instruction);
            }
        }
    }

    // Get initial list
    private static LinkedList<IRInstruction> getCriticalInstructions(IRFunction function, HashSet<IRInstruction> marked) {
        LinkedList<IRInstruction> criticalInstructions = new LinkedList<>();
        HashSet<IRInstruction.OpCode> critical = new HashSet<>(Arrays.asList(
            IRInstruction.OpCode.BREQ, IRInstruction.OpCode.BRGEQ, IRInstruction.OpCode.BRGT, IRInstruction.OpCode.BRLT, IRInstruction.OpCode.BRNEQ,
            IRInstruction.OpCode.GOTO, IRInstruction.OpCode.ARRAY_STORE, IRInstruction.OpCode.RETURN, IRInstruction.OpCode.ASSIGN, IRInstruction.OpCode.CALL,
            IRInstruction.OpCode.CALLR, IRInstruction.OpCode.LABEL));
        for (IRInstruction instruction : function.instructions) {
            if (critical.contains(instruction.opCode)) {
                criticalInstructions.add(instruction);
            }
            marked.add(instruction);
        }
        return criticalInstructions;
    }
        //
    private static void addReachingDefinitions(IRInstruction instruction, HashSet<IRInstruction> marked, LinkedList<IRInstruction> workList) {
        LinkedList<IRInstruction> reachingDefinitions = getReachingDefinitions(instruction);
        for (IRInstruction reachingInstruction : reachingDefinitions) {
            if (!marked.contains(reachingInstruction)) {
                marked.add(reachingInstruction);
                workList.add(reachingInstruction);
            }
        }
    }

    private static LinkedList<IRInstruction> getReachingDefinitions(IRInstruction instruction) {
        return new LinkedList<IRInstruction>();
    }

    private static computeSetDifference() {

    }

    static HashSet<IRInstruction> findLeaders(IRProgram program) {
        HashSet<IRInstruction> leaders = new HashSet<>();
        for (IRFunction func : program.functions) {
            for (int i = 0; i < func.instructions.size(); i++) {
                IRInstruction inst = func.instructions.get(i);
                IROperand operand = (inst.opCode == IRInstruction.OpCode.CALLR) ? inst.operands[1] : inst.operands[0];
                switch (inst.opCode) {
                    case GOTO:
                        inst.branch1 = func.labelMap.get(operand.toString());
                        leaders.add(inst.branch1);
                        break;
                    case BREQ:
                    case BRNEQ:
                    case BRLT:
                    case BRGT:
                    case BRGEQ:
                        inst.branch1 = (i + 1 < func.instructions.size()) ? func.instructions.get(i + 1) : null;
                        inst.branch2 = func.labelMap.get(operand.toString());
                        leaders.add(inst.branch1);
                        leaders.add(inst.branch2);
                        break;
                    case CALL:
                    case CALLR:
                        inst.branch1 = (i + 1 < func.instructions.size()) ? func.instructions.get(i + 1) : null;
                        List<IRInstruction> instructions = program.functionMap.get(operand.toString()).instructions;
                        inst.branch2 = instructions != null ? instructions.get(0) : null;
                        leaders.add(inst.branch1);
                        break;
                    default:
                        inst.branch1 = (i + 1 < func.instructions.size()) ? func.instructions.get(i + 1) : null;
                        break;
                }
            }
        }
        return leaders;
    }

    static ArrayList<Block> buildBlocks(IRProgram program, HashSet<IRInstruction> leaders) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (IRFunction func : program.functions) {
            Block block = new Block();
            for (IRInstruction inst : func.instructions) {
                if (leaders.contains(inst)) {
                    blocks.add(block);
                    block = new Block();
                }
                inst.block = block;
                block.instructions.add(inst);
            }
            blocks.add(block);
        }
        return blocks;
    }

    static void buildCFG(ArrayList<Block> blocks) {
        for (Block block : blocks) {
            block.next1 = block.branch().branch1 == null ? null : block.branch().branch1.block;
            block.next2 = block.branch().branch2 == null ? null : block.branch().branch2.block;
        }
    }



    private static HashMap<String, IRInstruction> computeKill(IRFunction) {
        
    }
}
