package ast;

import visitor.Visitor;

public interface ASTNode {
	<T, E extends Exception> T accept(Visitor<T, E> v) throws E;
}
