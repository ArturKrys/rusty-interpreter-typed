package visitor;

import java.util.ArrayList;
import java.util.List;

import ast.ASTNode;
import ast.bindings.ASTLet;
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
import interpreter.environment.Store;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import interpreter.values.VBool;
import interpreter.values.VCell;
import interpreter.values.VClosure;
import interpreter.values.VInt;
import interpreter.values.VString;
import interpreter.values.VStructTuple;
import interpreter.values.VUnit;

public class EvalVisitor implements Visitor {
	private Environment<IValue> environment;
	private Store store;

	public EvalVisitor(Environment<IValue> environment, Store store) {
		this.environment = environment;
		this.store = store;
	}

	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	public IValue visitLet(ASTLet node) throws InterpreterError {
		this.environment = this.environment.beginScope();

		try {
			for (Bind binding : node.getDeclarations()) {
				String variableName = binding.getId();
				IValue evaluatedValue = binding.getExp().accept(this);

				this.environment.assoc(variableName, evaluatedValue);
			}
			IValue bodyResult = node.getBodyExpr().accept(this);

			return bodyResult;

		} finally {
			this.environment = this.environment.endScope();
		}
	}

	// ----------------------------------------------------
	// Control
	// ----------------------------------------------------
	public IValue visitIf(ASTIf node) throws InterpreterError {
		IValue evaluatedCondition = node.getCondition().accept(this);

		if (evaluatedCondition instanceof VBool) {
			boolean isConditionTrue = ((VBool) evaluatedCondition).getval();

			if (isConditionTrue) {
				return node.getThenBranch().accept(this);
			} else {
				return node.getElseBranch().accept(this);
			}
		}
		throw new InterpreterError("if: boolean expected, found: " + evaluatedCondition);
	}

	public IValue visitWhile(ASTWhile node) throws InterpreterError {
		return null;
	}

	public IValue visitSeq(ASTSeq node) throws InterpreterError {
		node.getLeft().accept(this);
		return node.getRight().accept(this);
	}

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	public IValue visitFun(ASTFun node) throws InterpreterError {
		return new VClosure(node.getParam(), node.getBody(), this.environment, this.store);
	}

	public IValue visitApp(ASTApp node) throws InterpreterError {
		IValue functionResult = node.getFunction().accept(this);
		IValue argumentResult = node.getArgument().accept(this);

		if (functionResult instanceof VClosure) {
			VClosure closure = (VClosure) functionResult;
			return closure.apply(argumentResult);
		} else {
			throw new InterpreterError("app: closure expected, found: " + functionResult);
		}
	}

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	public IValue visitPrint(ASTPrint node) throws InterpreterError {
		IValue result = node.getExpression().accept(this);
		System.out.print(result.toStr());
		return result; /* WARNING: bcz of this return, it will print 2 times */
	}

	public IValue visitPrintln(ASTPrintln node) throws InterpreterError {
		IValue result = node.getExpression().accept(this);
		System.out.println(result.toStr());
		return result; /* WARNING: bcz of this return, it will print 2 times */
	}

	// ----------------------------------------------------
	// Literals
	// ----------------------------------------------------
	public IValue visitInt(ASTInt node) throws InterpreterError {
		return new VInt(node.getValue());
	}

	public IValue visitString(ASTString node) throws InterpreterError {
		return new VString(node.getValue());
	}

	public IValue visitBool(ASTBool node) throws InterpreterError {
		return new VBool(node.getValue());
	}

	public IValue visitId(ASTId node) throws InterpreterError {
		return environment.find(node.getId());
	}

	public IValue visitUnit(ASTUnit node) throws InterpreterError {
		return new VUnit();
	}

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	public IValue visitAssign(ASTAssign node) throws InterpreterError {
		return null;
	}

	public IValue visitCell(ASTCell node) throws InterpreterError {
		return null;
	}

