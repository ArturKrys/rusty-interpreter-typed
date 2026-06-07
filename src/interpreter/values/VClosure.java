package interpreter.values;

import ast.ASTNode;
import interpreter.environment.Environment;
import interpreter.environment.Store;
import interpreter.errors.BindingException;
import interpreter.errors.InterpreterError;
import visitor.EvalVisitor;

public class VClosure implements IValue {
	private final String param;
	private final ASTNode body;
	private final Environment<IValue> capturedEnv;
	private final Store<IValue> store;

	public VClosure(String param,
			ASTNode body,
			Environment<IValue> capturedEnv,
			Store<IValue> store) {
		this.param = param;
		this.body = body;
		this.capturedEnv = capturedEnv;
		this.store = store;
	}

	public IValue apply(IValue arg) throws InterpreterError {
		Environment<IValue> callEnv = capturedEnv.beginScope();

		try {
			callEnv.assoc(param, arg);
		} catch (BindingException e) {
			throw new InterpreterError("closure: " + e.getMessage());
		}

		EvalVisitor callVisitor = new EvalVisitor(callEnv, store);
		return body.accept(callVisitor);
	}

	@Override
	public String toStr() {
		return "<closure:" + param + ">";
	}
}
