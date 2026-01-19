package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.lang.lexer.source.SourceTokenType;
import me.tud.skriptinterpreter.lang.lexer.source.SourceTokenizer;
import me.tud.skriptinterpreter.lang.lexer.source.SourceTokenizerImpl;

public class Main {

    public static void main(String[] args) {
//        PatternTokenizer tokenizer = new PatternTokenizerImpl("test:hi :hi");
//        System.out.println(tokenizer.tokenize());
        SourceTokenizer tokenizer = new SourceTokenizerImpl("""
                .
                """);
        int[] indent = {0};
        SourceTokenType[] prev = {SourceTokenType.WORD};
        tokenizer.tokenize().forEach(token -> {
            if (token.type() == SourceTokenType.DEDENT)
                indent[0]--;
            if (prev[0] == SourceTokenType.NEWLINE || prev[0] == SourceTokenType.INDENT || prev[0] == SourceTokenType.DEDENT || token.type() == SourceTokenType.EOF) {
                System.out.println();
                System.out.print("\t".repeat(indent[0]));
            }
            if (token.type() == SourceTokenType.INDENT)
                indent[0]++;
            System.out.print(token.type());
            if (!token.text().isBlank())
                System.out.print("(" + token.text() + ")");
            System.out.print(" ");
            prev[0] = token.type();
        });
        System.out.println();
    }

}
