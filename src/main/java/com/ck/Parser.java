package com.ck;

import com.ck.ast.*;
import com.ck.token.NumberToken;
import com.ck.token.StringToken;
import com.ck.token.Token;

/**
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


    private ASTree program() {
        return new Program(
                this.literal()
        );

    }

    /*
        Literal
            : NumericLiteral
            | StringLiteral
            ;
     */
    private Literal literal() {
        @SuppressWarnings("rawtypes") Class<? extends Token> lookaheadClass = this.lookahead.getClass();

        if (lookaheadClass == NumberToken.class) {
            return this.numericLiteral();
        } else if (lookaheadClass == StringToken.class) {
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
