package interpreter.environment;

import ast.ASTNode;
import ast.type.ASTType;

public class Bind {
	private final String id;
	private final boolean isMutable;
	private final ASTType type;
	private final ASTNode exp;

	public Bind(String id, boolean isMutable, ASTType type, ASTNode exp) {
		this.id = id;
		this.isMutable = isMutable;
		this.type = type;
		this.exp = exp;
	}

	public String getId() {
		return id;
	}

	public boolean isMutable() {
		return isMutable;
	}

	public ASTType getType() {
		return type;
	}

	public ASTNode getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return String.format("Bind{id='%s', type=%s, exp=%s, mutable=%b}",
				id, type, exp, isMutable);
	}
}
