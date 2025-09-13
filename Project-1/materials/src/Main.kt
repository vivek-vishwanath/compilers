import ir.IRInstruction
import ir.IRPrinter
import ir.IRReader
import ir.datatype.IRArrayType
import ir.datatype.IRIntType
import ir.operand.IRVariableOperand
import java.io.PrintStream
import ir.OpCode
import ir.OpCode.*

object Main {

    val ALU = arrayOf(ADD, SUB, MULT, DIV, AND, OR)
    val BRANCH = arrayOf(BREQ, BRNEQ, BRLT, BRGT, BRGEQ)

    @JvmStatic
    fun main(args: Array<String>) {
        // Parse the IR file
        val irReader = IRReader()
        val program = irReader.parseIRFile(args[0])

        // Print the IR to another file
        val filePrinter = IRPrinter(PrintStream(args[1]))
        filePrinter.printProgram(program)

        // Create an IR printer that prints to stdout
        IRPrinter(PrintStream(System.out))

        val worklist = ArrayDeque<IRInstruction>()
        for (function in program.functions) {
            for (instruction in function.instructions) {
                if (instruction.opCode in BRANCH || instruction.opCode == CALL || instruction.opCode == CALLR || instruction.opCode == RETURN) {
                    instruction.critical = true
                    worklist.add(instruction)
                }
            }
        }
        while (worklist.isNotEmpty()) {
            val item = worklist.removeFirst()
            val args = HashSet<String>()
            if (item.opCode == GOTO || item.opCode == LABEL) continue
            val op1 = item.getVarOperand(1)
            val op2 = item.getVarOperand(2)
            when (item.opCode) {
                RETURN -> item.getVarOperand(0)?.let { args.add(it) }
                ASSIGN -> op1?.let { args.add(it) }
                ADD, SUB, MULT, DIV, AND, OR, BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                    op1?.let { args.add(it) }
                    op2?.let { args.add(it) }
                }
                CALL, CALLR -> {
                    for (i in 2 ..< item.operands.size)
                        item.getVarOperand(i)?.let { args.add(it) }
                }
                ARRAY_STORE -> {
                    val op3 = item.getVarOperand(3)
                    op1?.let { args.add(it) }
                    op2?.let { args.add(it) }
                    op3?.let { args.add(it) }
                }
                ARRAY_LOAD -> {
                    val op3 = item.getVarOperand(3)
                    op2?.let { args.add(it) }
                    op3?.let { args.add(it) }
                }

                else -> {}
            }
            for (f in program.functions) {
                for (i in f.instructions) {
                    if (item === i || i.critical) continue
                    when (i.opCode) {
                        ASSIGN, ADD, SUB, MULT, DIV, AND, OR, CALLR, ARRAY_LOAD -> {
                            val operand = i.operands[0]
                            if (operand is IRVariableOperand) {
                                if (args.contains(operand.name)) {
                                    i.critical = true
                                    worklist.add(i)
                                }
                            }
                        }
                        BREQ, BRNEQ, BRLT, BRGT, BRGEQ, GOTO, CALL, RETURN, LABEL, ARRAY_STORE -> {}
                    }
                }
            }
        }


        // Print the name of all int scalars and int arrays with a size of 1
        println("Int scalars and 1-sized arrays:")
        for (function in program.functions) {
            val vars: MutableList<String?> = ArrayList<String?>()
            for (v in function.variables) {
                val type = v.type
                // For each unique data type, only one IRType object will be created
                // so that IRType objects can be compared using '=='
                if (type === IRIntType.get() || type === IRArrayType.get(IRIntType.get(), 1)) vars.add(v.name)
            }
            if (!vars.isEmpty()) println(function.name + ": " + java.lang.String.join(", ", vars))
        }
        println()


        // Print all variables that are declared but not used (including unused parameters)
        println("Unused variables/parameters:")
        for (function in program.functions) {
            // IROperand objects are not shared between instructions/parameter list/variable list
            // They should be compared using their names
            val vars: MutableSet<String?> = HashSet<String?>()
            // Parameters are not included in the variable list
            for (v in function.parameters) vars.add(v.name)
            for (v in function.variables) vars.add(v.name)
            for (instruction in function.instructions) for (operand in instruction.operands) if (operand is IRVariableOperand) {
                val variableOperand = operand
                vars.remove(variableOperand.name)
            }
            if (!vars.isEmpty()) println(function.name + ": " + java.lang.String.join(", ", vars))
        }
        println()
    }

}