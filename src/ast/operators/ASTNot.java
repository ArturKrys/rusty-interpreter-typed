package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTNot implements ASTNode {
	private final ASTNode expr;

	public ASTNot(ASTNode expr) {
		this.expr = expr;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitNot(this);
	}

	public ASTNode getExpr() {
		return expr;
	}
}
