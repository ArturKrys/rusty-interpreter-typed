package ast.functions;

import ast.ASTNode;
import visitor.Visitor;

public class ASTApp implements ASTNode {
    private final ASTNode function, argument;

    public ASTApp(ASTNode function, ASTNode argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
        return v.visitApp(this);
    }

    public ASTNode getFunction() {
        return function;
    }

    public ASTNode getArgument() {
        return argument;
    }
}
