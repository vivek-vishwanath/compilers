package ir;

public enum OpCode {
    ASSIGN,
    ADD, SUB, MULT, DIV, AND, OR,
    GOTO,
    BREQ, BRNEQ, BRLT, BRGT, BRGEQ,
    RETURN,
    CALL, CALLR,
    ARRAY_STORE, ARRAY_LOAD,
    LABEL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
