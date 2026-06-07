package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTAnd implements ASTNode {
	private final ASTNode left, right;

	public ASTAnd(ASTNode left, ASTNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitAnd(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}
}
