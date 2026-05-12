package ast.memory;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTDeref implements ASTNode {
	private ASTNode reference;

	public ASTDeref(ASTNode reference) {
		this.reference = reference;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitDeref(this);
	}

	public ASTNode getReference() {
		return reference;
	}
}
