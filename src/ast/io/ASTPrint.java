package ast.io;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTPrint implements ASTNode {
	private final ASTNode expression;

	public ASTPrint(ASTNode expression) {
		this.expression = expression;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitPrint(this);
	}

	public ASTNode getExpression() {
		return expression;
	}
}
