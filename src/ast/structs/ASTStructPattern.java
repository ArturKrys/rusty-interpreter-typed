package ast.structs;

import java.util.List;

import ast.ASTNode;
import visitor.Visitor;

public class ASTStructPattern implements ASTNode {
	String tag;
	List<String> boundVariables;

	public ASTStructPattern(String tag, List<String> boundVariables) {
		this.tag = tag;
		this.boundVariables = boundVariables;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitStructPattern(this);
	}

	public String getTag() {
		return tag;
	}

	public List<String> getBoundVariables() {
		return boundVariables;
	}
}
