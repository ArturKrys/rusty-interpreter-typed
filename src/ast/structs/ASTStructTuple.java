package ast.structs;

import java.util.List;

import ast.ASTNode;
import visitor.Visitor;

public class ASTStructTuple implements ASTNode {
	String tag;
	List<ASTNode> elements;

	public ASTStructTuple(String tag, List<ASTNode> elements) {
		this.tag = tag;
		this.elements = elements;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitStructTuple(this);
	}

	public String getTag() {
		return tag;
	}

	public List<ASTNode> getElements() {
		return elements;
	}
}
