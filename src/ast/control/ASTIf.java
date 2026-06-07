package ast.control;

import ast.ASTNode;
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
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
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
