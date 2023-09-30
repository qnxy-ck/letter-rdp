package com.ck;

import com.ck.token.Token;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配信息
 * 
 * @author 陈坤
 * 2023/9/30
 */
public record RegexpInfo(
        Pattern regexp,
        Function<String, ? extends Token<?>> token
) {

    public String match(String str) {
        Matcher matcher = this.regexp.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }
    
    public Token<?> makeToken(String tokenValue) {
        return this.token.apply(tokenValue);
    }
}
