package com.ck;

import com.ck.ast.*;
import com.ck.ast.literal.*;
import com.ck.ast.statement.*;
import com.ck.token.*;
import com.ck.token.keyword.*;
import com.ck.token.literal.IdentifierToken;
import com.ck.token.literal.NumberToken;
import com.ck.token.literal.StringToken;
import com.ck.token.operator.*;
import com.ck.token.symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 解析器 构建AST
 *
 * @author 陈坤
 * 2023/9/30
 */
public class Parser {

    private final Tokenizer tokenizer;
    private Token<?> lookahead;

    public Parser() {
        this.tokenizer = new Tokenizer();
    }


    public ASTree parse(String string) {
        this.tokenizer.init(string);
        this.lookahead = this.tokenizer.getNextToken();

        return this.program();
    }

    /*
        Main entry point.
        
        Program
            : StatementList
            ;
     */
    private ASTree program() {
        return new Program(
                this.statementList()
        );
    }


    /*
        StatementList
            : Statement
            | StatementList Statement -> Statement Statement Statement Statement
            ;
     */
    private List<ASTree> statementList() {
        return this.statementList(null);
    }

    private List<ASTree> statementList(Class<? extends Token<?>> stopLookahead) {
        final List<ASTree> statementList = new ArrayList<>();
        statementList.add(this.statement());

        while (this.lookahead != null && !this.lookaheadEq(stopLookahead)) {
            statementList.add(this.statement());
        }

        return statementList;
    }

    /*
        Statement
            : ExpressionStatement
            | BlockStatement
            | EmptyStatement
            | VariableStatement
            | IfStatement
            | IterationStatement
            | FunctionDeclaration
            | ReturnStatement
            | ClassDeclaration
            ;
     */
    private Statement statement() {
        if (this.lookaheadEq(SemicolonToken.class)) {
            return this.emptyStatement();
        } else if (this.lookaheadEq(OpenBraceToken.class)) {
            return this.blockStatement();
        } else if (this.lookaheadEq(LetToken.class)) {
            return this.variableStatement();
        } else if (this.lookaheadEq(IfToken.class)) {
            return this.ifStatement();
        } else if (this.lookahead instanceof IterationToken) {
            return this.iterationStatement();
        } else if (this.lookaheadEq(DefToken.class)) {
            return this.functionDeclaration();
        } else if (this.lookaheadEq(ReturnToken.class)) {
            return this.returnStatement();
        } else if (this.lookaheadEq(ClassToken.class)) {
            return this.classDeclaration();
        } else {
            return this.expressionStatement();
        }
    }

    /*
        ClassDeclaration
            : 'class' Identifier OptClassExtends BlockStatement
            ;
     */
    private Statement classDeclaration() {
        this.eat(ClassToken.class);
        Identifier id = this.identifier();

        Identifier superClass = this.lookaheadEq(ExtendsToken.class) ? this.clasExtends() : null;
        BlockStatement body = this.blockStatement();

        return new ClassDeclaration(
                id,
                superClass,
                body
        );
    }

    /*
        ClasExtends
            : 'extends' Identifier
            ;
     */
    private Identifier clasExtends() {
        this.eat(ExtendsToken.class);
        return this.identifier();
    }

    /*
        FunctionDeclaration
            : 'def' Identifier '(' OptFormalParameterList ')' BlockStatement
            ;
        
     */
    private Statement functionDeclaration() {
        this.eat(DefToken.class);
        Identifier name = this.identifier();

        this.eat(OpenParenthesisToken.class);

        // OptFormalParameterList
        List<ASTree> params = this.lookaheadEq(ClosedParenthesisToken.class)
                ? List.of()
                : this.formalParameterList();

        this.eat(ClosedParenthesisToken.class);

        BlockStatement body = this.blockStatement();
        return new FunctionDeclaration(
                name,
                params,
                body
        );
    }

    /*
        FormalParameterList
            : Identifier
            | FormalParameterList ',' Identifier
            ;
     */
    private List<ASTree> formalParameterList() {
        List<ASTree> params = new ArrayList<>();

        params.add(this.identifier());
        while (this.lookaheadEq(CommaToken.class)) {
            this.eat(CommaToken.class);
            params.add(this.identifier());
        }

        return params;
    }

