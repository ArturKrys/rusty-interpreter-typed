package interpreter.environment;

import java.util.ArrayList;
import java.util.List;

import interpreter.errors.InterpreterError;
import interpreter.values.IValue;

public class Store {
	private final List<IValue> heap = new ArrayList<>();

	public int alloc(IValue value) {
		int address = heap.size();
		heap.add(value);
		return address;
	}

	public IValue get(int address) throws InterpreterError {
		if (address < 0 || address >= heap.size()) {
			throw new InterpreterError("store: address out of bounds: " + address);
		}
		return heap.get(address);
	}

	public void set(int address, IValue value) throws InterpreterError {
		if (address < 0 || address >= heap.size()) {
			throw new InterpreterError("store: address out of bounds: " + address);
		}
		heap.set(address, value);
	}
}
