package ast.control;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTIf implements ASTNode {
	private ASTNode condition;
	private ASTNode thenBranch;
	private ASTNode elseBranch;

	public ASTIf(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitIf(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public ASTNode getThenBranch() {
		return thenBranch;
	}

	public ASTNode getElseBranch() {
		return elseBranch;
	}
}