	public IValue visitDeref(ASTDeref node) throws InterpreterError {
		return null;
	}

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	public IValue visitPlus(ASTPlus node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VInt(leftOperand + rightOperand);
			}
			throw new InterpreterError("plus: integer expected, found: " + rightResult);
		}
		throw new InterpreterError("plus: integer expected, found: " + leftResult);
	}

	public IValue visitSub(ASTSub node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VInt(leftOperand - rightOperand);
			}
			throw new InterpreterError("sub: integer expected, found: " + rightResult);
		}
		throw new InterpreterError("sub: integer expected, found: " + leftResult);
	}

	public IValue visitMult(ASTMult node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VInt(leftOperand * rightOperand);
			}
			throw new InterpreterError("mult: integer expected, found: " + rightResult);
		}
		throw new InterpreterError("mult: integer expected, found: " + leftResult);
	}

	public IValue visitDiv(ASTDiv node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				if (rightOperand == 0) {
					throw new InterpreterError("div: division by zero");
				}

				return new VInt(leftOperand / rightOperand);
			}
			throw new InterpreterError("div: integer expected, found: " + rightResult);
		}
		throw new InterpreterError("div: integer expected, found: " + leftResult);
	}

	public IValue visitNeg(ASTNeg node) throws InterpreterError {
		IValue result = node.getExpr().accept(this);

		if (result instanceof VInt) {
			int operand = ((VInt) result).getval();
			return new VInt(-operand);
		}

		throw new InterpreterError("neg: integer expected, found: " + result);
	}

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	public IValue visitEq(ASTEq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();
				return new VBool(leftOperand == rightOperand);
			}
			throw new InterpreterError("eq: integer expected, found: " + rightResult);

		} else if (leftResult instanceof VBool) {
			if (rightResult instanceof VBool) {
				boolean leftOperand = ((VBool) leftResult).getval();
				boolean rightOperand = ((VBool) rightResult).getval();
				return new VBool(leftOperand == rightOperand);
			}
			throw new InterpreterError("eq: boolean expected, found: " + rightResult);
		}

		throw new InterpreterError("eq: integer or boolean expected, found: " + leftResult);
	}

	public IValue visitDif(ASTDif node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();
				return new VBool(leftOperand != rightOperand);
			}
			throw new InterpreterError("dif: integer expected, found: " + rightResult);

		} else if (leftResult instanceof VBool) {
			if (rightResult instanceof VBool) {
				boolean leftOperand = ((VBool) leftResult).getval();
				boolean rightOperand = ((VBool) rightResult).getval();
				return new VBool(leftOperand != rightOperand);
			}
			throw new InterpreterError("dif: boolean expected, found: " + rightResult);
		}

		throw new InterpreterError("dif: integer or boolean expected, found: " + leftResult);
	}

	public IValue visitGt(ASTGt node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VBool(leftOperand > rightOperand);
			}
			throw new InterpreterError("gt: integer expected, found: " + rightResult);
		}

		throw new InterpreterError("gt: integer expected, found: " + leftResult);
	}

	public IValue visitGteq(ASTGteq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VBool(leftOperand >= rightOperand);
			}
			throw new InterpreterError("gteq: integer expected, found: " + rightResult);
		}

		throw new InterpreterError("gteq: integer expected, found: " + leftResult);
	}

	public IValue visitLt(ASTLt node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VBool(leftOperand < rightOperand);
			}
			throw new InterpreterError("lt: integer expected, found: " + rightResult);
		}

		throw new InterpreterError("lt: integer expected, found: " + leftResult);
	}

	public IValue visitLteq(ASTLteq node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VInt) {
			if (rightResult instanceof VInt) {
				int leftOperand = ((VInt) leftResult).getval();
				int rightOperand = ((VInt) rightResult).getval();

				return new VBool(leftOperand <= rightOperand);
			}
			throw new InterpreterError("lteq: integer expected, found: " + rightResult);
		}

		throw new InterpreterError("lteq: integer expected, found: " + leftResult);
	}

	// ----------------------------------------------------
	// Logical Operations
	// ----------------------------------------------------
	public IValue visitAnd(ASTAnd node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VBool) {
			if (rightResult instanceof VBool) {
				boolean leftOperand = ((VBool) leftResult).getval();
				boolean rightOperand = ((VBool) rightResult).getval();

				return new VBool(leftOperand && rightOperand);
			}
			throw new InterpreterError("and: boolean expected, found: " + rightResult);
		}

		throw new InterpreterError("and: boolean expected, found: " + leftResult);
	}

	public IValue visitOr(ASTOr node) throws InterpreterError {
		IValue leftResult = node.getLeft().accept(this);
		IValue rightResult = node.getRight().accept(this);

		if (leftResult instanceof VBool) {
			if (rightResult instanceof VBool) {
				boolean leftOperand = ((VBool) leftResult).getval();
				boolean rightOperand = ((VBool) rightResult).getval();

				return new VBool(leftOperand || rightOperand);
			}
			throw new InterpreterError("or: boolean expected, found: " + rightResult);
		}

		throw new InterpreterError("or: boolean expected, found: " + leftResult);
	}

	public IValue visitNot(ASTNot node) throws InterpreterError {
		IValue result = node.getExpr().accept(this);

		if (result instanceof VBool) {
			boolean operand = ((VBool) result).getval();
			return new VBool(!operand);
		}

		throw new InterpreterError("not: boolean expected, found: " + result);
	}

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------
	public IValue visitStructPattern(ASTStructPattern node) throws InterpreterError {
		throw new InterpreterError("pettern: not to be evaluated");
	}

	public IValue visitStructTuple(ASTStructTuple node) throws InterpreterError {
		List<IValue> evaluatedElements = new ArrayList<>();

		for (ASTNode element : node.getElements()) {
			evaluatedElements.add(element.accept(this));
		}

		return new VStructTuple(node.getTag(), evaluatedElements);
	}

	public IValue visitMatch(ASTMatch node) throws InterpreterError {
		IValue target = node.getTargetExpression().accept(this);

		if (!(target instanceof VStructTuple)) {
			throw new InterpreterError("match: VStructTuple expected, found: " + target);
		}

		VStructTuple structTuple = (VStructTuple) target;
		ASTStructPattern pattern = node.getStructPattern();

		if (!structTuple.getTag().equals(pattern.getTag())) {
			return node.getOnMismatchExpression().accept(this);
		}

		List<String> patternVars = pattern.getBoundVariables();
		List<IValue> tupleValues = structTuple.getValues();

		if (patternVars.size() != tupleValues.size()) {
			return node.getOnMismatchExpression().accept(this);
		}

		this.environment = this.environment.beginScope();
		try {
			for (int i = 0; i < patternVars.size(); i++) {
				String varName = patternVars.get(i);

				// example of what is being skipped: #tag()
				if (!varName.equals("_")) {
					this.environment.assoc(varName, tupleValues.get(i));
				}
			}
			return node.getOnMatchExpression().accept(this);
		} finally {
			this.environment = this.environment.endScope();
		}
	}
}