    /*
        ReturnStatement
            : 'return' OptExpression ';'
            ;
     */
    private Statement returnStatement() {
        this.eat(ReturnToken.class);
        ASTree argument = this.lookaheadEq(SemicolonToken.class) ? null : this.expression();

        this.eat(SemicolonToken.class);
        return new ReturnStatement(argument);
    }

    /*
        IterationStatement
            : WhileStatement
            | DoWhileStatement
            | ForStatement
            ;
     */
    private Statement iterationStatement() {
        if (this.lookaheadEq(WhileToken.class)) {
            return this.whileStatement();
        } else if (this.lookaheadEq(DoToken.class)) {
            return this.doWhileStatement();
        } else {
            return this.forStatement();
        }
    }

    /*
        ForStatement
            : 'for' '(' OptForStatementInit ';' OptExpression ';' OptExpression ')' Statement
            ;
     */
    private Statement forStatement() {
        this.eat(ForToken.class);
        this.eat(OpenParenthesisToken.class);

        ASTree init = this.lookaheadEq(SemicolonToken.class) ? null : this.forStatementInit();
        this.eat(SemicolonToken.class);

        ASTree test = this.lookaheadEq(SemicolonToken.class) ? null : this.expression();
        this.eat(SemicolonToken.class);

        ASTree update = this.lookaheadEq(ClosedParenthesisToken.class) ? null : this.expression();
        this.eat(ClosedParenthesisToken.class);

        Statement body = this.statement();

        return new ForStatement(
                init,
                test,
                update,
                body
        );
    }

    /*
        ForStatementInit
            : VariableStatementInit
            | Expression
            ;
     */
    private ASTree forStatementInit() {
        if (this.lookaheadEq(LetToken.class)) {
            return this.variableStatementInit();
        }
        return this.expression();
    }

    /*
        DoWhileStatement
            : 'do' Statement 'while' '(' Expression ')' ';'
            ;
     */
    private Statement doWhileStatement() {
        this.eat(DoToken.class);

        Statement body = this.statement();

        this.eat(WhileToken.class);

        this.eat(OpenParenthesisToken.class);
        ASTree test = this.expression();
        this.eat(ClosedParenthesisToken.class);

        this.eat(SemicolonToken.class);

        return new DoWhileStatement(test, body);
    }

    /*
        WhileStatement
            : 'while' '(' Expression ')' Statement
            ;
     */
    private Statement whileStatement() {
        this.eat(WhileToken.class);

        this.eat(OpenParenthesisToken.class);
        ASTree test = this.expression();
        this.eat(ClosedParenthesisToken.class);

        Statement body = this.statement();
        return new WhileStatement(test, body);
    }

    /*
        IfStatement
            : 'if' '(' Expression ')' Statement
            | 'if' '(' Expression ')' Statement 'else' Statement
            ;
     */
    private Statement ifStatement() {
        this.eat(IfToken.class);

        this.eat(OpenParenthesisToken.class);
        ASTree test = this.expression();
        this.eat(ClosedParenthesisToken.class);

        Statement consequent = this.statement();

        Statement alternate = null;
        if (this.lookahead != null && this.lookaheadEq(ElseToken.class)) {
            this.eat(ElseToken.class);
            alternate = this.statement();
        }

        return new IfStatement(
                test,
                consequent,
                alternate
        );
    }

    /*
        VariableStatementInit
            : 'let' VariableDeclarationList
            ;
     */
    private VariableStatement variableStatementInit() {
        this.eat(LetToken.class);
        List<ASTree> list = this.variableDeclarationList();
        return new VariableStatement(list);
    }

    /*
        VariableStatement
            : VariableStatementInit ';'
            ;
     */
    private VariableStatement variableStatement() {
        VariableStatement variableStatement = this.variableStatementInit();
        this.eat(SemicolonToken.class);
        return variableStatement;
    }

    /*
        VariableDeclarationList
            : VariableDeclaration
            | VariableDeclarationList ',' VariableDeclaration
            ;
     */
    private List<ASTree> variableDeclarationList() {
        List<ASTree> list = new ArrayList<>();
        list.add(this.variableDeclaration());

        while (this.lookaheadEq(CommaToken.class)) {
            this.eat(CommaToken.class);
            list.add(this.variableDeclaration());
        }
        return list;
    }

