package ast.literals;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTInt implements ASTNode {
	private final int value;

	public ASTInt(int value) {
		this.value = value;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitInt(this);
	}

	public int getValue() {
		return value;
	}
}
