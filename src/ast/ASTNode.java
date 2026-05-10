package ast;

import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public interface ASTNode {
	IValue accept(Visitor v) throws InterpreterError;
}
