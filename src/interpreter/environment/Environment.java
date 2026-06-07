package interpreter.environment;

import java.util.HashMap;
import java.util.Map;

import interpreter.errors.BindingException;

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

	public void assoc(String id, E bind) throws BindingException {
		if (bindings.containsKey(id)) {
			throw new BindingException("Duplicate binding for " + id);
		}
		bindings.put(id, bind);
	}

	public E find(String id) throws BindingException {
		if (bindings.containsKey(id)) {
			return bindings.get(id);
		} else if (ancestor != null) {
			return ancestor.find(id);
		} else {
			throw new BindingException("Unbound identifier: " + id);
		}
	}
}
