package ast.control;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTWhile implements ASTNode {
	private ASTNode condition;
	private ASTNode body;

	public ASTWhile(ASTNode condition, ASTNode body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitWhile(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public ASTNode getBody() {
		return body;
	}
}
