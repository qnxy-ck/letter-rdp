package com.ck;

import com.ck.ast.ASTree;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * @author 陈坤
 * 2023/9/30
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String program = readText("/letter.txt");

        Parser parser = new Parser();

        ASTree asTree = parser.parse(program);

        ObjectMapper om = new ObjectMapper();

        String json = om.writerWithDefaultPrettyPrinter()
                .writeValueAsString(asTree);

        System.out.println(json);


    }

    private static String readText(String file) throws Exception {
        StringBuilder s = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(file)))) {

            char[] chars = new char[100];

            int temp;
            while ((temp = bufferedReader.read(chars)) != -1) {
                s.append(String.valueOf(chars, 0, temp));
            }
        }
        return s.toString();

    }
}