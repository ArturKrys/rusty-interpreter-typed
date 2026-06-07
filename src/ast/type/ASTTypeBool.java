package ast.type;

public class ASTTypeBool implements ASTType {
	public static final ASTType TBOOL = new ASTTypeBool();

	private ASTTypeBool() {
	}

	@Override
	public String toStr() {
		return "bool";
	}

	@Override
	public boolean eqt(ASTType other) {
		return other instanceof ASTTypeBool;
	}
}
