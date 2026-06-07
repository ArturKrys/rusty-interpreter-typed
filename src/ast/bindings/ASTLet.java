package ast.bindings;

import java.util.List;

import ast.ASTNode;
import interpreter.environment.Bind;
import visitor.Visitor;

public class ASTLet implements ASTNode {
	private final List<Bind> declarations;
	private ASTNode bodyExpr;

	public ASTLet(List<Bind> declarations, ASTNode bodyExpr) {
		this.declarations = declarations;
		this.bodyExpr = bodyExpr;
	}

	public void setBodyExpr(ASTNode bodyExpr) {
		this.bodyExpr = bodyExpr;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitLet(this);
	}

	public List<Bind> getDeclarations() {
		return declarations;
	}

	public ASTNode getBodyExpr() {
		return bodyExpr;
	}
}
