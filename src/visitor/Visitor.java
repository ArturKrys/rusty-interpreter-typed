package visitor;

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
import ast.structs.ASTStructTuple;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;

public interface Visitor {
	// ----------------------------------------------------
	// Bindings
	// ----------------------------------------------------
	IValue visitLet(ASTLet node) throws InterpreterError;

	// ----------------------------------------------------
	// Control
	// ----------------------------------------------------
	IValue visitIf(ASTIf node) throws InterpreterError;

	IValue visitWhile(ASTWhile node) throws InterpreterError;

	IValue visitSeq(ASTSeq node) throws InterpreterError;

	// ----------------------------------------------------
	// Functions
	// ----------------------------------------------------
	IValue visitFun(ASTFun node) throws InterpreterError;

	IValue visitApp(ASTApp node) throws InterpreterError;

	// ----------------------------------------------------
	// I/O Operations
	// ----------------------------------------------------
	IValue visitPrint(ASTPrint node) throws InterpreterError;

	IValue visitPrintln(ASTPrintln node) throws InterpreterError;

	// ----------------------------------------------------
	// literals
	// ----------------------------------------------------
	IValue visitInt(ASTInt node) throws InterpreterError;

	IValue visitString(ASTString node) throws InterpreterError;

	IValue visitBool(ASTBool node) throws InterpreterError;

	IValue visitId(ASTId node) throws InterpreterError;

	// ----------------------------------------------------
	// Memory
	// ----------------------------------------------------
	IValue visitAssign(ASTAssign node) throws InterpreterError;

	IValue visitCell(ASTCell node) throws InterpreterError;

	IValue visitDeref(ASTDeref node) throws InterpreterError;

	// ----------------------------------------------------
	// Arithmetic Operations
	// ----------------------------------------------------
	IValue visitPlus(ASTPlus node) throws InterpreterError;

	IValue visitSub(ASTSub node) throws InterpreterError;

	IValue visitMult(ASTMult node) throws InterpreterError;

	IValue visitDiv(ASTDiv node) throws InterpreterError;

	IValue visitNeg(ASTNeg node) throws InterpreterError;

	// ----------------------------------------------------
	// Relational Operations
	// ----------------------------------------------------
	IValue visitEq(ASTEq node) throws InterpreterError;

	IValue visitDif(ASTDif node) throws InterpreterError;

	IValue visitGt(ASTGt node) throws InterpreterError;

	IValue visitGteq(ASTGteq node) throws InterpreterError;

	IValue visitLt(ASTLt node) throws InterpreterError;

	IValue visitLteq(ASTLteq node) throws InterpreterError;

	// ----------------------------------------------------
	// Logical Operations
	// ----------------------------------------------------
	IValue visitAnd(ASTAnd node) throws InterpreterError;

	IValue visitOr(ASTOr node) throws InterpreterError;

	IValue visitNot(ASTNot node) throws InterpreterError;

	// ----------------------------------------------------
	// Struct Tuples and Pattern Matching
	// ----------------------------------------------------
	IValue visitMatch(ASTMatch node) throws InterpreterError;

	IValue visitStructTuple(ASTStructTuple node) throws InterpreterError;
}
