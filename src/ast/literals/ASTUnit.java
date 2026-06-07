package ast.literals;

import ast.ASTNode;
import visitor.Visitor;

public class ASTUnit implements ASTNode {
	public ASTUnit() {
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitUnit(this);
	}
}
