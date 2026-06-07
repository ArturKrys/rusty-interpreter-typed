package ast.literals;

import ast.ASTNode;
import visitor.Visitor;

public class ASTInt implements ASTNode {
	private final int value;

	public ASTInt(int value) {
		this.value = value;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitInt(this);
	}

	public int getValue() {
		return value;
	}
}
