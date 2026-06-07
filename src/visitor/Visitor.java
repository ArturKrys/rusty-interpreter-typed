package visitor;

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

public interface Visitor<T, E extends Exception> {
	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	T visitLet(ASTLet node) throws E;

	T visitTypeDefinition(ASTTypeDefinition node) throws E;

	// ----------------------------------------------------
	// Control
	// ----------------------------------------------------
	T visitIf(ASTIf node) throws E;

	T visitWhile(ASTWhile node) throws E;

	T visitSeq(ASTSeq node) throws E;

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	T visitFun(ASTFun node) throws E;

	T visitApp(ASTApp node) throws E;

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	T visitPrint(ASTPrint node) throws E;

	T visitPrintln(ASTPrintln node) throws E;

	// ----------------------------------------------------
	// literals
	// ----------------------------------------------------
	T visitInt(ASTInt node) throws E;

	T visitString(ASTString node) throws E;

	T visitBool(ASTBool node) throws E;

	T visitId(ASTId node) throws E;

	public T visitUnit(ASTUnit node) throws E;

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	T visitAssign(ASTAssign node) throws E;

	T visitCell(ASTCell node) throws E;

	T visitDeref(ASTDeref node) throws E;

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	T visitPlus(ASTPlus node) throws E;

	T visitSub(ASTSub node) throws E;

	T visitMult(ASTMult node) throws E;

	T visitDiv(ASTDiv node) throws E;

	T visitNeg(ASTNeg node) throws E;

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	T visitEq(ASTEq node) throws E;

	T visitDif(ASTDif node) throws E;

	T visitGt(ASTGt node) throws E;

	T visitGteq(ASTGteq node) throws E;

	T visitLt(ASTLt node) throws E;

	T visitLteq(ASTLteq node) throws E;

	// ----------------------------------------------------
	// Logical Operations
	// ----------------------------------------------------
	T visitAnd(ASTAnd node) throws E;

	T visitOr(ASTOr node) throws E;

	T visitNot(ASTNot node) throws E;

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------

	public T visitStructPattern(ASTStructPattern node) throws E;

	T visitStructTuple(ASTStructTuple node) throws E;

	T visitMatch(ASTMatch node) throws E;
}
