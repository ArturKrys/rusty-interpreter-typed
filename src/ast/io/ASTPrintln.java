package ast.io;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTPrintln implements ASTNode {
	private final ASTNode expression;

	public ASTPrintln(ASTNode expression) {
		this.expression = expression;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitPrintln(this);
	}

	public ASTNode getExpression() {
		return expression;
	}
}
