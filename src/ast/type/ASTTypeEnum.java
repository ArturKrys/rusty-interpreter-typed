package ast.type;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class ASTTypeEnum implements ASTType {

	private final Map<String, List<ASTType>> options;

	public ASTTypeEnum(Map<String, List<ASTType>> options) {
		Objects.requireNonNull(options, "Enum options map cannot be null");

		Map<String, List<ASTType>> safeCopy = new LinkedHashMap<>();
		for (Map.Entry<String, List<ASTType>> entry : options.entrySet()) {
			Objects.requireNonNull(entry.getKey(), "Enum tag cannot be null");
			Objects.requireNonNull(entry.getValue(), "Enum type list cannot be null");

			safeCopy.put(entry.getKey(), List.copyOf(entry.getValue()));
		}

		this.options = Collections.unmodifiableMap(safeCopy);
	}

	public Map<String, List<ASTType>> getOptions() {
		return options;
	}

	@Override
	public String toStr() {
		if (options.isEmpty()) {
			return "enum {}";
		}

		String optionsString = options.entrySet().stream()
				.map(entry -> {
					String typesString = entry.getValue().stream()
							.map(ASTType::toStr)
							.collect(Collectors.joining(", "));
					return entry.getKey() + ": (" + typesString + ")";
				})
				.collect(Collectors.joining(", "));

		return "enum { " + optionsString + " }";
	}

	@Override
	public boolean eqt(ASTType other) {
		if (this == other)
			return true;
		if (!(other instanceof ASTTypeEnum))
			return false;

		ASTTypeEnum otherEnum = (ASTTypeEnum) other;

		if (this.options.size() != otherEnum.options.size())
			return false;

		for (Entry<String, List<ASTType>> entry : this.options.entrySet()) {
			String tag = entry.getKey();
			if (!otherEnum.options.containsKey(tag))
				return false;

			List<ASTType> thisTypes = entry.getValue();
			List<ASTType> otherTypes = otherEnum.options.get(tag);

			if (thisTypes.size() != otherTypes.size())
				return false;

			for (int i = 0; i < thisTypes.size(); i++) {
				if (!thisTypes.get(i).eqt(otherTypes.get(i)))
					return false;
			}
		}
		return true;
	}
}
