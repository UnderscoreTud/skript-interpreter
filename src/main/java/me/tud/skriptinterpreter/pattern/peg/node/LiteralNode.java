package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.Collections;
import java.util.List;

public record LiteralNode(String text) implements PegNode {

    @Override
    public List<String> expand() {
        return Collections.singletonList(text);
    }

}
