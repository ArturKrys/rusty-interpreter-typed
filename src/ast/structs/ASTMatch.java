package ast.structs;

import ast.ASTNode;
import visitor.Visitor;
import java.util.List;

public class ASTMatch implements ASTNode {
	private final ASTNode targetExpression;
	private final List<ASTStructPattern> patterns;
	private final List<ASTNode> branches;

	public ASTMatch(
			ASTNode targetExpression,
			List<ASTStructPattern> patterns,
			List<ASTNode> branches) {

		this.targetExpression = targetExpression;
		this.patterns = patterns;
		this.branches = branches;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitMatch(this);
	}

	public ASTNode getTargetExpression() {
		return targetExpression;
	}

	public List<ASTStructPattern> getPatterns() {
		return patterns;
	}

	public List<ASTNode> getBranches() {
		return branches;
	}
}
