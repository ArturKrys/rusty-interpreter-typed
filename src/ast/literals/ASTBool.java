package ast.literals;

import ast.ASTNode;
import visitor.Visitor;

public class ASTBool implements ASTNode {
	private final boolean value;

	public ASTBool(boolean value) {
		this.value = value;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitBool(this);
	}

	public boolean getValue() {
		return value;
	}
}
