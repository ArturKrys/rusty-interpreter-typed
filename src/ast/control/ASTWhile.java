package ast.control;

import ast.ASTNode;
import visitor.Visitor;

public class ASTWhile implements ASTNode {
	private ASTNode condition;
	private ASTNode body;

	public ASTWhile(ASTNode condition, ASTNode body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitWhile(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public ASTNode getBody() {
		return body;
	}
}
