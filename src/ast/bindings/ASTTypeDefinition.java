package ast.bindings;

import java.util.Map;

import ast.ASTNode;
import ast.type.ASTType;
import visitor.Visitor;

/**
 * It registers the type aliases within a scoped environment
 */
public class ASTTypeDefinition implements ASTNode {
	private final Map<String, ASTType> typeDefinitions;
	private ASTNode bodyExpr;

	/**
	 * Constructs a new type definition AST node.
	 *
	 * @param typeDefinitions A mapping of type alias names to their corresponding
	 *                        structural types.
	 * @param body            The AST node/expression to be evaluated within this
	 *                        type scope.
	 */
	public ASTTypeDefinition(Map<String, ASTType> typeDefinitions, ASTNode bodyExpr) {
		if (typeDefinitions == null) {
			throw new IllegalArgumentException("Type definitions map node cannot be null.");
		}
		this.typeDefinitions = typeDefinitions;
		this.bodyExpr = bodyExpr;
	}

	@Override
	public <T, E extends Exception> T accept(Visitor<T, E> v) throws E {
		return v.visitTypeDefinition(this);
	}

	public void setBodyExpr(ASTNode bodyExpr) {
		this.bodyExpr = bodyExpr;
	}

	public Map<String, ASTType> getTypeDefinitions() {
		return this.typeDefinitions;
	}

	public ASTNode getBodyExpr() {
		return bodyExpr;
	}
}
