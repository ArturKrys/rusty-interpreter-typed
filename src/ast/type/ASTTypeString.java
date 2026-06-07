package ast.type;

public class ASTTypeString implements ASTType {
	public ASTTypeString() {
	}

	@Override
	public String toStr() {
		return "string";
	}

	@Override
	public boolean eqt(ASTType other) {
		return other instanceof ASTTypeString;
	}
}
