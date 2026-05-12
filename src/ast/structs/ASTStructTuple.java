package ast.structs;

import java.util.List;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTStructTuple implements ASTNode {
	String tag;
	List<ASTNode> elements;

	public ASTStructTuple(String tag, List<ASTNode> elements) {
		this.tag = tag;
		this.elements = elements;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitStructTuple(this);
	}

	public String getTag() {
		return tag;
	}

	public List<ASTNode> getElements() {
		return elements;
	}
}
