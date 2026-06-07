package ast.type;

public class ASTTypeRef implements ASTType {
	private final ASTType type;

	/**
	 * Constructs a new reference type.
	 *
	 * @param type The type being referenced (e.g., 'int' in 'ref(int)')
	 */
	public ASTTypeRef(ASTType type) {
		if (type == null) {
			throw new IllegalArgumentException("Referenced type cannot be null.");
		}
		this.type = type;
	}

	public ASTType getType() {
		return this.type;
	}

	@Override
	public String toStr() {
		return "ref<" + type.toStr() + ">";
	}

	@Override
	public boolean eqt(ASTType other) {
		if (other instanceof ASTTypeRef) {
			ASTTypeRef otherRef = (ASTTypeRef) other;
			return this.type.eqt(otherRef.type);
		}
		return false;
	}
}
