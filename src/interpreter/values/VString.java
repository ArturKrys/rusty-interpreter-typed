package interpreter.values;

public class VString implements IValue {
	private final String value;

	public VString(String value) {
		this.value = value;
	}

	public String getval() {
		return value;
	}

	@Override
	public String toStr() {
		return value;
	}
}
