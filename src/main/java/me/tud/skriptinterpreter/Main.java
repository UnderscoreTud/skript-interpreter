package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        PatternTrie trie = skript.patternTrie();
        trie.insert("[an] expression");
        trie.insert("hello [%expression%] [%expression%] world", trie);
        trie.print();
        System.out.println();
        System.out.println(trie.match("hello world"));
        System.out.println(trie.match("hello expression world"));
        System.out.println(trie.match("hello expression an expression world"));
    }

}
