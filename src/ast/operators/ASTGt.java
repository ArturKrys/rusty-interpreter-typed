package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTGt implements ASTNode {
	private final ASTNode left, right;

	public ASTGt(ASTNode left, ASTNode right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitGt(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}
}
