package backend;

import ir.*;

import java.io.FileNotFoundException;

public class Generator {

    public static void main(String[] args) throws FileNotFoundException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        for (IRFunction function : program.functions) {
            for (IRInstruction instruction : function.instructions) {

            }
        }
    }

}
