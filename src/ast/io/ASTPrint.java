package ast.io;

import ast.ASTNode;
import visitor.Visitor;

public class ASTPrint implements ASTNode {
	private final ASTNode expression;

	public ASTPrint(ASTNode expression) {
		this.expression = expression;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitPrint(this);
	}

	public ASTNode getExpression() {
		return expression;
	}
}
