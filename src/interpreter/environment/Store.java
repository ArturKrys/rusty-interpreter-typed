package interpreter.environment;

import java.util.HashMap;
import java.util.Map;

import interpreter.values.IValue;

public class Store {
	private final Map<Integer, IValue> memory = new HashMap<>();
}
