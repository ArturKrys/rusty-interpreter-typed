package interpreter.environment;

import interpreter.errors.MemoryPanicException;
import interpreter.values.IValue;

public class MemoryManager {

	public static class VirtualAddress {
		private final int address;

		VirtualAddress(int representation) {
			this.address = representation;
		}

		private int get() {
			return address;
		}
	}

	private static final int DEFAULT_SIZE = 10000;
	private static final String PANIC_PREFIX = "PANIC: ";

	private IValue[] store;
	private int stackPointer;
	private int limit;

	public MemoryManager(int size) {
		this.limit = size;
		this.store = new IValue[limit];
		this.stackPointer = limit;
	}

	public MemoryManager() {
		this(DEFAULT_SIZE);
	}

	public VirtualAddress push(IValue v) throws MemoryPanicException {
		if (stackPointer == 0) {
			throw new MemoryPanicException(PANIC_PREFIX + "out of memory");
		}
		stackPointer--;

		if (store[stackPointer] != null) {
			throw new MemoryPanicException(PANIC_PREFIX + "push overwrite");
		}
		store[stackPointer] = v;
		return new VirtualAddress(stackPointer);
	}

	public IValue pop() throws MemoryPanicException {
		if (stackPointer == limit) {
			throw new MemoryPanicException(PANIC_PREFIX + "read out of bounds");
		}

		IValue v = store[stackPointer];
		store[stackPointer] = null;
		stackPointer++;

		return v;
	}

	public IValue read(VirtualAddress address) throws MemoryPanicException {
		int addr = address.get();
		if (addr < stackPointer || addr >= limit) {
			throw new MemoryPanicException(PANIC_PREFIX + "read out of bounds");
		}
		if (store[addr] == null) {
			throw new MemoryPanicException(PANIC_PREFIX + "read empty location");
		}
		return store[addr];
	}

	public void write(VirtualAddress address, IValue v) throws MemoryPanicException {
		int addr = address.get();
		if (addr < stackPointer || addr >= limit) {
			throw new MemoryPanicException(PANIC_PREFIX + "write out of bounds");
		}
		store[addr] = v;
	}
}
