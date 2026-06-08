package visitor;

import java.util.ArrayList;
import java.util.List;

import ast.ASTNode;
import ast.bindings.ASTLet;
import ast.bindings.ASTTypeDefinition;
import ast.control.ASTIf;
import ast.control.ASTSeq;
import ast.control.ASTWhile;
import ast.functions.ASTApp;
import ast.functions.ASTFun;
import ast.io.ASTPrint;
import ast.io.ASTPrintln;
import ast.literals.ASTBool;
import ast.literals.ASTId;
import ast.literals.ASTInt;
import ast.literals.ASTString;
import ast.literals.ASTUnit;
import ast.memory.ASTAddr;
import ast.memory.ASTAssign;
import ast.memory.ASTCell;
import ast.memory.ASTDeref;
import ast.operators.ASTAnd;
import ast.operators.ASTDif;
import ast.operators.ASTDiv;
import ast.operators.ASTEq;
import ast.operators.ASTGt;
import ast.operators.ASTGteq;
import ast.operators.ASTLt;
import ast.operators.ASTLteq;
import ast.operators.ASTMult;
import ast.operators.ASTNeg;
import ast.operators.ASTNot;
import ast.operators.ASTOr;
import ast.operators.ASTPlus;
import ast.operators.ASTSub;
import ast.structs.ASTMatch;
import ast.structs.ASTStructPattern;
import ast.structs.ASTStructTuple;
import interpreter.environment.Bind;
import interpreter.environment.Environment;
import interpreter.environment.MemoryManager;
import interpreter.environment.Store;
import interpreter.errors.BindingException;
import interpreter.errors.InterpreterError;
import interpreter.errors.MemoryPanicException;
import interpreter.values.IValue;
import interpreter.values.VBool;
import interpreter.values.VCell;
import interpreter.values.VClosure;
import interpreter.values.VInt;
import interpreter.values.VMutAddress;
import interpreter.values.VString;
import interpreter.values.VStructTuple;
import interpreter.values.VUnit;

public class EvalVisitor implements Visitor<IValue, InterpreterError> {
	private Environment<IValue> environment;
	private Store<IValue> store;
	private MemoryManager memory;

	public EvalVisitor(Environment<IValue> environment, Store<IValue> store) {
		this(environment, store, new MemoryManager());
	}

	public EvalVisitor(Environment<IValue> environment, Store<IValue> store, MemoryManager memory) {
		this.environment = environment;
		this.store = store;
		this.memory = memory;
	}

	private IValue lookupRawIdentifier(String id) throws InterpreterError {
		try {
			return environment.find(id);
		} catch (BindingException e) {
			throw new InterpreterError("id: " + e.getMessage());
		}
	}

	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	@Override
	public IValue visitLet(ASTLet node) throws InterpreterError {
		this.environment = this.environment.beginScope();
		int mutableBindings = 0;

		try {
			for (Bind binding : node.getDeclarations()) {
				String variableName = binding.getId();
				IValue evaluatedValue = binding.getExp().accept(this);

				if (!variableName.equals("_")) {
					if (binding.isMutable()) {
						MemoryManager.VirtualAddress address = this.memory.push(evaluatedValue);
						mutableBindings++;
						this.environment.assoc(variableName, new VMutAddress(address));
					} else {
						this.environment.assoc(variableName, evaluatedValue);
					}
				}
			}
			return node.getBodyExpr().accept(this);

		} catch (BindingException | MemoryPanicException e) {
			throw new InterpreterError("let: " + e.getMessage());
		} finally {
			try {
				for (int i = 0; i < mutableBindings; i++) {
					this.memory.pop();
				}
			} catch (MemoryPanicException e) {
				throw new InterpreterError("let cleanup: " + e.getMessage());
			} finally {
				this.environment = this.environment.endScope();
			}
		}
	}

	@Override
	public IValue visitTypeDefinition(ASTTypeDefinition node) throws InterpreterError {
		return node.getBodyExpr().accept(this);
	}

	// ----------------------------------------------------
	// Control
	// ----------------------------------------------------
	@Override
	public IValue visitIf(ASTIf node) throws InterpreterError {
		IValue evaluatedCondition = node.getCondition().accept(this);

		if (!(evaluatedCondition instanceof VBool))
			throw new InterpreterError("if: boolean expected, found: " + evaluatedCondition);

		boolean isConditionTrue = ((VBool) evaluatedCondition).getval();
		return isConditionTrue ? node.getThenBranch().accept(this) : node.getElseBranch().accept(this);
	}

