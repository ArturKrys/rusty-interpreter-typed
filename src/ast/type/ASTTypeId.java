package ast.type;

public class ASTTypeId implements ASTType {
	private final String id;

	/**
	 * Constructs a new identifier type.
	 *
	 * @param id The name of the type identifier (e.g., alias name)
	 */
	public ASTTypeId(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Type identifier cannot be null.");
		}
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toStr() {
		return this.id;
	}

	@Override
	public boolean eqt(ASTType other) {
		if (other instanceof ASTTypeId) {
			ASTTypeId otherId = (ASTTypeId) other;
			return this.id.equals(otherId.id);
		}
		return false;
	}
}
