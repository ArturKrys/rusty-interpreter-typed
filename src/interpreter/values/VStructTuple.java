package interpreter.values;

import java.util.List;
import java.util.stream.Collectors;

public class VStructTuple implements IValue {
	private final String tag;
	private final List<IValue> values;

	public VStructTuple(String tag, List<IValue> values) {
		this.tag = tag;
		this.values = values;
	}

	public String getTag() {
		return tag;
	}

	public List<IValue> getValues() {
		return values;
	}

	@Override
	public String toStr() {
		String body = values.stream()
				.map(IValue::toStr)
				.collect(Collectors.joining(", "));
		return tag + "(" + body + ")";
	}
}
