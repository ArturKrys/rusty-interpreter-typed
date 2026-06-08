package ast.type;

public class ASTTypeAddr implements ASTType {

	private ASTType innerType;

	public ASTTypeAddr(ASTType innerType) {
		this.innerType = innerType;
	}

	public ASTType getInnerType() {
		return innerType;
	}

	@Override
	public String toStr() {
		return "addr(" + innerType.toStr() + ")";
	}

	@Override
	public boolean eqt(ASTType other) {
		if (other instanceof ASTTypeAddr) {
			ASTTypeAddr otherAddr = (ASTTypeAddr) other;
			return this.innerType.eqt(otherAddr.getInnerType());
		}
		return false;
	}
}
