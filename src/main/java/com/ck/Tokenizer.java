package com.ck;

import com.ck.token.Token;
import com.ck.token.keyword.*;
import com.ck.token.literal.IdentifierToken;
import com.ck.token.literal.NumberToken;
import com.ck.token.literal.StringToken;
import com.ck.token.operator.*;
import com.ck.token.symbol.*;

import java.util.regex.Pattern;

import static com.ck.token.OperatorToken.toBuilder;

/**
 * 词法解析器
 *
 * @author 陈坤
 * 2023/9/30
 */
public class Tokenizer {

    /**
     * 正则匹配规则
     */
    private static final RegexpInfo[] TOKENIZER_SPEC_ARR = {
            // --------------------------------------------------
            // 空白字符:
            new RegexpInfo(Pattern.compile("^\\s+"), it -> null),

            // --------------------------------------------------
            // 跳过单行注释
            new RegexpInfo(Pattern.compile("^//.*"), it -> null),

            // 跳过多行注释
            new RegexpInfo(Pattern.compile("^/\\*[\\s\\S]*?\\*/"), it -> null),

            // --------------------------------------------------
            // 符号, 分隔符
            new RegexpInfo(Pattern.compile("^;"), it -> SemicolonToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\{"), it -> OpenBraceToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^}"), it -> ClosedBraceToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\("), it -> OpenParenthesisToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\)"), it -> ClosedParenthesisToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^,"), it -> CommaToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\."), it -> DotToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\["), it -> OpenSquareBracketsToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^]"), it -> ClosedSquareBracketsToken.INSTANCE),

            // --------------------------------------------------
            // 关键字
            new RegexpInfo(Pattern.compile("^\\blet\\b"), it -> LetToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bif\\b"), it -> IfToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\belse\\b"), it -> ElseToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\btrue\\b"), it -> TrueToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bfalse\\b"), it -> FalseToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bnull\\b"), it -> NullToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bwhile\\b"), it -> WhileToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bdo\\b"), it -> DoToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bfor\\b"), it -> ForToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bdef\\b"), it -> DefToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\breturn\\b"), it -> ReturnToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bclass\\b"), it -> ClassToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bextends\\b"), it -> ExtendsToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bsuper\\b"), it -> SuperToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bnew\\b"), it -> NewToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\bthis\\b"), it -> ThisToken.INSTANCE),

            // --------------------------------------------------
            // 数字:
            new RegexpInfo(Pattern.compile("^\\d+"), it -> new NumberToken(Integer.parseInt(it))),

            // --------------------------------------------------
            // 标识符
            new RegexpInfo(Pattern.compile("^\\w+"), IdentifierToken::new),

            // --------------------------------------------------
            // 等式运算符: ==. !=
            new RegexpInfo(Pattern.compile("^[=!]="), it -> toBuilder(EqualityOperatorToken.values(), it)),


            // --------------------------------------------------
            // 赋值运算符: =, *=, /=, +=. -=
            new RegexpInfo(Pattern.compile("^="), it -> SimpleAssignToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^[*/+-]="), it -> toBuilder(ComplexAssignToken.values(), it)),


            // --------------------------------------------------
            // 数学运算符: +, -, *, /
            new RegexpInfo(Pattern.compile("^[+-]"), it -> toBuilder(AdditiveOperatorToken.values(), it)),
            new RegexpInfo(Pattern.compile("^[*/]"), it -> toBuilder(MultiplicativeOperatorToken.values(), it)),

            // --------------------------------------------------
            // 关系运算符 >, >=, <, <=
            new RegexpInfo(Pattern.compile("^[><]=?"), it -> toBuilder(RelationalOperatorToken.values(), it)),

            // --------------------------------------------------
            // 逻辑运算符 &&, ||
            new RegexpInfo(Pattern.compile("^&&"), it -> LogicalAndToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^\\|\\| "), it -> LogicalOrToken.INSTANCE),
            new RegexpInfo(Pattern.compile("^!"), it -> LogicalNotToken.INSTANCE),


            // --------------------------------------------------
            // 字符串:
            new RegexpInfo(Pattern.compile("^\"[^\"]*\""), StringToken::new),
            new RegexpInfo(Pattern.compile("^'[^']*'"), StringToken::new),


    };


    private String string;
    private int cursor = 0;


    public void init(String string) {
        this.string = string;
    }


    private Boolean hasMoreTokens() {
        if (this.string == null) {
            throw new RuntimeException("请先调用初始化方法(#.init(string)), 并初始化内容不能为null.");
        }
        return this.cursor < this.string.length();
    }

    /**
     * 获取一个Token
     */
    public Token<?> getNextToken() {
        if (!hasMoreTokens()) {
            return null;
        }

        final String s = this.string.substring(this.cursor);

        for (RegexpInfo regexpInfo : TOKENIZER_SPEC_ARR) {
            String tokenValue = regexpInfo.match(s);
            if (tokenValue == null) {
                continue;
            }

            this.cursor += tokenValue.length();

            Token<?> token = regexpInfo.makeToken(tokenValue);
            if (token == null) {
                return this.getNextToken();
            }
            return token;
        }

        throw new SyntaxException("Unexpected token: [" + this.string.charAt(0) + "]");
    }
}
