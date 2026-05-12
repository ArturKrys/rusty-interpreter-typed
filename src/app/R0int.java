package app;

import parser.ParseException;
import parser.Parser;
import visitor.EvalVisitor;
import interpreter.environment.Environment;
import interpreter.environment.Store;
import interpreter.values.IValue;
import ast.ASTNode;

public class R0int {

	public static void main(String args[]) {
		Parser parser = new Parser(System.in);
		ASTNode exp;

		System.out.println("R0 interpreter PL MEIC 2025/26 (v0.1)\n");

		while (true) {
			try {
				System.out.print("# ");
				exp = parser.ParseProgram();
				if (exp == null)
					System.exit(0);
				EvalVisitor eval = new EvalVisitor(new Environment<>(), new Store<>());
				IValue result = exp.accept(eval);
				System.out.println(result.toStr());
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
