import ir.*;
import ir.datatype.IRArrayType;
import ir.datatype.IRIntType;
import ir.datatype.IRType;
import ir.operand.IRConstantOperand;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

import java.io.PrintStream;
import java.util.*;

public class Demo {
    public static void main(String[] args) throws Exception {
        // Parse the IR file
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        // Print the IR to another file
        IRPrinter filePrinter = new IRPrinter(new PrintStream(args[1]));
        filePrinter.printProgram(program);

        // Create an IR printer that prints to stdout
        IRPrinter irPrinter = new IRPrinter(new PrintStream(System.out));

        // Print all instructions that stores a constant to an array
        System.out.println("Instructions that stores a constant to an array:");
        for (IRFunction function : program.functions) {
            for (IRInstruction instruction : function.instructions) {
                if (instruction.opCode == IRInstruction.OpCode.ARRAY_STORE) {
                    if (instruction.operands[0] instanceof IRConstantOperand) {
                        System.out.print(String.format("Line %d:", instruction.irLineNumber));
                        irPrinter.printInstruction(instruction);
                    }
                }
            }
        }
        System.out.println();

        // Print the name of all int scalars and int arrays with a size of 1
        System.out.println("Int scalars and 1-sized arrays:");
        for (IRFunction function : program.functions) {
            List<String> vars = new ArrayList<>();
            for (IRVariableOperand v : function.variables) {
                IRType type = v.type;
                // For each unique data type, only one IRType object will be created
                // so that IRType objects can be compared using '=='
                if (type == IRIntType.get() || type == IRArrayType.get(IRIntType.get(), 1))
                    vars.add(v.getName());
            }
            if (!vars.isEmpty())
                System.out.println(function.name + ": " + String.join(", ", vars));
        }
        System.out.println();

        // Print all variables that are declared but not used (including unused parameters)
        System.out.println("Unused variables/parameters:");
        for (IRFunction function : program.functions) {
            // IROperand objects are not shared between instructions/parameter list/variable list
            // They should be compared using their names
            Set<String> vars = new HashSet<>();
            // Parameters are not included in the variable list
            for (IRVariableOperand v : function.parameters)
                vars.add(v.getName());
            for (IRVariableOperand v : function.variables)
                vars.add(v.getName());
            for (IRInstruction instruction : function.instructions)
                for (IROperand operand : instruction.operands)
                    if (operand instanceof IRVariableOperand) {
                        IRVariableOperand variableOperand = (IRVariableOperand) operand;
                        vars.remove(variableOperand.getName());
                    }
            if (!vars.isEmpty())
                System.out.println(function.name + ": " + String.join(", ", vars));
        }
        System.out.println();
    }

    //
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

    // dummy method
    private static LinkedList<IRInstruction> getReachingDefinitions(IRInstruction instruction) {
        return new LinkedList<IRInstruction>();
    }

    private static HashMap<String, IRInstruction> computeGen(Block basicBlock) {
        HashMap<String, IRInstruction> gen = new HashMap<>();
        for (IRInstruction instruction : basicBlock.instructions) {
            gen.put(instruction.operands, instruction);
        }
    }

    private static computeSetDifference() {

    }

    private static boolean isWrite(IRInstruction instruction) {
        HashSet<IRInstruction.OpCode> nonWriteInstructions = new HashSet<>(Arrays.asList(
            IRInstruction.OpCode.GOTO, IRInstruction.OpCode.BREQ, IRInstruction.OpCode.BRNEQ, IRInstruction.OpCode.BRLT,
            IRInstruction.OpCode.BRGT, IRInstruction.OpCode.BRGEQ, IRInstruction.OpCode.RETURN, IRInstruction.OpCode.CALL));
        
        
    }
}
