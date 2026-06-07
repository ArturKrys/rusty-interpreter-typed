package ast.memory;

import ast.ASTNode;
import visitor.Visitor;

public class ASTAssign implements ASTNode {
	private final ASTNode target, value;

	public ASTAssign(ASTNode target, ASTNode value) {
		this.target = target;
		this.value = value;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitAssign(this);
	}

	public ASTNode getTarget() {
		return target;
	}

	public ASTNode getValue() {
		return value;
	}
}
