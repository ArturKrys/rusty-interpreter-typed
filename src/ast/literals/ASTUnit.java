package ast.literals;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTUnit implements ASTNode {
	public ASTUnit() {
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitUnit(this);
	}
}
