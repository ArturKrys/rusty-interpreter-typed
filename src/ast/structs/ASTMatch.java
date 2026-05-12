package ast.structs;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTMatch implements ASTNode {
	private final ASTNode targetExpression;
	private final ASTStructPattern structPattern;
	private final ASTNode onMatchExpression;
	private final ASTNode onMismatchExpression;

	public ASTMatch(
			ASTNode targetExpression,
			ASTStructPattern structPattern,
			ASTNode onMatchExpression,
			ASTNode onMismatchExpression) {

		this.targetExpression = targetExpression;
		this.structPattern = structPattern;
		this.onMatchExpression = onMatchExpression;
		this.onMismatchExpression = onMismatchExpression;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitMatch(this);
	}

	public ASTNode getTargetExpression() {
		return targetExpression;
	}

	public ASTStructPattern getStructPattern() {
		return structPattern;
	}

	public ASTNode getOnMatchExpression() {
		return onMatchExpression;
	}

	public ASTNode getOnMismatchExpression() {
		return onMismatchExpression;
	}
}
