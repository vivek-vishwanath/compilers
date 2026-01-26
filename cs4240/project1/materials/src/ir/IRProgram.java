package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IRProgram {

    public List<IRFunction> functions;
    public HashMap<String, IRFunction> functionMap;

    public IRProgram() {
        functions = new ArrayList<>();
    }

    public IRProgram(List<IRFunction> functions, HashMap<String, IRFunction> functionMap) {
        this.functions = functions;
        this.functionMap = functionMap;
    }

    public void markSweep() {
        for (IRFunction function : functions) {
            function.markSweep();
        }
    }

}
