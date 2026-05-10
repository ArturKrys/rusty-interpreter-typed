package ast.operators;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTNot implements ASTNode {
    private final ASTNode expr;

    public ASTNot(ASTNode expr) {
        this.expr = expr;
    }

    @Override
    public IValue accept(Visitor v) throws InterpreterError {
        return v.visitNot(this);
    }

    public ASTNode getExpr() {
        return expr;
    }
}
