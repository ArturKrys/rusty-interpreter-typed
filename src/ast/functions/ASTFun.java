package ast.functions;

import ast.ASTNode;
import visitor.Visitor;

public class ASTFun implements ASTNode {
	private final String param;
	private ASTNode bodyExpr;

	public ASTFun(String param, ASTNode bodyExpr) {
		this.param = param;
		this.bodyExpr = bodyExpr;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitFun(this);
	}

	public void setBodyExpr(ASTNode bodyExpr) {
		this.bodyExpr = bodyExpr;
	}

	public String getParam() {
		return param;
	}

	public ASTNode getBodyExpr() {
		return bodyExpr;
	}
}
