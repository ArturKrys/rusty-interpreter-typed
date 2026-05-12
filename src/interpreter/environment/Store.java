package interpreter.environment;

import java.util.ArrayList;
import java.util.List;

import interpreter.errors.InterpreterError;

public final class Store<E> {
	private final List<E> heap;

	public Store() {
		heap = new ArrayList<>();
	}

	public int alloc(E value) {
		int address = heap.size();
		heap.add(value);
		return address;
	}

	public E get(int address) throws InterpreterError {
		checkAddress(address);
		return heap.get(address);
	}

	public void set(int address, E value) throws InterpreterError {
		checkAddress(address);
		heap.set(address, value);
	}

	private void checkAddress(int address) throws InterpreterError {
		if (address < 0 || address >= heap.size()) {
			throw new InterpreterError(
					"store: address out of bounds: " + address);
		}
	}
}
