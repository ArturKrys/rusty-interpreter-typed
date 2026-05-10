package interpreter.values;

import ast.ASTNode;
import interpreter.environment.Environment;
import interpreter.errors.InterpreterError;
import interpreter.environment.Store;
import visitor.EvalVisitor;

public class VClosure implements IValue {
	private final String param;
	private final ASTNode body;
	private final Environment<IValue> capturedEnv;
	private final Store store;

	public VClosure(String param, ASTNode body,
			Environment<IValue> capturedEnv, Store store) {
		this.param = param;
		this.body = body;
		this.capturedEnv = capturedEnv;
		this.store = store;
	}

	public IValue apply(IValue arg) throws InterpreterError {
		Environment<IValue> callEnv = capturedEnv.beginScope();
		callEnv.assoc(param, arg);

		EvalVisitor callVisitor = new EvalVisitor(callEnv, store);
		return body.accept(callVisitor);
	}

	@Override
	public String toStr() {
		return "<closure:" + param + ">";
	}
}
