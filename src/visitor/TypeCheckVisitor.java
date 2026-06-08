package visitor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import ast.type.ASTType;
import ast.type.ASTTypeAddr;
import ast.type.ASTTypeArrow;
import ast.type.ASTTypeBool;
import ast.type.ASTTypeEnum;
import ast.type.ASTTypeId;
import ast.type.ASTTypeInt;
import ast.type.ASTTypeMut;
import ast.type.ASTTypeRef;
import ast.type.ASTTypeString;
import ast.type.ASTTypeUnit;
import interpreter.environment.Bind;
import interpreter.environment.Environment;
import interpreter.errors.BindingException;
import interpreter.errors.TypeCheckError;

public class TypeCheckVisitor implements Visitor<ASTType, TypeCheckError> {
	private Environment<ASTType> typeEnvironment;

	public TypeCheckVisitor(Environment<ASTType> typeEnvironment) {
		this.typeEnvironment = typeEnvironment;
	}

	// ----------------------------------------------------
	// Helper methods
	// ----------------------------------------------------
	private ASTType leastUpperBound(ASTType t1, ASTType t2) throws TypeCheckError {
		t1 = unfold(t1);
		t2 = unfold(t2);

		// 1. If they are strictly equal, that's the LUB.
		if (t1.eqt(t2)) {
			return t1;
		}

		// 2. If one is a subtype of the other, the supertype is the LUB.
		if (isSubtype(t1, t2))
			return t2;
		if (isSubtype(t2, t1))
			return t1;

		// 3. If both are Enums, we calculate their Union (Depth & Width)
		if (t1 instanceof ASTTypeEnum && t2 instanceof ASTTypeEnum) {
			ASTTypeEnum e1 = (ASTTypeEnum) t1;
			ASTTypeEnum e2 = (ASTTypeEnum) t2;

			Map<String, List<ASTType>> mergedOptions = new java.util.LinkedHashMap<>(e1.getOptions());

			for (Map.Entry<String, List<ASTType>> entry : e2.getOptions().entrySet()) {
				String tag = entry.getKey();
				if (mergedOptions.containsKey(tag)) {
					List<ASTType> p1 = mergedOptions.get(tag);
					List<ASTType> p2 = entry.getValue();

					int minLen = Math.min(p1.size(), p2.size());
					List<ASTType> commonPrefix = new java.util.ArrayList<>();
					for (int j = 0; j < minLen; j++) {
						if (!p1.get(j).eqt(p2.get(j))) {
							throw new TypeCheckError("Parameter conflict in variant '" + tag + "'");
						}
						commonPrefix.add(p1.get(j));
					}
					mergedOptions.put(tag, commonPrefix);
				} else {
					mergedOptions.put(tag, entry.getValue());
				}
			}
			return new ASTTypeEnum(mergedOptions);
		}

		throw new TypeCheckError("Types " + t1.toStr() + " and " + t2.toStr() + " are incompatible.");
	}

	private ASTType unfold(ASTType type) throws TypeCheckError {
		if (type instanceof ASTTypeId) {
			ASTTypeId typeId = (ASTTypeId) type;
			try {
				ASTType resolvedType = this.typeEnvironment.find(typeId.toStr());
				return unfold(resolvedType);

			} catch (BindingException e) {
				throw new TypeCheckError("Undefined type alias: " + typeId.toStr());
			}
		}
		return type;
	}

