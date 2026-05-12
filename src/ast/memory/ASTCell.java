package ast.memory;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTCell implements ASTNode {
	private final ASTNode initialValue;

	public ASTCell(ASTNode initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitCell(this);
	}

	public ASTNode getInitialValue() {
		return initialValue;
	}
}
