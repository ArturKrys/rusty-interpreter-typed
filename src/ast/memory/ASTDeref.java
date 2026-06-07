package ast.memory;

import ast.ASTNode;
import visitor.Visitor;

public class ASTDeref implements ASTNode {
	private final ASTNode reference;

	public ASTDeref(ASTNode reference) {
		this.reference = reference;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitDeref(this);
	}

	public ASTNode getReference() {
		return reference;
	}
}