	private boolean isSubtype(ASTType t1, ASTType t2) throws TypeCheckError {
		t1 = unfold(t1);
		t2 = unfold(t2);

		// 1. Base primitive equality
		if (t1.eqt(t2)) {
			return true;
		}

		// 2. Arrow (Function) Subtyping
		if (t1 instanceof ASTTypeArrow && t2 instanceof ASTTypeArrow) {
			ASTTypeArrow a1 = (ASTTypeArrow) t1;
			ASTTypeArrow a2 = (ASTTypeArrow) t2;
			// Contravariant domain, Covariant codomain
			return isSubtype(a2.getDomain(), a1.getDomain()) &&
					isSubtype(a1.getCodomain(), a2.getCodomain());
		}

		// 3. Addr Stack Alias Subtyping
		if (t1 instanceof ASTTypeAddr && t2 instanceof ASTTypeAddr) {
			ASTTypeAddr a1 = (ASTTypeAddr) t1;
			ASTTypeAddr a2 = (ASTTypeAddr) t2;
			// References are strictly Invariant (must be subtypes in both directions)
			return isSubtype(a1.getInnerType(), a2.getInnerType()) &&
					isSubtype(a2.getInnerType(), a1.getInnerType());
		}

		// 4. ref Heap Mutable Cell Subtyping
		if (t1 instanceof ASTTypeRef && t2 instanceof ASTTypeRef) {
			ASTTypeRef r1 = (ASTTypeRef) t1;
			ASTTypeRef r2 = (ASTTypeRef) t2;
			return isSubtype(r1.getType(), r2.getType()) &&
					isSubtype(r2.getType(), r1.getType());
		}

		// 5. Enum (Labeled Sum) Subtyping
		if (t1 instanceof ASTTypeEnum && t2 instanceof ASTTypeEnum) {
			ASTTypeEnum e1 = (ASTTypeEnum) t1;
			ASTTypeEnum e2 = (ASTTypeEnum) t2;

			for (String tag : e1.getOptions().keySet()) {
				if (!e2.getOptions().containsKey(tag))
					return false;

				List<ASTType> p1 = e1.getOptions().get(tag);
				List<ASTType> p2 = e2.getOptions().get(tag);

				if (p1.size() < p2.size())
					return false;

				for (int j = 0; j < p2.size(); j++) {
					if (!isSubtype(p1.get(j), p2.get(j)))
						return false;
				}
			}
			return true;
		}

		return false;
	}

	private ASTType typeOfAssignable(ASTNode target) throws TypeCheckError {
		if (target instanceof ASTId) {
			String id = ((ASTId) target).getId();
			try {
				ASTType rawType = this.typeEnvironment.find(id);

				if (rawType instanceof ASTTypeMut) {
					return ((ASTTypeMut) rawType).getInnerType();
				}

				ASTType unfoldedRawType = unfold(rawType);
				if (unfoldedRawType instanceof ASTTypeRef) {
					return ((ASTTypeRef) unfoldedRawType).getType();
				}
				if (unfoldedRawType instanceof ASTTypeAddr) {
					return ((ASTTypeAddr) unfoldedRawType).getInnerType();
				}
			} catch (BindingException e) {
				throw new TypeCheckError("assign: " + e.getMessage());
			}
		}

		ASTType targetType = unfold(target.accept(this));
		if (targetType instanceof ASTTypeRef) {
			return ((ASTTypeRef) targetType).getType();
		}
		if (targetType instanceof ASTTypeAddr) {
			return ((ASTTypeAddr) targetType).getInnerType();
		}

		throw new TypeCheckError("assign: assignable target expected, got " + targetType.toStr());
	}

	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	@Override
	public ASTType visitLet(ASTLet node) throws TypeCheckError {
		this.typeEnvironment = this.typeEnvironment.beginScope();

		try {
			for (Bind bind : node.getDeclarations()) {
				// This enables the RHS to recursively reference the identifier.
				if (bind.getType() != null && !bind.getId().equals("_")) {
					ASTType declaredType = bind.getType();
					ASTType preBindingType = bind.isMutable() ? new ASTTypeMut(declaredType) : declaredType;
					this.typeEnvironment.assoc(bind.getId(), preBindingType);
				}

				// 2. Now it is safe to evaluate the right-hand side expression
				ASTType initType = bind.getExp().accept(this);
				ASTType finalType = initType;

				// 3. Verify subtyping and bind inferred types if no explicit type was given
				if (bind.getType() != null) {
					ASTType declaredType = bind.getType();

					if (!isSubtype(initType, declaredType)) {
						throw new TypeCheckError("Let: type mismatch for '" + bind.getId() +
								"'. Type '" + unfold(initType).toStr() +
								"' is not a subtype of '" + unfold(declaredType).toStr() + "'");
					}

				} else if (!bind.getId().equals("_")) {
					// If there was no explicit type annotation, we bind the inferred type NOW.
					// (This means non-annotated functions cannot be easily recursive).
					ASTType bindingType = bind.isMutable() ? new ASTTypeMut(finalType) : finalType;
					this.typeEnvironment.assoc(bind.getId(), bindingType);
				}
			}

			return node.getBodyExpr().accept(this);

		} catch (BindingException e) {
			throw new TypeCheckError("Let: binding error " + e.getMessage());
		} finally {
			this.typeEnvironment = this.typeEnvironment.endScope();
		}
	}

