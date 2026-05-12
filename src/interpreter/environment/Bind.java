package interpreter.environment;

import ast.ASTNode;

public class Bind {
	private final String id;
	private final ASTNode exp;

	public Bind(String id, ASTNode exp) {
		this.id = id;
		this.exp = exp;
	}

	public String getId() {
		return id;
	}

	public ASTNode getExp() {
		return exp;
	}
}
