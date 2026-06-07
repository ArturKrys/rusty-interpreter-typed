package ast.type;

public class ASTTypeUnit implements ASTType {
	public static final ASTTypeUnit TUNIT = new ASTTypeUnit();

	private ASTTypeUnit() {
	}

	@Override
	public String toStr() {
		return "()";
	}

	@Override
	public boolean eqt(ASTType other) {
		return other instanceof ASTTypeUnit;
	}
}
