package interpreter.environment;

import java.util.*;

import interpreter.errors.InterpreterError;

public class Environment<E> {
	private Environment<E> ancestor;
	private Map<String, E> bindings;

	public Environment() {
		ancestor = null;
		bindings = new HashMap<String, E>();
	}

	public Environment(Environment<E> ancestor) {
		this.ancestor = ancestor;
		this.bindings = new HashMap<String, E>();
	}

	public Environment<E> beginScope() {
		return new Environment<E>(this);
	}

	public Environment<E> endScope() {
		return ancestor;
	}

	public void assoc(String id, E bind) throws InterpreterError {
		if (bindings.containsKey(id)) {
			throw new InterpreterError("Duplicate binding for " + id);
		}
		bindings.put(id, bind);
	}

	public E find(String id) throws InterpreterError {
		if (bindings.containsKey(id)) {
			return bindings.get(id);
		} else if (ancestor != null) {
			return ancestor.find(id);
		} else {
			throw new InterpreterError("Unbound identifier: " + id);
		}
	}
}
