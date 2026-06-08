package interpreter.values;

import interpreter.environment.MemoryManager;

public class VMutAddress implements IValue {
	private final MemoryManager.VirtualAddress address;

	public VMutAddress(MemoryManager.VirtualAddress address) {
		this.address = address;
	}

	public MemoryManager.VirtualAddress getAddress() {
		return address;
	}

	@Override
	public String toStr() {
		return "<mut " + address.toStr() + ">";
	}
}
