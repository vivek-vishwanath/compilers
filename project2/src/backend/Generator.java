package backend;

import backend.interpreter.mips.MemLayout;
import ir.*;

import java.io.FileWriter;
import java.io.IOException;

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
            writer.write(function.name + ":\n");
            function.compileToMIPS();
            function.intraBlockAlloc();
            function.print(writer, true);
            writer.write("\n");
            writer.flush();
        }
        writer.close();

    }
}
