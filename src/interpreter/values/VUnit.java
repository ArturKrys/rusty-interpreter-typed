package interpreter.values;

public class VUnit implements IValue {
	public VUnit() {
	}

	@Override
	public String toStr() {
		return "()";
	}

}
