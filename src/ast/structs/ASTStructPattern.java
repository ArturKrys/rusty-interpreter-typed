package ast.structs;

import java.util.List;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTStructPattern implements ASTNode {
	String tag;
	List<String> boundVariables;

	public ASTStructPattern(String tag, List<String> boundVariables) {
		this.tag = tag;
		this.boundVariables = boundVariables;
	}

	@Override
	public IValue accept(Visitor v) throws InterpreterError {
		return v.visitStructPattern(this);
	}

	public String getTag() {
		return tag;
	}

	public List<String> getBoundVariables() {
		return boundVariables;
	}
}
