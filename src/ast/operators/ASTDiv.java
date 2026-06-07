package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTDiv implements ASTNode {
	private final ASTNode left, right;

	public ASTDiv(ASTNode left, ASTNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitDiv(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}
}
