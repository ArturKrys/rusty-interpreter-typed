package ast.functions;

import ast.ASTNode;
import interpreter.errors.InterpreterError;
import interpreter.values.IValue;
import visitor.Visitor;

public class ASTFun implements ASTNode {
    private String param;
    private ASTNode body;

    public ASTFun(String param, ASTNode body) {
        this.param = param;
        this.body = body;
    }

    @Override
    public IValue accept(Visitor v) throws InterpreterError {
        return v.visitFun(this);
    }

    public String getParam() {
        return param;
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode body) {
        this.body = body;
    }
}
