package ast.type;

public class ASTTypeArrow implements ASTType {
	private final ASTType domain;
	private final ASTType codomain;

	/**
	 * Constructs a new function arrow type.
	 *
	 * @param domain   The input parameter type (e.g., 'int' in 'int -> bool')
	 * @param codomain The return type (e.g., 'bool' in 'int -> bool')
	 */
	public ASTTypeArrow(ASTType domain, ASTType codomain) {
		if (domain == null || codomain == null) {
			throw new IllegalArgumentException("Domain and codomain types cannot be null.");
		}
		this.domain = domain;
		this.codomain = codomain;
	}

	public ASTType getDomain() {
		return this.domain;
	}

	public ASTType getCodomain() {
		return this.codomain;
	}

	@Override
	public String toStr() {
		return "(" + domain.toStr() + " -> " + codomain.toStr() + ")";
	}

	@Override
	public boolean eqt(ASTType other) {
		if (other instanceof ASTTypeArrow) {
			ASTTypeArrow otherArrow = (ASTTypeArrow) other;
			return this.domain.eqt(otherArrow.domain)
					&& this.codomain.eqt(otherArrow.codomain);
		}
		return false;
	}
}
