package ast.operators;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTLt implements ASTNode {
    private final ASTNode left, right;

    public ASTLt(ASTNode left, ASTNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public IValue accept(Visitor v) throws InterpreterError {
        return v.visitLt(this);
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }
}
