package backend;

import backend.interpreter.mips.MemLayout;
import backend.interpreter.mips.operand.Register;
import ir.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class Generator {

    public static void main(String[] args) throws IOException, IRException {
        IRReader irReader = new IRReader();
        IRProgram program = irReader.parseIRFile(args[0]);

        FileWriter writer = new FileWriter(args[1]);

        writer.write(".data\n\tSTACK: .word " + (long) MemLayout.STACK + "\n\n");
        writer.write(".text\n");
        writer.write("\tlw $sp, STACK\n");
        writer.write("\tmove $fp, $sp\n");
        writer.write("\tjal main\n\tli $v0, 10\n\tsyscall\n\n");
        writer.flush();

        for (IRFunction function : program.functions) {
            Mode mode = null;
            if (args.length == 2) mode = Mode.NAIVE;
            else if ("--naive".equals(args[2])) mode = Mode.NAIVE;
            else if ("--greedy".equals(args[2])) mode = Mode.GREEDY;
            else if ("--chaitin".equals(args[2])) mode = Mode.CHAITIN_BRIGGS;
            else mode = Mode.NAIVE;
            writer.write(function.name + ":\n");
            function.buildBlocks();
            function.buildCFG();
            function.compileToMIPS();
            function.allocate(mode);
            function.print(writer, mode);
            writer.write("\n");
            writer.flush();
            Register.clear();
        }
        writer.close();

    }

    public enum Mode {
        NAIVE, GREEDY, CHAITIN_BRIGGS
    }
}
