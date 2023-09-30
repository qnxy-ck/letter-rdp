package com.ck;

import com.ck.ast.*;
import com.ck.token.*;

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
            ;
     */
    private Statement statement() {
        if (this.lookahead.getClass() == SemicolonToken.class) {
            return this.emptyStatement();
        } else if (this.lookahead.getClass() == OpenBraceToken.class) {
            return this.blockStatement();
        } else {
            return this.expressionStatement();
        }
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
            : AdditiveExpression
            | LeftHandSideExpression ASSIGNMENT_OPERATOR AssignmentExpression
            ;
     */
    private ASTree assignmentExpression() {
        ASTree left = this.additiveExpression();

        // 如果下一个Token不是赋值运算符直接返回
        if (!this.isAssignmentOperator(this.lookahead.getClass())) {
            return left;
        }

        return new AssignmentExpression(
                this.assignmentOperator().toAssignmentOperator(),
                this.checkValidAssignmentTarget(left),
                this.assignmentExpression()
        );
    }

    /*
        LeftHandSideExpression
            : Identifier
            ;
     */
    private ASTree leftHandSideExpression() {
        return this.identifier();
    }

    /*
        Identifier
            : IDENTIFIER
            ;
     */
    private ASTree identifier() {
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
    private AssignToken assignmentOperator() {
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
     * @param builderMethod     方法名称
     * @param operatorTokenType 运算符类型
     */
    private ASTree binaryExpression(Supplier<ASTree> builderMethod, Class<? extends OperatorToken> operatorTokenType) {
        ASTree left = builderMethod.get();

        while (this.lookahead.getClass() == operatorTokenType) {
            OperatorToken operator = this.eat(operatorTokenType);
            ASTree right = builderMethod.get();

            left = new BinaryExpression(
                    operator.toBinaryOperator(),
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
        return lookahead.getClass() == NumberToken.class || lookahead.getClass() == StringToken.class;
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
            ;
     */
    private Literal literal() {
        if (this.lookahead.getClass() == NumberToken.class) {
            return this.numericLiteral();
        } else if (this.lookahead.getClass() == StringToken.class) {
            return this.stringLiteral();
        }

        throw new SyntaxException("Literal: unexpected literal production");
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
            throw new SyntaxException("Unexpected token: " + token.value() + ", expected: " + tokenType);
        }

        this.lookahead = this.tokenizer.getNextToken();
        //noinspection unchecked
        return (T) token;
    }
}
