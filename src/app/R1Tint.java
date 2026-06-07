package app;

import parser.ParseException;
import parser.Parser;
import visitor.EvalVisitor;
import visitor.TypeCheckVisitor;
import interpreter.environment.Environment;
import interpreter.environment.Store;
import interpreter.values.IValue;
import ast.ASTNode;
import ast.type.ASTType;

public class R1Tint {

	public static void main(String args[]) {
		Parser parser = new Parser(System.in);
		ASTNode exp;

		System.out.println("R1T interpreter PL MEIC 2025/26 (v0.0)\n");

		while (true) {
			try {
				System.out.print("# ");
				exp = parser.ParseProgram();
				if (exp == null)
					System.exit(0);
				EvalVisitor eval = new EvalVisitor(new Environment<>(), new Store<>());
				TypeCheckVisitor typeCheck = new TypeCheckVisitor(new Environment<>());
				ASTType type = exp.accept(typeCheck);
				IValue result = exp.accept(eval);
				System.out.println(result.toStr() + " : " + type.toStr());
			} catch (ParseException e) {
				System.out.println("Syntax Error.");
				parser.ReInit(System.in);
			} catch (Exception e) {
				e.printStackTrace();
				parser.ReInit(System.in);
			}
		}
	}
}
