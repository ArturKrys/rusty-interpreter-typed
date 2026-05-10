package ast.literals;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTBool implements ASTNode {
    private final boolean value;

    public ASTBool(boolean value) {
        this.value = value;
    }

    @Override
    public IValue accept(Visitor v) throws InterpreterError {
        return v.visitBool(this);
    }

    public boolean getValue() {
        return value;
    }
}
