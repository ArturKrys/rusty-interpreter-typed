package interpreter.values;

public class VCell implements IValue {
	private final int location;

	public VCell(int location) {
		this.location = location;
	}

	public int getLocation() {
		return location;
	}

	@Override
	public String toStr() {
		return "cell@" + location;
	}
}
