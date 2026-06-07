package ast.type;

import java.util.List;
import java.util.ArrayList;

/**
 * Constructs a new structural tuple/variant type defined by a unique tag
 * and a list of internal element types (e.g., '#pair(int, bool)').
 */
public class ASTTypeStruct implements ASTType {
	private final String tag;
	private final List<ASTType> elementTypes;

	/**
	 * Constructs a new structural type.
	 *
	 * @param tag          The structural label/identifier (e.g., '#point')
	 * @param elementTypes The list of types contained within this structure
	 */
	public ASTTypeStruct(String tag, List<ASTType> elementTypes) {
		if (tag == null) {
			throw new IllegalArgumentException("Structure tag cannot be null.");
		}
		if (elementTypes == null) {
			throw new IllegalArgumentException("Element types list cannot be null.");
		}
		this.tag = tag;
		this.elementTypes = new ArrayList<>(elementTypes);
	}

	public String getTag() {
		return this.tag;
	}

	public List<ASTType> getElementTypes() {
		return new ArrayList<>(this.elementTypes);
	}

	@Override
	public String toStr() {
		StringBuilder sb = new StringBuilder();
		sb.append(tag).append("(");

		for (int i = 0; i < elementTypes.size(); i++) {
			sb.append(elementTypes.get(i).toStr());
			if (i < elementTypes.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean eqt(ASTType other) {
		if (other instanceof ASTTypeStruct) {
			ASTTypeStruct otherStruct = (ASTTypeStruct) other;

			// Both the tag and the number of nested fields must match
			if (!this.tag.equals(otherStruct.tag) || this.elementTypes.size() != otherStruct.elementTypes.size()) {
				return false;
			}

			// Structurally check equality for each element type in sequence
			for (int i = 0; i < this.elementTypes.size(); i++) {
				if (!this.elementTypes.get(i).equals(otherStruct.elementTypes.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
