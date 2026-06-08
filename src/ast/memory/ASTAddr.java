package ast.memory;

import ast.ASTNode;
import visitor.Visitor;

public class ASTAddr implements ASTNode {
	private final ASTNode target;

	/**
	 * Constructs an ASTAddr node representing the address-of operator (&).
	 *
	 * @param target The expression whose address is being taken (e.g., 's' in
	 *               '&s').
	 */
	public ASTAddr(ASTNode target) {
		if (target == null) {
			throw new IllegalArgumentException("Target expression for ASTAddr cannot be null.");
		}
		this.target = target;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitAddr(this);
	}

	public ASTNode getTarget() {
		return target;
	}

}