	@Override
	public IValue visitWhile(ASTWhile node) throws InterpreterError {
		IValue evaluatedCondition = node.getCondition().accept(this);

		if (!(evaluatedCondition instanceof VBool)) {
			throw new InterpreterError(
					"while: boolean expected, found: " + evaluatedCondition);
		}

		while (((VBool) evaluatedCondition).getval()) {
			node.getBody().accept(this);

			evaluatedCondition = node.getCondition().accept(this);

			if (!(evaluatedCondition instanceof VBool)) {
				throw new InterpreterError(
						"while: boolean expected, found: " + evaluatedCondition);
			}
		}

		return new VUnit();
	}

	@Override
	public IValue visitSeq(ASTSeq node) throws InterpreterError {
		node.getLeft().accept(this);
		return node.getRight().accept(this);
	}

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	@Override
	public IValue visitFun(ASTFun node) throws InterpreterError {
		return new VClosure(node.getParam(), node.getBodyExpr(), this.environment, this.store, this.memory);
	}

	@Override
	public IValue visitApp(ASTApp node) throws InterpreterError {
		IValue functionResult = node.getFunction().accept(this);
		IValue argumentResult = node.getArgument().accept(this);

		if (!(functionResult instanceof VClosure))
			throw new InterpreterError("app: closure expected, found: " + functionResult);

		VClosure closure = (VClosure) functionResult;
		return closure.apply(argumentResult);
	}

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	@Override
	public IValue visitPrint(ASTPrint node) throws InterpreterError {
		IValue result = node.getExpression().accept(this);
		System.out.print(result.toStr());
		return result;
	}

	@Override
	public IValue visitPrintln(ASTPrintln node) throws InterpreterError {
		IValue result = node.getExpression().accept(this);
		System.out.println(result.toStr());
		return result;
	}

	// ----------------------------------------------------
	// Literals
	// ----------------------------------------------------
	@Override
	public IValue visitInt(ASTInt node) throws InterpreterError {
		return new VInt(node.getValue());
	}

	@Override
	public IValue visitString(ASTString node) throws InterpreterError {
		return new VString(node.getValue());
	}

	@Override
	public IValue visitBool(ASTBool node) throws InterpreterError {
		return new VBool(node.getValue());
	}

	@Override
	public IValue visitId(ASTId node) throws InterpreterError {
		try {
			IValue value = environment.find(node.getId());
			if (value instanceof VMutAddress) {
				return memory.read(((VMutAddress) value).getAddress());
			}
			return value;
		} catch (BindingException | MemoryPanicException e) {
			throw new InterpreterError("id: " + e.getMessage());
		}
	}

	@Override
	public IValue visitUnit(ASTUnit node) throws InterpreterError {
		return new VUnit();
	}

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	@Override
	public IValue visitAddr(ASTAddr node) throws InterpreterError {
		if (!(node.getTarget() instanceof ASTId)) {
			throw new InterpreterError("addr: mutable identifier expected");
		}

		ASTId target = (ASTId) node.getTarget();
		IValue value = lookupRawIdentifier(target.getId());

		if (!(value instanceof VMutAddress)) {
			throw new InterpreterError("addr: mutable identifier expected, found: " + target.getId());
		}

		return ((VMutAddress) value).getAddress();
	}

	@Override
	public IValue visitAssign(ASTAssign node) throws InterpreterError {
		if (node.getTarget() instanceof ASTId) {
			ASTId targetId = (ASTId) node.getTarget();
			IValue rawTarget = lookupRawIdentifier(targetId.getId());
			IValue newValue = node.getValue().accept(this);

			try {
				if (rawTarget instanceof VMutAddress) {
					this.memory.write(((VMutAddress) rawTarget).getAddress(), newValue);
					return newValue;
				}

				if (rawTarget instanceof MemoryManager.VirtualAddress) {
					this.memory.write((MemoryManager.VirtualAddress) rawTarget, newValue);
					return newValue;
				}

				if (rawTarget instanceof VCell) {
					store.set(((VCell) rawTarget).getLocation(), newValue);
					return newValue;
				}
			} catch (MemoryPanicException e) {
				throw new InterpreterError("assign: " + e.getMessage());
			}
		}

		IValue target = node.getTarget().accept(this);
		IValue newValue = node.getValue().accept(this);

		if (target instanceof VCell) {
			int location = ((VCell) target).getLocation();
			store.set(location, newValue);
			return newValue;
		}

		if (target instanceof MemoryManager.VirtualAddress) {
			try {
				this.memory.write((MemoryManager.VirtualAddress) target, newValue);
				return newValue;
			} catch (MemoryPanicException e) {
				throw new InterpreterError("assign: " + e.getMessage());
			}
		}

		throw new InterpreterError("assign: assignable target expected, found: " + target);
	}

