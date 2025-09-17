package ir;

import ir.datatype.IRType;
import ir.operand.IRVariableOperand;

import java.util.*;

public class IRFunction {

    public String name;

    public IRType returnType;

    public List<IRVariableOperand> parameters;

    public List<IRVariableOperand> variables;

    public List<IRInstruction> instructions;

    public HashMap<String, IRInstruction> labelMap = new HashMap<>();

    public IRFunction(String name, IRType returnType,
                      List<IRVariableOperand> parameters, List<IRVariableOperand> variables,
                      List<IRInstruction> instructions) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.variables = variables;
        this.instructions = instructions;
    }

    public void markSweep() {
        // Mark
        LinkedList<IRInstruction> workList = getCriticalInstructions();
        HashSet<IRInstruction> marked = new HashSet<>(workList);
        while (!workList.isEmpty()) {
            IRInstruction instruction = workList.pop();
            HashMap<String, ArrayList<IRInstruction>> reachingDefinitions = instruction.getReachingDefinitions();
            for (Map.Entry<String, ArrayList<IRInstruction>> instructions : reachingDefinitions.entrySet()) {
                for (IRInstruction inst : instructions.getValue()) {
                    if (!marked.contains(inst)) {
                        marked.add(inst);
                        workList.add(inst);
                    }
                }
            }
        }
        // Sweep
        instructions.removeIf(instruction -> !marked.contains(instruction));
    }


    private LinkedList<IRInstruction> getCriticalInstructions() {
        LinkedList<IRInstruction> criticalInstructions = new LinkedList<>();
        HashSet<IRInstruction.OpCode> critical = new HashSet<>(Arrays.asList(
                IRInstruction.OpCode.BREQ, IRInstruction.OpCode.BRGEQ, IRInstruction.OpCode.BRGT, IRInstruction.OpCode.BRLT, IRInstruction.OpCode.BRNEQ,
                IRInstruction.OpCode.GOTO, IRInstruction.OpCode.ARRAY_STORE, IRInstruction.OpCode.RETURN, IRInstruction.OpCode.CALL,
                IRInstruction.OpCode.CALLR, IRInstruction.OpCode.LABEL));
        for (IRInstruction instruction : instructions) {
            if (critical.contains(instruction.opCode) || instruction.opCode == IRInstruction.OpCode.ASSIGN && instruction.operands.length > 2) {
                criticalInstructions.add(instruction);
            }
        }
        return criticalInstructions;
    }
}