    /*
        VariableDeclaration
            : Identifier OptVariableInitializer
            ;
     */
    private ASTree variableDeclaration() {
        Identifier id = this.identifier();

        ASTree init = this.lookaheadEq(SemicolonToken.class) || this.lookaheadEq(CommaToken.class)
                ? null
                : this.variableInitializer();

        return new VariableDeclaration(id, init);
    }

    /*
        VariableInitializer
            : SIMPLE_ASSIGN assignmentExpression
            ;
     */
    private ASTree variableInitializer() {
        this.eat(SimpleAssignToken.class);
        return this.assignmentExpression();
    }

    /*
        EmptyStatement
            : ';'
            ;
     */
    private EmptyStatement emptyStatement() {
        this.eat(SemicolonToken.class);
        return EmptyStatement.INSTANCE;
    }

    /*
        BlockStatement
            : '{' OptStatementList '}'
            ;
     */
    private BlockStatement blockStatement() {
        this.eat(OpenBraceToken.class);

        final List<ASTree> body = this.lookaheadEq(ClosedBraceToken.class)
                ? List.of()
                : this.statementList(ClosedBraceToken.class);

        this.eat(ClosedBraceToken.class);

        return new BlockStatement(body);
    }

    /*
        ExpressionStatement
            : Expression ';'
            ;
     */
    private ExpressionStatement expressionStatement() {
        ASTree expression = this.expression();
        this.eat(SemicolonToken.class);

        return new ExpressionStatement(expression);
    }

    /*
        Expression
            : AssignmentExpression
            ;
     */
    private ASTree expression() {
        return this.assignmentExpression();
    }

    /*
        AssignmentExpression
            : LogicalOrExpression
            | LeftHandSideExpression ASSIGNMENT_OPERATOR AssignmentExpression
            ;
     */
    private ASTree assignmentExpression() {
        ASTree left = this.logicalOrExpression();

        // 如果下一个Token不是赋值运算符直接返回
        if (!(this.lookahead instanceof AssignToken)) {
            return left;
        }

        return new AssignmentExpression(
                this.eat(AssignToken.class),
                this.checkValidAssignmentTarget(left),
                this.assignmentExpression()
        );
    }

    /*
        Identifier
            : IDENTIFIER
            ;
     */
    private Identifier identifier() {
        IdentifierToken token = this.eat(IdentifierToken.class);
        return new Identifier(token.value());
    }

    /**
     * 检查赋值运算符左侧是不是一个标识符
     */
    private ASTree checkValidAssignmentTarget(ASTree tree) {
        if (tree instanceof Identifier || tree instanceof MemberExpression) {
            return tree;
        }

        throw new SyntaxException("Invalid left-hand side in assignment expression");
    }

    /*
        Logical OR expression.
        x || y
        
        LogicalOrExpression
            : LogicalAndExpression 
            | LogicalOrExpression LOGICAL_OR LogicalAndExpression
            ;
     */
    private ASTree logicalOrExpression() {
        return this.logicalExpression(this::logicalAndExpression, LogicalOrToken.class);
    }

    /*
       Logical AND expression.
       x && y
        
       LogicalAndExpression
           : EqualityExpression
           | LogicalAndExpression LOGICAL_AND EqualityExpression
           ;
    */
    private ASTree logicalAndExpression() {
        return this.logicalExpression(this::equalityExpression, LogicalAndToken.class);
    }

    /*
        EQUALITY_OPERATOR: ==, !=
        x == y
        x != y
        
        EqualityExpression
            : RelationalExpression
            | EqualityExpression EQUALITY_OPERATOR RelationalExpression
            ;
     */
    private ASTree equalityExpression() {
        return this.binaryExpression(this::relationalExpression, EqualityOperatorToken.class);
    }

    /*
        RELATIONAL_OPERATOR: >, >=, <, <=
        x > y
        x >= y
        x < y
        x <= y
        
        RelationalExpression
            : AdditiveExpression
            | RelationalExpression RELATIONAL_OPERATOR AdditiveExpression
            ;
     */
    private ASTree relationalExpression() {
        return this.binaryExpression(this::additiveExpression, RelationalOperatorToken.class);
    }

    /*
        AdditiveExpression
            : MultiplicativeExpression
            | AdditiveExpression ADDITIVE_OPERATOR MultiplicativeExpression
            ;
     */
    private ASTree additiveExpression() {
        return this.binaryExpression(this::multiplicativeExpression, AdditiveOperatorToken.class);
    }

