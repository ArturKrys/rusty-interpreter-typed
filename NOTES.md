# Things I want to improve

- put body in [ASTLet](src/ast/bindings/ASTLet.java) and
[ASTTypeDefinition](src/ast/bindings/ASTTypeDefinition.java) as final.
    1. Parse the let bindings and store them in a local Java List or variable inside your parser rule.
    2. Parse the body and store it in a local ASTNode variable.
    3. At the very end of the parser rule, combine them: return new ASTLet(bindings, body)
- Improve all the times find  of the method assoc in
[Environment](src/interpreter/environment/Environment.java) is used. I don't think it's a good
idea to use toStr to find the variable
- It would be cool to add those nice comments every class
- it would be nice to add toString methods everywhere
