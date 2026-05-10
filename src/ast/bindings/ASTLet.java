package ast.bindings;

import java.util.List;

import ast.ASTNode;
import interpreter.environment.Bind;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTLet implements ASTNode {
	private final List<Bind> declarations;
	private final ASTNode bodyExpr;

	public ASTLet(List<Bind> declarations, ASTNode bodyExpr) {
		this.declarations = declarations;
		this.bodyExpr = bodyExpr;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitLet(this);
	}

	public List<Bind> getDeclarations() {
		return declarations;
	}

	public ASTNode getBodyExpr() {
		return bodyExpr;
	}
}
