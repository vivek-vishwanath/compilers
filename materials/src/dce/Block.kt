package dce

import ir.IRInstruction

class Block {

    val instructions = ArrayList<IRInstruction>()
    val leader get() = instructions.first()
    val branch get() = instructions.last()

    val id = Block.id++

    var next1: Block? = null
    var next2: Block? = null

    companion object {
        var id = 0
    }

    override fun toString() = "Block_$id [${next1?.id ?: ""}, ${next2?.id ?: ""}]]"
}