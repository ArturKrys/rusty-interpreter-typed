package ast.type;

public class ASTTypeInt implements ASTType {
	public static final ASTTypeInt TINT = new ASTTypeInt();

	private ASTTypeInt() {
	}

	@Override
	public String toStr() {
		return "int";
	}

	@Override
	public boolean eqt(ASTType other) {
		return (other instanceof ASTTypeInt);
	}
}
