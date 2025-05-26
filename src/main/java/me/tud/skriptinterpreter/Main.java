package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;

public class Main {

    public static void main(String[] args) {
        LexicalAnalyzer analyzer = new LexicalAnalyzer("""
                on load:
                    broadcast .123"Hello, \\"World!"
                """);
        System.out.println(analyzer.tokenize());
    }

}
