package interpreter.values;

import ast.ASTNode;
import interpreter.environment.Environment;
import interpreter.environment.MemoryManager;
import interpreter.environment.Store;
import interpreter.errors.BindingException;
import interpreter.errors.InterpreterError;
import visitor.EvalVisitor;

public class VClosure implements IValue {
	private final String param;
	private final ASTNode body;
	private final Environment<IValue> capturedEnv;
	private final Store<IValue> store;
	private final MemoryManager memory;

	public VClosure(String param,
			ASTNode body,
			Environment<IValue> capturedEnv,
			Store<IValue> store) {
		this(param, body, capturedEnv, store, new MemoryManager());
	}

	public VClosure(String param,
			ASTNode body,
			Environment<IValue> capturedEnv,
			Store<IValue> store,
			MemoryManager memory) {
		this.param = param;
		this.body = body;
		this.capturedEnv = capturedEnv;
		this.store = store;
		this.memory = memory;
	}

	public IValue apply(IValue arg) throws InterpreterError {
		Environment<IValue> callEnv = capturedEnv.beginScope();

		try {
			if (!param.equals("_")) {
				callEnv.assoc(param, arg);
			}
		} catch (BindingException e) {
			throw new InterpreterError("closure: " + e.getMessage());
		}

		EvalVisitor callVisitor = new EvalVisitor(callEnv, store, memory);
		return body.accept(callVisitor);
	}

	@Override
	public String toStr() {
		return "<closure:" + param + ">";
	}
}
