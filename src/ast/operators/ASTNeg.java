package ast.operators;

import ast.ASTNode;
import visitor.Visitor;

public class ASTNeg implements ASTNode {
	private final ASTNode expr;

	public ASTNeg(ASTNode expr) {
		this.expr = expr;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitNeg(this);
	}

	public ASTNode getExpr() {
		return expr;
	}
}
