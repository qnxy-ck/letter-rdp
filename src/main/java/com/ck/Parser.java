package com.ck;

import com.ck.ast.*;
import com.ck.ast.literal.*;
import com.ck.ast.statement.*;
import com.ck.token.OperatorToken;
import com.ck.token.Token;
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

        while (this.lookahead != null && this.lookahead.getClass() != stopLookahead) {
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
            | ifStatement
            ;
     */
    private Statement statement() {
        if (this.lookahead.getClass() == SemicolonToken.class) {
            return this.emptyStatement();
        } else if (this.lookahead.getClass() == OpenBraceToken.class) {
            return this.blockStatement();
        } else if (this.lookahead.getClass() == LetToken.class) {
            return this.variableStatement();
        } else if (this.lookahead.getClass() == IfToken.class) {
            return this.ifStatement();
        } else {
            return this.expressionStatement();
        }
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
        if (this.lookahead != null && this.lookahead.getClass() == ElseToken.class) {
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
        VariableStatement
            : 'let' VariableDeclarationList ';'
            ;
     */
    private VariableStatement variableStatement() {
        this.eat(LetToken.class);
        List<ASTree> list = this.variableDeclarationList();
        this.eat(SemicolonToken.class);

        return new VariableStatement(list);
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

        while (this.lookahead.getClass() == CommaToken.class) {
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

        ASTree init = this.lookahead.getClass() != SemicolonToken.class && this.lookahead.getClass() != CommaToken.class
                ? this.variableInitializer()
                : null;

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

        final List<ASTree> body = this.lookahead.getClass() == ClosedBraceToken.class
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
            : AdditiveExpression
            ;
     */
    private ASTree expression() {
        return this.assignmentExpression();
    }

    /*
        AssignmentExpression
            : EqualityExpression
            | LeftHandSideExpression ASSIGNMENT_OPERATOR AssignmentExpression
            ;
     */
    private ASTree assignmentExpression() {
        ASTree left = this.equalityExpression();

        // 如果下一个Token不是赋值运算符直接返回
        if (!this.isAssignmentOperator(this.lookahead.getClass())) {
            return left;
        }

        return new AssignmentExpression(
                this.assignmentOperator(),
                this.checkValidAssignmentTarget(left),
                this.assignmentExpression()
        );
    }

    /*
        LeftHandSideExpression
            : Identifier
            ;
     */
    private Identifier leftHandSideExpression() {
        return this.identifier();
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
        if (tree instanceof Identifier) {
            return tree;
        }

        throw new SyntaxException("Invalid left-hand side in assignment expression");
    }

    /*
        AssignmentOperator
            : SIMPLE_ASSIGN
            | COMPLEX_ASSIGN
            ;
     */
    private OperatorToken assignmentOperator() {
        if (this.lookahead.getClass() == SimpleAssignToken.class) {
            return this.eat(SimpleAssignToken.class);
        }

        return this.eat(ComplexAssignToken.class);
    }

    /**
     * 判断是否为赋值运算符
     */
    private boolean isAssignmentOperator(@SuppressWarnings("rawtypes") Class<? extends Token> tokenType) {
        return tokenType == SimpleAssignToken.class || tokenType == ComplexAssignToken.class;
    }

    /*
        EqualityExpression
            : RelationalExpression
            | RelationalExpression ADDITIVE_OPERATOR EqualityExpression
            ;
     */
    private ASTree equalityExpression() {
        return this.binaryExpression(this::relationalExpression, EqualityOperatorToken.class);
    }

    /*
        RelationalExpression
            : AdditiveExpression
            | AdditiveExpression RELATIONAL_OPERATOR RelationalExpression
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
            : PrimaryExpression
            | MultiplicativeExpression MULTIPLICATIVE_OPERATOR PrimaryExpression
            ;
     */
    private ASTree multiplicativeExpression() {
        return this.binaryExpression(this::primaryExpression, MultiplicativeOperatorToken.class);
    }

    /**
     * 构建二进制运算表达式
     *
     * @param builderMethod     构建数据
     * @param operatorTokenType 运算符类型
     */
    private ASTree binaryExpression(Supplier<ASTree> builderMethod, Class<? extends OperatorToken> operatorTokenType) {
        ASTree left = builderMethod.get();

        while (this.lookahead.getClass() == operatorTokenType) {
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

    /*
        PrimaryExpression
            : Literal
            | ParenthesizedExpression
            | LeftHandSideExpression
            ;
     */
    private ASTree primaryExpression() {
        if (this.isLiteral(this.lookahead)) {
            return this.literal();
        } else if (this.lookahead.getClass() == OpenParenthesisToken.class) {
            return this.parenthesizedExpression();
        } else {
            return this.leftHandSideExpression();
        }
    }

    private boolean isLiteral(Token<?> lookahead) {
        return lookahead.getClass() == NumberToken.class
                || lookahead.getClass() == StringToken.class
                || lookahead.getClass() == TrueToken.class
                || lookahead.getClass() == FalseToken.class
                || lookahead.getClass() == NullToken.class
                ;
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
        if (this.lookahead.getClass() == NumberToken.class) {
            return this.numericLiteral();
        } else if (this.lookahead.getClass() == StringToken.class) {
            return this.stringLiteral();
        } else if (this.lookahead.getClass() == TrueToken.class) {
            return this.booleanLiteral(true);
        } else if (this.lookahead.getClass() == FalseToken.class) {
            return this.booleanLiteral(false);
        } else if (this.lookahead.getClass() == NullToken.class) {
            return this.nullLiteral();
        }

        throw new SyntaxException("Literal: unexpected literal production");
    }

    private Literal booleanLiteral(boolean b) {
        Class<? extends Token<Boolean>> tokenType = b ? TrueToken.class : FalseToken.class;
        this.eat(tokenType);
        return new BooleanLiteral(b);
    }

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

    private <T extends Token<?>> T eat(Class<T> tokenType) {
        final Token<?> token = this.lookahead;

        if (token == null) {
            throw new SyntaxException("Unexpected end of input, expected: " + tokenType);
        }

        if (!tokenType.isInstance(token)) {
            throw new SyntaxException("Unexpected token: " + token.value() + ", expected: " + tokenType.getSimpleName());
        }

        this.lookahead = this.tokenizer.getNextToken();
        //noinspection unchecked
        return (T) token;
    }
}
