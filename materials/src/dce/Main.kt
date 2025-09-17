package dce

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
        val blocks = buildBlocks(program, leaders)

        buildCFG(blocks)

        println(blocks)
    }

    fun findLeaders(program: IRProgram): HashSet<IRInstruction> {

        val leaders = HashSet<IRInstruction>()

        for (func in program.functions) {
            for ((i, inst) in func.instructions.withIndex()) {
                fun getNext() = if (i+1 < func.instructions.size) func.instructions[i+1] else null
                val operand = (if (inst.opCode == CALLR) inst.operands[1] else inst.operands[0]).toString()
                when (inst.opCode) {
                    GOTO -> {
                        inst.branch1 = func.labelMap[operand]!!
                        leaders.add(inst.branch1)
                    }
                    BREQ, BRNEQ, BRLT, BRGT, BRGEQ -> {
                        inst.branch1 = getNext()
                        inst.branch2 = func.labelMap[operand]!!
                        leaders.add(inst.branch1)
                        leaders.add(inst.branch2)
                    }
                    CALL, CALLR -> {
                        inst.branch1 = getNext()
                        inst.branch2 = program.functionMap[operand]?.instructions?.firstOrNull()
                        leaders.add(inst.branch1)
                    }
                    else -> {
                        inst.branch1 = getNext()
                    }
                }
            }
        }

        return leaders
	}

    fun buildBlocks(program: IRProgram, leaders: HashSet<IRInstruction>): ArrayList<Block> {
        val blocks = ArrayList<Block>()
        for (func in program.functions) {
            var block = Block()
            for (inst in func.instructions) {
                if (leaders.contains(inst)) {
                    blocks.add(block)
                    block = Block()
                }
                inst.block = block
                block.instructions.add(inst)
            }
            blocks.add(block)
        }
        return blocks

    }

    fun buildCFG(blocks: ArrayList<Block>) {
        for (block in blocks) {
            block.next1 = block.branch.branch1?.block
            block.next2 = block.branch.branch2?.block
        }
    }
}