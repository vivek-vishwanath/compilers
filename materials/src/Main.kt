import ir.IRInstruction
import ir.IRInstruction.OpCode.*
import ir.IRProgram
import ir.IRReader

object Main {

	@JvmStatic 
	fun main(args: Array<String>) {

        // Parse the IR file
        val irReader = IRReader()
        val program = irReader.parseIRFile(args[0])

        val leaders = findLeaders(program)
        for (leader in leaders) {
            println(leader)
        }
    }

    fun findLeaders(program: IRProgram): HashSet<IRInstruction> {

        val leaders = HashSet<IRInstruction>()
        var wasBranch = false

        for (func in program.functions) {
            for (inst in func.instructions) {
                if (wasBranch) {
                    leaders.add(inst)
                    wasBranch = false
                }


                when (inst.opCode) {
                    GOTO -> leaders.add(func.labelMap[inst.operands[0].toString()]!!)
                    BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                        wasBranch = true
                        leaders.add(func.labelMap[inst.operands[0].toString()]!!)
                    }
                    CALL, CALLR -> {
                        wasBranch = true
                    }
                    else -> {}
                }
            }
        }

        return leaders
	}
}