	@Override
	public IValue visitCell(ASTCell node) throws InterpreterError {
		IValue initialValue = node.getInitialValue().accept(this);
		int location = store.alloc(initialValue);
		return new VCell(location);
	}

	@Override
	public IValue visitDeref(ASTDeref node) throws InterpreterError {
		IValue reference = node.getReference().accept(this);

		if (reference instanceof VCell) {
			int location = ((VCell) reference).getLocation();
			return store.get(location);
		}

		if (reference instanceof MemoryManager.VirtualAddress) {
			try {
				return memory.read((MemoryManager.VirtualAddress) reference);
			} catch (MemoryPanicException e) {
				throw new InterpreterError("deref: " + e.getMessage());
			}
		}

		throw new InterpreterError("deref: reference expected, found: " + reference);
	}

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	@Override
	public IValue visitPlus(ASTPlus node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VString || rightResult instanceof VString) {
			return new VString(leftResult.toStr() + rightResult.toStr());
		}

		if (leftResult instanceof VInt && rightResult instanceof VInt) {
			int l = ((VInt) leftResult).getval();
			int r = ((VInt) rightResult).getval();
			return new VInt(l + r);
		}

		throw new InterpreterError("plus: integer or string expected, found: "
				+ leftResult + " and " + rightResult);
	}

	@Override
	public IValue visitSub(ASTSub node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("sub: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("sub: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VInt(leftOperand - rightOperand);
	}

	@Override
	public IValue visitMult(ASTMult node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("mult: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("mult: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VInt(leftOperand * rightOperand);
	}

	@Override
	public IValue visitDiv(ASTDiv node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("div: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("div: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();

		if (rightOperand == 0)
			throw new InterpreterError("div: division by zero");

		return new VInt(leftOperand / rightOperand);
	}

	@Override
	public IValue visitNeg(ASTNeg node) throws InterpreterError {
		IValue result = node.getExpr().accept(this);

		if (!(result instanceof VInt))
			throw new InterpreterError("neg: integer expected, found: " + result);

		int operand = ((VInt) result).getval();
		return new VInt(-operand);
	}

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	@Override
	public IValue visitEq(ASTEq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (!(rightResult instanceof VInt))
				throw new InterpreterError("eq: integer expected, found: " + rightResult);
			int leftOperand = ((VInt) leftResult).getval();
			int rightOperand = ((VInt) rightResult).getval();
			return new VBool(leftOperand == rightOperand);

		} else if (leftResult instanceof VBool) {
			if (!(rightResult instanceof VBool))
				throw new InterpreterError("eq: boolean expected, found: " + rightResult);
			boolean leftOperand = ((VBool) leftResult).getval();
			boolean rightOperand = ((VBool) rightResult).getval();
			return new VBool(leftOperand == rightOperand);
		}

		throw new InterpreterError("eq: integer or boolean expected, found: " + leftResult);
	}

	@Override
	public IValue visitDif(ASTDif node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (!(rightResult instanceof VInt))
				throw new InterpreterError("dif: integer expected, found: " + rightResult);
			int leftOperand = ((VInt) leftResult).getval();
			int rightOperand = ((VInt) rightResult).getval();
			return new VBool(leftOperand != rightOperand);

		} else if (leftResult instanceof VBool) {
			if (!(rightResult instanceof VBool))
				throw new InterpreterError("dif: boolean expected, found: " + rightResult);
			boolean leftOperand = ((VBool) leftResult).getval();
			boolean rightOperand = ((VBool) rightResult).getval();
			return new VBool(leftOperand != rightOperand);
		}

		throw new InterpreterError("dif: integer or boolean expected, found: " + leftResult);
	}

	@Override
	public IValue visitGt(ASTGt node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("gt: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("gt: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VBool(leftOperand > rightOperand);
	}

	@Override
	public IValue visitGteq(ASTGteq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("gteq: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("gteq: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VBool(leftOperand >= rightOperand);
	}

	@Override
	public IValue visitLt(ASTLt node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("lt: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("lt: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VBool(leftOperand < rightOperand);
	}

	@Override
	public IValue visitLteq(ASTLteq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VInt))
			throw new InterpreterError("lteq: integer expected, found: " + leftResult);
		if (!(rightResult instanceof VInt))
			throw new InterpreterError("lteq: integer expected, found: " + rightResult);

		int leftOperand = ((VInt) leftResult).getval();
		int rightOperand = ((VInt) rightResult).getval();
		return new VBool(leftOperand <= rightOperand);
	}

	// ----------------------------------------------------
	// Logical Operations
	// ----------------------------------------------------
	@Override
	public IValue visitAnd(ASTAnd node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VBool))
			throw new InterpreterError("and: boolean expected, found: " + leftResult);
		if (!(rightResult instanceof VBool))
			throw new InterpreterError("and: boolean expected, found: " + rightResult);

		boolean leftOperand = ((VBool) leftResult).getval();
		boolean rightOperand = ((VBool) rightResult).getval();
		return new VBool(leftOperand && rightOperand);
	}

	@Override
	public IValue visitOr(ASTOr node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (!(leftResult instanceof VBool))
			throw new InterpreterError("or: boolean expected, found: " + leftResult);
		if (!(rightResult instanceof VBool))
			throw new InterpreterError("or: boolean expected, found: " + rightResult);

		boolean leftOperand = ((VBool) leftResult).getval();
		boolean rightOperand = ((VBool) rightResult).getval();
		return new VBool(leftOperand || rightOperand);
	}

	@Override
	public IValue visitNot(ASTNot node) throws InterpreterError {
		IValue result = node.getExpr().accept(this);

		if (!(result instanceof VBool))
			throw new InterpreterError("not: boolean expected, found: " + result);

		boolean operand = ((VBool) result).getval();
		return new VBool(!operand);
	}

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------
	@Override
	public IValue visitStructPattern(ASTStructPattern node) throws InterpreterError {
		throw new InterpreterError("pattern: not to be evaluated");
	}

	@Override
	public IValue visitStructTuple(ASTStructTuple node) throws InterpreterError {
		List<IValue> evaluatedElements = new ArrayList<>();

		for (ASTNode element : node.getElements()) {
			evaluatedElements.add(element.accept(this));
		}

		return new VStructTuple(node.getTag(), evaluatedElements);
	}

	@Override
	public IValue visitMatch(ASTMatch node) throws InterpreterError {
		IValue targetVal = node.getTargetExpression().accept(this);

		if (!(targetVal instanceof VStructTuple)) {
			throw new InterpreterError("match: VStructTuple expected, found: \\\" + target");
		}
		VStructTuple evaluatedTuple = (VStructTuple) targetVal;

		List<ASTStructPattern> patterns = node.getPatterns();
		List<ASTNode> branches = node.getBranches();

		for (int i = 0; i < patterns.size(); i++) {
			ASTStructPattern pattern = patterns.get(i);

			if (pattern.getTag().equals(evaluatedTuple.getTag())) {

				environment = environment.beginScope();

				List<String> patternIdentifiers = pattern.getBoundVariables();
				List<IValue> tupleValues = evaluatedTuple.getValues();

				if (patternIdentifiers.size() <= tupleValues.size()) {
					for (int j = 0; j < patternIdentifiers.size(); j++) {
						try {
							environment.assoc(patternIdentifiers.get(j), tupleValues.get(j));
						} catch (BindingException e) {
							throw new InterpreterError("match: " + e.getMessage());
						}
					}
				} else {
					throw new InterpreterError("match mismatch: pattern expects " +
							patternIdentifiers.size() + " values, but runtime tuple only has " + tupleValues.size());
				}

				IValue result = branches.get(i).accept(this);
				environment = environment.endScope();

				return result;
			}
		}

		throw new InterpreterError("match: no pattern found for tag " + evaluatedTuple.getTag());
	}
}
