package dce;

import ir.*;
import ir.operand.IROperand;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static dce.Definitions.buildGen;
import static dce.Definitions.buildKill;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        HashSet<IRInstruction> leaders = findLeaders(program);
        ArrayList<Block> blocks = buildBlocks(program, leaders);
        buildCFG(blocks);
        buildKill(program);
        buildGen(blocks);
        runIterations(blocks);
        program.markSweep();

        IRPrinter filePrinter = new IRPrinter(new PrintStream(args[1]));
        filePrinter.printProgram(program);
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

    static void runIterations(ArrayList<Block> cfg) {
        boolean steady = false;
        while (!steady) {
            steady = true;
            for (Block block : cfg) {
                steady &= block.iterate();
            }
        }
    }
}
