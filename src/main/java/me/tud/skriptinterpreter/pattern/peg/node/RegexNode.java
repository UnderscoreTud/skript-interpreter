package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.Collections;
import java.util.List;

public record RegexNode(String regex, int index) implements PegNode {

    @Override
    public List<String> expand() {
        return Collections.singletonList("<" + regex + ":" + index + ">");
    }

}
