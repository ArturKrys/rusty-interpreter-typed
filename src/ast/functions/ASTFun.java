package ast.functions;

import ast.ASTNode;
import ast.type.ASTType;
import visitor.Visitor;

public class ASTFun implements ASTNode {
	private final String param;
	private final ASTType paramType;
	private ASTNode bodyExpr;

	public ASTFun(String param, ASTType paramType, ASTNode bodyExpr) {
		this.param = param;
		this.paramType = paramType;
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

	public ASTType getParamType() {
		return paramType;
	}

	public ASTNode getBodyExpr() {
		return bodyExpr;
	}
}
