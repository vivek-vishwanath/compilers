package backend;

import backend.interpreter.mips.MIPSInstruction;
import ir.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Generator {

    public static void main(String[] args) throws FileNotFoundException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

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
                label = null;
            }
        }
    }

    public static void allocate(ArrayList<MIPSInstruction> list) {

    }

}
