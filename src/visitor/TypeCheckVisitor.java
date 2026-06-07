package visitor;

import java.util.Map.Entry;

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
import ast.type.ASTTypeBool;
import ast.type.ASTTypeId;
import ast.type.ASTTypeInt;
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

	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	@Override
	public ASTType visitLet(ASTLet node) throws TypeCheckError {
		this.typeEnvironment = this.typeEnvironment.beginScope();

		try {
			for (Bind bind : node.getDeclarations()) {
				ASTType initType = bind.getExp().accept(this);
				ASTType finalType = initType;

				if (bind.getType() != null) {
					ASTType declaredType = unfold(bind.getType());
					ASTType unfoldedInitType = unfold(initType);

					if (!declaredType.equals(unfoldedInitType)) {
						throw new TypeCheckError("Let: type mismatch for '" + bind.getId() + "'");
					}

					finalType = bind.getType();
				}

				this.typeEnvironment.assoc(bind.getId(), finalType);
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
		return null;
	}

	@Override
	public ASTType visitWhile(ASTWhile node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitSeq(ASTSeq node) throws TypeCheckError {
		return null;
	}

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	@Override
	public ASTType visitFun(ASTFun node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitApp(ASTApp node) throws TypeCheckError {
		return null;
	}

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitPrint(ASTPrint node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitPrintln(ASTPrintln node) throws TypeCheckError {
		return null;
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
			return this.typeEnvironment.find(node.getId());
		} catch (BindingException e) {
			throw new TypeCheckError("id: " + e.getMessage());
		}
	}

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	@Override
	public ASTType visitAssign(ASTAssign node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitCell(ASTCell node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitDeref(ASTDeref node) throws TypeCheckError {
		return null;
	}

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitPlus(ASTPlus node) throws TypeCheckError {
		ASTType leftType = unfold(node.getLeft().accept(this));
		ASTType rightType = unfold(node.getRight().accept(this));

		if (!(leftType instanceof ASTTypeInt))
			throw new TypeCheckError("plus: left operand not int, found: " + leftType);
		if (!(rightType instanceof ASTTypeInt))
			throw new TypeCheckError("plus: right operand not int, found: " + rightType);

		return leftType;
	}

	@Override
	public ASTType visitSub(ASTSub node) throws TypeCheckError {
		return null;
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
		return null;
	}

	@Override
	public ASTType visitNeg(ASTNeg node) throws TypeCheckError {
		return null;
	}

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	@Override
	public ASTType visitEq(ASTEq node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitDif(ASTDif node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitGt(ASTGt node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitGteq(ASTGteq node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitLt(ASTLt node) throws TypeCheckError {
		return null;
	}

	public ASTType visitLteq(ASTLteq node) throws TypeCheckError {
		return null;
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
		return null;
	}

	@Override
	public ASTType visitNot(ASTNot node) throws TypeCheckError {
		return null;
	}

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------
	@Override
	public ASTType visitStructPattern(ASTStructPattern node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitStructTuple(ASTStructTuple node) throws TypeCheckError {
		return null;
	}

	@Override
	public ASTType visitMatch(ASTMatch node) throws TypeCheckError {
		return null;
	}
}