    /*
        MultiplicativeExpression
            : UnaryExpression
            | MultiplicativeExpression MULTIPLICATIVE_OPERATOR UnaryExpression
            ;
     */
    private ASTree multiplicativeExpression() {
        return this.binaryExpression(this::unaryExpression, MultiplicativeOperatorToken.class);
    }

    /**
     * 构建二进制运算表达式
     *
     * @param builderMethod     构建数据
     * @param operatorTokenType 运算符类型
     */
    private ASTree binaryExpression(Supplier<ASTree> builderMethod, Class<? extends OperatorToken> operatorTokenType) {
        ASTree left = builderMethod.get();

        while (this.lookaheadEq(operatorTokenType)) {
            OperatorToken operator = this.eat(operatorTokenType);
            ASTree right = builderMethod.get();

            left = new BinaryExpression(
                    operator,
                    left,
                    right
            );
        }

        return left;
    }

    /**
     * 构建逻辑运算表达式
     *
     * @param builderMethod     构建数据
     * @param operatorTokenType 运算符类型
     */
    private ASTree logicalExpression(Supplier<ASTree> builderMethod, Class<? extends OperatorToken> operatorTokenType) {
        ASTree left = builderMethod.get();

        while (this.lookaheadEq(operatorTokenType)) {
            OperatorToken operator = this.eat(operatorTokenType);
            ASTree right = builderMethod.get();

            left = new LogicalExpression(
                    operator,
                    left,
                    right
            );
        }

        return left;
    }

    /*
        UnaryExpression
            : LeftHandSideExpression
            | ADDITIVE_OPERATOR UnaryExpression
            | LOGICAL_NOT UnaryExpression
            ;
     */
    private ASTree unaryExpression() {
        OperatorToken operator = null;
        if (this.lookaheadEq(AdditiveOperatorToken.class)) {
            operator = this.eat(AdditiveOperatorToken.class);
        } else if (this.lookaheadEq(LogicalNotToken.class)) {
            operator = this.eat(LogicalNotToken.class);
        }

        if (operator != null) {
            return new UnaryExpression(
                    operator,
                    this.unaryExpression()
            );
        }

        return this.leftHandSideExpression();
    }

    /*
       LeftHandSideExpression
           : CallMemberExpression
           ;
    */
    private ASTree leftHandSideExpression() {
        return this.callMemberExpression();
    }

    /*
        CallMemberExpression
            : MemberExpression
            | CallExpression
            ;
     */
    private ASTree callMemberExpression() {
        if (this.lookaheadEq(SuperToken.class)) {
            return this.callExpression(this.superAst());
        }

        ASTree member = this.memberExpression();

        if (this.lookaheadEq(OpenParenthesisToken.class)) {
            return this.callExpression(member);
        }

        return member;
    }

    /*
        CallExpression
            : Callee Arguments
            ;
        
        Callee
            : MemberExpression
            | CallExpression
            ;
     */
    private ASTree callExpression(ASTree callee) {
        ASTree callExpression = new CallExpression(callee, this.arguments());

        if (this.lookaheadEq(OpenParenthesisToken.class)) {
            callExpression = this.callExpression(callExpression);
        }

        return callExpression;
    }

    /*
        Arguments
            : '(' OptArgumentList ')'
            ;
     */
    private List<ASTree> arguments() {
        this.eat(OpenParenthesisToken.class);

        List<ASTree> argumentList = this.lookaheadEq(ClosedParenthesisToken.class) ? List.of() : this.argumentList();

        this.eat(ClosedParenthesisToken.class);

        return argumentList;
    }

    /*
        ArgumentList
            : AssignmentExpression
            | ArgumentList ',' AssignmentExpression
            ;
     */
    private List<ASTree> argumentList() {
        var argumentList = new ArrayList<ASTree>();
        argumentList.add(this.assignmentExpression());

        while (this.lookaheadEq(CommaToken.class)) {
            this.eat(CommaToken.class);
            argumentList.add(this.assignmentExpression());
        }

        return argumentList;
    }