	@Override
	public ASTType visitTypeDefinition(ASTTypeDefinition node) throws TypeCheckError {
		this.typeEnvironment = this.typeEnvironment.beginScope();

		try {
			for (Entry<String, ASTType> entry : node.getTypeDefinitions().entrySet()) {
				this.typeEnvironment.assoc(entry.getKey(), entry.getValue());
			}

			return node.getBodyExpr().accept(this);

		} catch (BindingException e) {
			throw new TypeCheckError("Type error: " + e.getMessage());
		} finally {
			this.typeEnvironment = this.typeEnvironment.endScope();
		}
	}

	// ----------------------------------------------------
	// Control
	// ----------------------------------------------------
	@Override
	public ASTType visitIf(ASTIf node) throws TypeCheckError {
		ASTType conditionType = unfold(node.getCondition().accept(this));
		if (!(conditionType instanceof ASTTypeBool)) {
			throw new TypeCheckError("if: condition must be bool, got: " + conditionType.toStr());
		}

		ASTType thenType = node.getThenBranch().accept(this);
		ASTType elseType = node.getElseBranch().accept(this);

		try {
			return leastUpperBound(thenType, elseType);
		} catch (TypeCheckError e) {
			throw new TypeCheckError("if: branch types differ or are incompatible - " + e.getMessage());
		}
	}

	@Override
	public ASTType visitWhile(ASTWhile node) throws TypeCheckError {
		ASTType conditionType = unfold(node.getCondition().accept(this));
		if (!(conditionType instanceof ASTTypeBool)) {
			throw new TypeCheckError("while: condition must be bool, got: " + conditionType.toStr());
		}

		node.getBody().accept(this);
		return ASTTypeUnit.TUNIT;
	}

	@Override
	public ASTType visitSeq(ASTSeq node) throws TypeCheckError {
		node.getLeft().accept(this);
		return node.getRight().accept(this);
	}

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	@Override
	public ASTType visitFun(ASTFun node) throws TypeCheckError {
		try {
			this.typeEnvironment = typeEnvironment.beginScope();

			typeEnvironment.assoc(node.getParam(), node.getParamType());
			ASTType bodyType = node.getBodyExpr().accept(this);

			this.typeEnvironment = typeEnvironment.endScope();

			return new ASTTypeArrow(node.getParamType(), bodyType);
		} catch (BindingException e) {
			throw new TypeCheckError("fun: " + e.getMessage());
		}
	}

	@Override
	public ASTType visitApp(ASTApp node) throws TypeCheckError {
		ASTType funType = unfold(node.getFunction().accept(this));
		if (!(funType instanceof ASTTypeArrow)) {
			throw new TypeCheckError(
					"app: Expected a function type in application, but got: " + funType.toStr());
		}

		ASTTypeArrow arrowType = (ASTTypeArrow) funType;

		ASTType argType = unfold(node.getArgument().accept(this));
		ASTType expectedDomain = unfold(arrowType.getDomain());

		if (!isSubtype(argType, expectedDomain)) {
			throw new TypeCheckError(
					"app: Argument type " + argType.toStr()
							+ " is not a subtype of parameter type " + arrowType.getDomain().toStr());
		}

		return arrowType.getCodomain();
	}

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitPrint(ASTPrint node) throws TypeCheckError {
		node.getExpression().accept(this);
		return ASTTypeUnit.TUNIT;
	}

	@Override
	public ASTType visitPrintln(ASTPrintln node) throws TypeCheckError {
		node.getExpression().accept(this);
		return ASTTypeUnit.TUNIT;
	}

	// ----------------------------------------------------
	// Literals
	// ----------------------------------------------------
	@Override
	public ASTType visitInt(ASTInt node) throws TypeCheckError {
		return ASTTypeInt.TINT;
	}

	@Override
	public ASTType visitString(ASTString node) throws TypeCheckError {
		return new ASTTypeString();
	}

