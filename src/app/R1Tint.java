package app;

import parser.ParseException;
import parser.Parser;
import visitor.EvalVisitor;
import visitor.TypeCheckVisitor;
import interpreter.environment.Environment;
import interpreter.environment.MemoryManager;
import interpreter.environment.Store;
import interpreter.values.IValue;
import ast.ASTNode;
import ast.type.ASTType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class R1Tint {

	public static void main(String args[]) {
		boolean typeCheckEnabled = true;
		String filename = null;

		// Parse command-line arguments
		for (String arg : args) {
			if (arg.equals("-types")) {
				typeCheckEnabled = false; // Deactivates the type-checker
			} else if (!arg.startsWith("-")) {
				filename = arg; // Capture the file to run
			}
		}

		InputStream inputStream = System.in;
		boolean isInteractive = true;

		if (filename != null) {
			try {
				inputStream = new FileInputStream(filename);
				isInteractive = false;
			} catch (FileNotFoundException e) {
				System.err.println("Error: File not found: " + filename);
				System.exit(1);
			}
		}

		Parser parser = new Parser(inputStream);
		ASTNode exp;

		if (isInteractive) {
			System.out.println("R1T interpreter PL MEIC 2025/26 (v0.0)\n");
		}

		while (true) {
			try {
				if (isInteractive) {
					System.out.print("# ");
				}

				exp = parser.ParseProgram();
				if (exp == null) {
					System.exit(0);
				}

				// Initialize environment per evaluation (matching your original logic)
				EvalVisitor eval = new EvalVisitor(new Environment<>(), new Store<>(), new MemoryManager());
				IValue result;

				if (typeCheckEnabled) {
					TypeCheckVisitor typeCheck = new TypeCheckVisitor(new Environment<>());
					ASTType type = exp.accept(typeCheck);
					result = exp.accept(eval);

					if (isInteractive) {
						System.out.println(result.toStr() + " : " + type.toStr());
					}
				} else {
					result = exp.accept(eval);

					if (isInteractive) {
						System.out.println(result.toStr());
					}
				}

			} catch (ParseException e) {
				System.out.println("Syntax Error.");
				if (!isInteractive)
					System.exit(1);
				parser.ReInit(System.in);
			} catch (Exception e) {
				if (!typeCheckEnabled && isInteractive) {
					System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
				} else {
					e.printStackTrace();
				}

				if (!isInteractive)
					System.exit(1);
				parser.ReInit(System.in);
			}
		}
	}
}
