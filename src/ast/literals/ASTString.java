package ast.literals;

import ast.ASTNode;
import visitor.Visitor;

public class ASTString implements ASTNode {
	private final String value;

	public ASTString(String rawValue) {
		this.value = rawValue.substring(1, rawValue.length() - 1)
				.replace("\\\"", "\"")
				.replace("\\n", "\n")
				.replace("\\t", "\t")
				.replace("\\r", "\r")
				.replace("\\\\", "\\");
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitString(this);
	}

	public String getValue() {
		return value;
	}
}
