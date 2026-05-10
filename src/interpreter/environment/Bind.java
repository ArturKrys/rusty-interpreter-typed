package interpreter.environment;

import ast.ASTNode;

public class Bind {
	private final String id;
	private final ASTNode exp;

	public Bind(String _id, ASTNode _exp) {
		this.id = _id;
		this.exp = _exp;
	}

	public String getId() {
		return id;
	}

	public ASTNode getExp() {
		return exp;
	}
}
