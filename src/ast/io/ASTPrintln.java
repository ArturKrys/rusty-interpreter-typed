package ast.io;

import ast.ASTNode;
import visitor.Visitor;

public class ASTPrintln implements ASTNode {
	private final ASTNode expression;

	public ASTPrintln(ASTNode expression) {
		this.expression = expression;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitPrintln(this);
	}

	public ASTNode getExpression() {
		return expression;
	}
}