    /*
        MemberExpression
            : PrimaryExpression
            | MemberExpression '.' Identifier
            | MemberExpression '[' Expression ']'
            ;
     */
    private ASTree memberExpression() {
        var object = this.primaryExpression();

        while (this.lookaheadEq(DotToken.class) || this.lookaheadEq(OpenSquareBracketsToken.class)) {

            if (this.lookaheadEq(DotToken.class)) {
                this.eat(DotToken.class);
                Identifier property = this.identifier();

                object = new MemberExpression(
                        false,
                        object,
                        property
                );

            }

            if (this.lookaheadEq(OpenSquareBracketsToken.class)) {
                this.eat(OpenSquareBracketsToken.class);
                ASTree property = this.expression();
                this.eat(ClosedSquareBracketsToken.class);

                object = new MemberExpression(
                        true,
                        object,
                        property
                );
            }
        }

        return object;
    }


    /*
        PrimaryExpression
            : Literal
            | ParenthesizedExpression
            | Identifier
            | ThisExpression
            | NewExpression
            ;
     */
    private ASTree primaryExpression() {
        if (this.lookahead instanceof LiteralToken<?>) {
            return this.literal();
        } else if (this.lookaheadEq(OpenParenthesisToken.class)) {
            return this.parenthesizedExpression();
        } else if (this.lookaheadEq(IdentifierToken.class)) {
            return this.identifier();
        } else if (this.lookaheadEq(ThisToken.class)) {
            return this.thisExpression();
        } else if (this.lookaheadEq(NewToken.class)) {
            return this.newExpression();
        }

        throw new SyntaxException("Unexpected primary expression.");
    }

    /*
        NewExpression
            : 'new' MemberExpression Arguments
            ;
     */
    private ASTree newExpression() {
        this.eat(NewToken.class);

        return new NewExpression(
                this.memberExpression(),
                this.arguments()
        );
    }

    /*
        ThisExpression
            : 'this'
            ;
     */
    private ASTree thisExpression() {
        this.eat(ThisToken.class);
        return ThisExpression.INSTANCE;
    }

    /*
        Super
            : 'super'
            ;
     */
    private ASTree superAst() {
        this.eat(SuperToken.class);
        return Super.INSTANCE;
    }

    /*
        ParenthesizedExpression
            : '(' Expression ')'
            ;
     */
    private ASTree parenthesizedExpression() {
        this.eat(OpenParenthesisToken.class);
        ASTree expression = this.expression();
        this.eat(ClosedParenthesisToken.class);

        return expression;
    }

    /*
        Literal
            : NumericLiteral
            | StringLiteral
            | BooleanLiteral
            | NullLiteral
            ;
     */
    private Literal literal() {
        if (this.lookahead instanceof BooleanLiteralToken) {
            return this.booleanLiteral(this.eat(BooleanLiteralToken.class).value());
        } else if (this.lookaheadEq(NumberToken.class)) {
            return this.numericLiteral();
        } else if (this.lookaheadEq(StringToken.class)) {
            return this.stringLiteral();
        } else if (this.lookaheadEq(NullToken.class)) {
            return this.nullLiteral();
        }

        throw new SyntaxException("Literal: unexpected literal production");
    }

    /*
        BooleanLiteral
            : 'true'
            | 'false'
            ;
     */
    private Literal booleanLiteral(boolean b) {
        return new BooleanLiteral(b);
    }

    /*
        NullLiteral
            : 'null'
            ;
     */
    private Literal nullLiteral() {
        this.eat(NullToken.class);
        return new NullLiteral(null);
    }

    /*
        NumericLiteral
            : NUMBER
            ;
     */
    private NumericLiteral numericLiteral() {
        NumberToken token = this.eat(NumberToken.class);
        return new NumericLiteral(token.value());
    }

    /*
        StringLiteral
            : STRING
            ;
     */
    private StringLiteral stringLiteral() {
        StringToken token = this.eat(StringToken.class);
        return new StringLiteral(token.value().substring(1, token.value().length() - 1));

    }

    private <T extends Token<?>> boolean lookaheadEq(Class<T> tokenType) {
        return this.lookahead.getClass() == tokenType;
    }

    private <T extends Token<?>> T eat(Class<T> tokenType) {
        final Token<?> token = this.lookahead;

        if (token == null) {
            throw new SyntaxException("Unexpected end of input, expected: " + tokenType.getSimpleName());
        }

        if (!tokenType.isInstance(token)) {
            throw new SyntaxException("Unexpected token: " + token.value() + ", expected: " + tokenType.getSimpleName());
        }

        this.lookahead = this.tokenizer.getNextToken();
        //noinspection unchecked
        return (T) token;
    }
}
