package ast.memory;

import ast.ASTNode;
import visitor.Visitor;

public class ASTCell implements ASTNode {
	private final ASTNode initialValue;

	public ASTCell(ASTNode initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitCell(this);
	}

	public ASTNode getInitialValue() {
		return initialValue;
	}
}
