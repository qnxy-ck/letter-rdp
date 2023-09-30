package com.ck;

import com.ck.token.NumberToken;
import com.ck.token.StringToken;
import com.ck.token.Token;

import java.util.regex.Pattern;

/**
 * 词法解析器
 *
 * @author 陈坤
 * 2023/9/30
 */
public class Tokenizer {

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
            // 数字:
            new RegexpInfo(Pattern.compile("^\\d+"), it -> new NumberToken(Integer.parseInt(it))),

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