	@Override
	public ASTType visitBool(ASTBool node) throws TypeCheckError {
		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitUnit(ASTUnit node) throws TypeCheckError {
		return ASTTypeUnit.TUNIT;
	}

	@Override
	public ASTType visitId(ASTId node) throws TypeCheckError {
		try {
			ASTType type = this.typeEnvironment.find(node.getId());
			if (type instanceof ASTTypeMut) {
				return ((ASTTypeMut) type).getInnerType();
			}
			return type;
		} catch (BindingException e) {
			throw new TypeCheckError("id: " + e.getMessage());
		}
	}

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	@Override
	public ASTType visitAddr(ASTAddr node) throws TypeCheckError {
		if (!(node.getTarget() instanceof ASTId)) {
			throw new TypeCheckError("addr: mutable identifier expected");
		}

		String id = ((ASTId) node.getTarget()).getId();
		try {
			ASTType rawType = this.typeEnvironment.find(id);
			if (!(rawType instanceof ASTTypeMut)) {
				throw new TypeCheckError("addr: mutable identifier expected, got: " + id);
			}

			return new ASTTypeAddr(((ASTTypeMut) rawType).getInnerType());
		} catch (BindingException e) {
			throw new TypeCheckError("addr: " + e.getMessage());
		}
	}

	@Override
	public ASTType visitAssign(ASTAssign node) throws TypeCheckError {
		ASTType targetType = typeOfAssignable(node.getTarget());
		ASTType valueType = node.getValue().accept(this);

		ASTType unfoldedActual = unfold(targetType);
		ASTType unfoldedExpected = unfold(valueType);

		if (!isSubtype(unfoldedActual, unfoldedExpected)) {
			throw new TypeCheckError("assign: " + "expected " + valueType.toStr() + ", got " + targetType.toStr());
		}
		return targetType;
	}

	@Override
	public ASTType visitCell(ASTCell node) throws TypeCheckError {
		return new ASTTypeRef(node.getInitialValue().accept(this));
	}

	@Override
	public ASTType visitDeref(ASTDeref node) throws TypeCheckError {
		ASTType referenceType = unfold(node.getReference().accept(this));

		if (referenceType instanceof ASTTypeRef) {
			return ((ASTTypeRef) referenceType).getType();
		}
		if (referenceType instanceof ASTTypeAddr) {
			return ((ASTTypeAddr) referenceType).getInnerType();
		}

		throw new TypeCheckError("deref: reference expected, got: " + referenceType.toStr());
	}

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitPlus(ASTPlus node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (leftType instanceof ASTTypeString)
			return leftType;
		if (rightType instanceof ASTTypeString)
			return rightType;
		if (leftType instanceof ASTTypeInt && rightType instanceof ASTTypeInt) {
			return leftType;
		}

		throw new TypeCheckError("plus: integer or string expected, found: "
				+ leftType + " and " + rightType);
	}

	@Override
	public ASTType visitSub(ASTSub node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("sub: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("sub: right operand not int, found: " + rightType.toStr());

		return ASTTypeInt.TINT;
	}

	@Override
	public ASTType visitMult(ASTMult node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("mult: left operand not int, found: " + leftType);
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("mult: right operand not int, found: " + rightType);

		return leftType;
	}

	@Override
	public ASTType visitDiv(ASTDiv node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("div: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("div: right operand not int, found: " + rightType.toStr());

		return ASTTypeInt.TINT;
	}

	@Override
	public ASTType visitNeg(ASTNeg node) throws TypeCheckError {
		ASTType type = unfold(node.getExpr().accept(this));
		if (!(type instanceof ASTTypeInt)) {
			throw new TypeCheckError("neg: operand not int, found: " + type.toStr());
		}
		return ASTTypeInt.TINT;
	}

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitEq(ASTEq node) throws TypeCheckError {
		ASTType leftType = node.getLeft().accept(this);
		ASTType rightType = node.getRight().accept(this);

		try {
			leastUpperBound(leftType, rightType);
		} catch (TypeCheckError e) {
			throw new TypeCheckError(
					"eq: operand types are incompatible, got: " +
							unfold(leftType).toStr() + " and " + unfold(rightType).toStr());
		}

		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitDif(ASTDif node) throws TypeCheckError {
		ASTType leftType = node.getLeft().accept(this);
		ASTType rightType = node.getRight().accept(this);

		try {
			leastUpperBound(leftType, rightType);
		} catch (TypeCheckError e) {
			throw new TypeCheckError(
					"dif: operand types are incompatible, got: " +
							unfold(leftType).toStr() + " and " + unfold(rightType).toStr());
		}

		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitGt(ASTGt node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("gt: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("gt: right operand not int, found: " + rightType.toStr());

		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitGteq(ASTGteq node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("gteq: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("gteq: right operand not int, found: " + rightType.toStr());

		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitLt(ASTLt node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("lt: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("lt: right operand not int, found: " + rightType.toStr());

		return ASTTypeBool.TBOOL;
	}

	public ASTType visitLteq(ASTLteq node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("lteq: left operand not int, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("lteq: right operand not int, found: " + rightType.toStr());

		return ASTTypeBool.TBOOL;
	}

	// ----------------------------------------------------
	// Logical Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitAnd(ASTAnd node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeBool))
			throw new TypeCheckError("and: left operand not bool, found: " + leftType);
		if (!(rightType instanceof ASTTypeBool))
			throw new TypeCheckError("and: right operand not bool, found: " + rightType);

		return leftType;
	}

	@Override
	public ASTType visitOr(ASTOr node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeBool))
			throw new TypeCheckError("or: left operand not bool, found: " + leftType.toStr());
		if (!(rightType instanceof ASTTypeBool))
			throw new TypeCheckError("or: right operand not bool, found: " + rightType.toStr());

		return ASTTypeBool.TBOOL;
	}

	@Override
	public ASTType visitNot(ASTNot node) throws TypeCheckError {
		ASTType type = unfold(node.getExpr().accept(this));
		if (!(type instanceof ASTTypeBool)) {
			throw new TypeCheckError("not: operand not bool, found: " + type.toStr());
		}
		return ASTTypeBool.TBOOL;
	}

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------
	@Override
	public ASTType visitStructPattern(ASTStructPattern node) throws TypeCheckError {
		throw new TypeCheckError("pattern: cannot be type-checked independently.");
	}

	@Override
	public ASTType visitStructTuple(ASTStructTuple node) throws TypeCheckError {
		List<ASTType> elementTypes = new java.util.ArrayList<>();

		for (ASTNode elem : node.getElements()) {
			elementTypes.add(elem.accept(this));
		}

		Map<String, List<ASTType>> options = new LinkedHashMap<>();
		options.put(node.getTag(), elementTypes);

		return new ASTTypeEnum(options);
	}

	@Override
	public ASTType visitMatch(ASTMatch node) throws TypeCheckError {
		ASTType targetType = unfold(node.getTargetExpression().accept(this));

		if (!(targetType instanceof ASTTypeEnum)) {
			throw new TypeCheckError("match: target must evaluate to an enum type, but got: " + targetType.toStr());
		}

		ASTTypeEnum enumType = (ASTTypeEnum) targetType;
		ASTType matchReturnType = null;
		Set<String> matchedTags = new HashSet<>();

		List<ASTStructPattern> patterns = node.getPatterns();
		List<ASTNode> branches = node.getBranches();

		// Type-check each branch
		for (int i = 0; i < patterns.size(); i++) {
			ASTStructPattern pattern = patterns.get(i);
			ASTNode branchBody = branches.get(i);

			String tag = pattern.getTag();
			matchedTags.add(tag);

			if (!enumType.getOptions().containsKey(tag)) {
				throw new TypeCheckError("match: Tag '" + tag + "' is not a valid variant of the matched enum.");
			}

			List<ASTType> expectedTypes = enumType.getOptions().get(tag);
			List<String> bindings = pattern.getBoundVariables();

			if (bindings.size() > expectedTypes.size()) {
				throw new TypeCheckError(
						"match: Arity mismatch in match branch for tag '" + tag + "'. Expected at most " +
								expectedTypes.size() + " bindings, got " + bindings.size() + ".");
			}

			// Create a new scope for the branch variables
			Environment<ASTType> branchEnv = this.typeEnvironment.beginScope();

			for (int j = 0; j < bindings.size(); j++) {
				String bindingId = bindings.get(j);
				if (!bindingId.equals("_")) {
					try {
						branchEnv.assoc(bindingId, expectedTypes.get(j));
					} catch (BindingException e) {
						throw new TypeCheckError("match: " + e.getMessage());
					}
				}
			}

			// Temporarily swap the environment to type-check the branch body
			Environment<ASTType> previousEnv = this.typeEnvironment;
			this.typeEnvironment = branchEnv;
			ASTType branchType;

			try {
				branchType = branchBody.accept(this);
			} finally {
				this.typeEnvironment = previousEnv;
			}

			// Unify branch return types
			if (matchReturnType == null) {
				matchReturnType = branchType;
			} else {
				try {
					matchReturnType = leastUpperBound(matchReturnType, branchType);
				} catch (TypeCheckError e) {
					throw new TypeCheckError("match: Incompatible return types in match branches. Cannot unify " +
							matchReturnType.toStr() + " and " + branchType.toStr() + ": " + e.getMessage());
				}
			}
		}

		// Exhaustiveness Check
		for (String expectedTag : enumType.getOptions().keySet()) {
			if (!matchedTags.contains(expectedTag)) {
				throw new TypeCheckError("match: non-exhaustive match. Missing branch for tag: '" + expectedTag + "'");
			}
		}

		return matchReturnType;
	}
}
