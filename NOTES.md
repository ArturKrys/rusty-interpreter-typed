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
- minor detail but it would be nice for the exception messages to be more consistent
- maybe there is a way to glue together the environment of type checker and the eval, but I'm not sure if that is considered good practice

there is more things I want to improve but I don't have time :(

one day see this if this would work
```jj
ASTNode ParseLetOrTypeDef(): /* Let */
{
    Token nameToken;
    ASTNode rootNode = null;
    ASTNode bodyExpression;
    ASTNode boundExpression;
    ASTType declaredType = null;
    ASTType rhsType;

    int currentMode = 0; // 0 none, 1 type block, 2 let block
    boolean isMutable = false;

    List<ASTNode> declarationBlocks = new ArrayList<ASTNode>();
    List<Bind> letBindings = new ArrayList<Bind>();
    HashMap<String, ASTType> typeDefinitions = new HashMap<String, ASTType>();
}
{
    (
        LOOKAHEAD( <LET> | <TYPE> )
        (
            (
                <LET>
                {
                    if (currentMode == 1) {
                        declarationBlocks.add(new ASTTypeDefinition(typeDefinitions, null));
                        typeDefinitions = new HashMap<String, ASTType>();
                    }
                    currentMode = 2;
                    isMutable = false;
                    declaredType = null;
                }
                ( <MUT> { isMutable = true; } )?
                nameToken = <IDENTIFIER>
                ( <COLON> declaredType = Type() )?
                <BIND> boundExpression = ParseLogicalOr() <SEMICOLON>
                {
                    letBindings.add(new Bind(nameToken.image, isMutable, declaredType, boundExpression));
                }
            )
        |
            (
                <TYPE>
                {
                    if (currentMode == 2) {
                        declarationBlocks.add(new ASTLet(letBindings, null));
                        letBindings = new ArrayList<Bind>();
                    }
                    currentMode = 1;
                }
                nameToken = <IDENTIFIER> <BIND> rhsType = Type() <SEMICOLON>
                {
                    typeDefinitions.put(nameToken.image, rhsType);
                }
            )
        )+
        bodyExpression = ParseSequence()
        {
            if (currentMode == 2) {
                declarationBlocks.add(new ASTLet(letBindings, null));
            } else if (currentMode == 1) {
                declarationBlocks.add(new ASTTypeDefinition(typeDefinitions, null));
            }

            rootNode = bodyExpression;
            for (int i = declarationBlocks.size() - 1; i >= 0; i--) {
                ASTNode block = declarationBlocks.get(i);
                if (block instanceof ASTLet) {
                    ((ASTLet) block).setBodyExpr(rootNode);
                } else {
                    ((ASTTypeDefinition) block).setBodyExpr(rootNode);
                }
                rootNode = block;
            }
        }
    |
        rootNode = ParseSequence()
    )
    { return rootNode; }
}
```
