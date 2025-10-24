package ir;

import backend.Block;
import ir.datatype.IRType;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class IRFunction {

    public String name;

    public IRType returnType;

    public List<IRVariableOperand> parameters;

    public List<IRVariableOperand> variables;

    public List<IRInstruction> instructions;

    public HashMap<String, IRInstruction> labelMap = new HashMap<>();

    public ArrayList<Block> blocks = new ArrayList<>();

    public IRFunction(String name, IRType returnType,
                      List<IRVariableOperand> parameters, List<IRVariableOperand> variables,
                      List<IRInstruction> instructions) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.variables = variables;
        this.instructions = instructions;
        if (instructions != null)
            buildBlocks();
    }

    private HashSet<IRInstruction> findLeaders() {
        HashSet<IRInstruction> leaders = new HashSet<>();
        for (int i = 0; i < instructions.size(); i++) {
            IRInstruction inst = instructions.get(i);
            IROperand operand = (inst.opCode == IRInstruction.OpCode.CALLR) ? inst.operands[1] : inst.operands[0];
            switch (inst.opCode) {
                case LABEL:
                    leaders.add(inst);
                    break;
                case GOTO:
                    inst.branch1 = labelMap.get(operand.toString());
                    leaders.add(inst.branch1);
                    break;
                case BREQ:
                case BRNEQ:
                case BRLT:
                case BRGT:
                case BRGEQ:
                    inst.branch1 = (i + 1 < instructions.size()) ? instructions.get(i + 1) : null;
                    inst.branch2 = labelMap.get(operand.toString());
                    leaders.add(inst.branch1);
                    leaders.add(inst.branch2);
                    break;
                case CALL:
                case CALLR:
                    inst.branch1 = (i + 1 < instructions.size()) ? instructions.get(i + 1) : null;
                    leaders.add(inst.branch1);
                    break;
                default:
                    inst.branch1 = (i + 1 < instructions.size()) ? instructions.get(i + 1) : null;
                    break;
            }
        }
        return leaders;
    }

    public void buildBlocks() {
        HashSet<IRInstruction> leaders = findLeaders();
        Block block = new Block();
        for (IRInstruction inst : instructions) {
            if (leaders.contains(inst)) {
                blocks.add(block);
                block = new Block();
            }
            inst.block = block;
            block.irInst.add(inst);
        }
        blocks.add(block);
    }
}
