package ast.literals;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTId implements ASTNode {
	private final String id;

	public ASTId(String id) {
		this.id = id;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitId(this);
	}

	public String getId() {
		return id;
	}
}
