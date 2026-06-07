package ast.literals;

import ast.ASTNode;
import visitor.Visitor;

public class ASTId implements ASTNode {
	private final String id;

	public ASTId(String id) {
		this.id = id;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitId(this);
	}

	public String getId() {
		return id;
	}
}
