package ast.type;

import java.util.*;

public class TypeBindList {
	private final HashMap<String, ASTType> variantTypes;

	public TypeBindList(HashMap<String, ASTType> variantTypes) {
		this.variantTypes = variantTypes;
	}

	public HashMap<String, ASTType> getVariantTypes() {
		return variantTypes;
	}
}
