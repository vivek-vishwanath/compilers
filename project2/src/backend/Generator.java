package backend;

import backend.interpreter.mips.MIPSInstruction;
import backend.interpreter.mips.MIPSOp;
import backend.interpreter.mips.MIPSProgram;
import backend.interpreter.mips.operand.Addr;
import backend.interpreter.mips.operand.Imm;
import backend.interpreter.mips.operand.Register;
import backend.interpreter.mips.operand.Imm.ImmType;
import ir.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class Generator {

    public static void main(String[] args) throws FileNotFoundException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);
        ArrayList<MIPSInstruction> instructions = new ArrayList<>();

        for (IRFunction function : program.functions) {
            System.out.println(function.name);
            String label = null;
            for (IRInstruction instruction : function.instructions) {
                if (instruction.opCode == IRInstruction.OpCode.LABEL) {
                    label = instruction.operands[0].toString();
                    continue;
                }
                ArrayList<MIPSInstruction> list = instruction.compile(label);
                list.forEach(it -> System.out.println("\t\t" + it));
                instructions.addAll(instructions);
                label = null;
            }
        }
        allocate(instructions, IRInstruction.vregCount);
    }

    // call function on a function-by-function basis
    public static void allocate(ArrayList<MIPSInstruction> list, int numVRegisters) {
        // ignore non virtual registers (a's, v's, etc.)
        Register stack = new Register("$sp");
        Imm stackOff = new Imm("" + -4*numVRegisters, ImmType.INT);
        list.add(0, new MIPSInstruction(MIPSOp.ADDI, list.get(1).label, stack, stack, stackOff));
        for (int i = 0; i < list.size(); i++) {
            MIPSInstruction instruction = list.get(i);
            HashMap<String, String> p2v = instruction.virtualToPhysical();
            Register[] reads = instruction.getReads();
            Register write = instruction.getWrite();
            for (int j = 0; j < reads.length; j++) {
                // only t regs were once virtual and need a mapping on the stack
                if (!reads[i].isT()) continue;
                Imm offset = new Imm("" + 4*Integer.parseInt(p2v.get(reads[i].name).substring(1)), ImmType.INT);
                Addr address = new Addr(offset, stack);
                // insert right before current instruction
                list.add(i, new MIPSInstruction(MIPSOp.LW, instruction.label, reads[i], address));
                i++; // move i down by 1
            }
            if (write != null) {
                if (!write.isT()) continue;
                Imm offset = new Imm("" + 4*Integer.parseInt(p2v.get(write.name).substring(1)), ImmType.INT);
                Addr address = new Addr(offset, stack);
                // add after current instruction
                list.add(i + 1, new MIPSInstruction(MIPSOp.SW, instruction.label, write, address));
                i++; // point i to sw instruction and i++ in for loop will skip over it
            }
        }
    }
}
