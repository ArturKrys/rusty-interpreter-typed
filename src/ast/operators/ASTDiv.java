package ast.operators;

import interpreter.values.IValue;
import visitor.Visitor;
import ast.ASTNode;
import interpreter.errors.InterpreterError;

public class ASTDiv implements ASTNode {
	private final ASTNode left, right;

	public ASTDiv(ASTNode left, ASTNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitDiv(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}
}
