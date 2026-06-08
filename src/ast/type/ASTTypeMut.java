package ast.type;

public class ASTTypeMut implements ASTType {

	private ASTType innerType;

	public ASTTypeMut(ASTType innerType) {
		this.innerType = innerType;
	}

	public ASTType getInnerType() {
		return innerType;
	}

	@Override
	public String toStr() {
		return "mut(" + innerType.toStr() + ")";
	}

	@Override
	public boolean eqt(ASTType t) {
		if (t instanceof ASTTypeMut) {
			ASTTypeMut other = (ASTTypeMut) t;
			return this.innerType.eqt(other.getInnerType());
		}
		return false;
	}
}
