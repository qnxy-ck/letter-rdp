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
        return this.additiveExpression();
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
            ;
     */
    private ASTree primaryExpression() {
        if (this.lookahead.getClass() == OpenParenthesisToken.class) {
            return this.parenthesizedExpression();
        } else {
            return this.literal();
        }
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
