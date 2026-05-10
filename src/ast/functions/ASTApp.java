package ast.functions;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTApp implements ASTNode {
    private final ASTNode function, argument;

    public ASTApp(ASTNode function, ASTNode argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public IValue accept(Visitor v) throws InterpreterError {
        return v.visitApp(this);
    }

    public ASTNode getFunction() {
        return function;
    }

    public ASTNode getArgument() {
        return argument;
    }
}
