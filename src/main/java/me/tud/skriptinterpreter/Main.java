package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;

public class Main {

    public static void main(String[] args) {
        PatternTrie trie = new PatternTrie();
        trie.registerPattern("test", trie);
        trie.registerPattern("hello", trie);
        trie.registerPattern("%expression%[ ](+|-|*|/|^)[ ]%expression%", trie);

        trie.print();
        System.out.println();
        System.out.println(trie.match("test+hello/test-test+test*hello"));
    }

}
