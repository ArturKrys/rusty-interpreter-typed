package ast.operators;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTNeg implements ASTNode {
	private final ASTNode expr;

	public ASTNeg(ASTNode expr) {
		this.expr = expr;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitNeg(this);
	}

	public ASTNode getExpr() {
		return expr;
	}
}
