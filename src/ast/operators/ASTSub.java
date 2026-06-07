package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTSub implements ASTNode {
	private final ASTNode left, right;

	public ASTSub(ASTNode left, ASTNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitSub(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}
}
