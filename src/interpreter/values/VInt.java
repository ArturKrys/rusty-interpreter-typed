package interpreter.values;

public class VInt implements IValue {
	private final int value;

	public VInt(int value) {
		this.value = value;
	}

	public int getval() {
		return value;
	}

	@Override
	public String toStr() {
		return Integer.toString(value);
	}
}
