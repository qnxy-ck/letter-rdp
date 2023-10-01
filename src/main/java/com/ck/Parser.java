package com.ck;

import com.ck.ast.*;
import com.ck.ast.literal.*;
import com.ck.ast.statement.*;
import com.ck.token.*;
import com.ck.token.keyword.ElseToken;
import com.ck.token.keyword.IfToken;
import com.ck.token.keyword.LetToken;
import com.ck.token.keyword.NullToken;
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
        if (tree instanceof Identifier) {
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
            | EqualityExpression ADDITIVE_OPERATOR RelationalExpression
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

    /**
     * 构建逻辑运算表达式
     *
     * @param builderMethod     构建数据
     * @param operatorTokenType 运算符类型
     */
    private ASTree logicalExpression(Supplier<ASTree> builderMethod, Class<? extends OperatorToken> operatorTokenType) {
        ASTree left = builderMethod.get();

        while (this.lookahead.getClass() == operatorTokenType) {
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
        if (this.lookahead.getClass() == AdditiveOperatorToken.class) {
            operator = this.eat(AdditiveOperatorToken.class);
        } else if (this.lookahead.getClass() == LogicalNotToken.class) {
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
           : PrimaryExpression
           ;
    */
    private ASTree leftHandSideExpression() {
        return this.primaryExpression();
    }


    /*
        PrimaryExpression
            : Literal
            | ParenthesizedExpression
            | Identifier
            ;
     */
    private ASTree primaryExpression() {
        if (this.lookahead instanceof LiteralToken<?>) {
            return this.literal();
        } else if (this.lookahead.getClass() == OpenParenthesisToken.class) {
            return this.parenthesizedExpression();
        } else if (this.lookahead.getClass() == IdentifierToken.class) {
            return this.identifier();
        }
        return this.leftHandSideExpression();
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
        } else if (this.lookahead.getClass() == NumberToken.class) {
            return this.numericLiteral();
        } else if (this.lookahead.getClass() == StringToken.class) {
            return this.stringLiteral();
        } else if (this.lookahead.getClass() == NullToken.class) {
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
