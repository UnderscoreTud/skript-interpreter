package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.List;

public record GroupNode(PegNode inner) implements PegNode {

    @Override
    public List<String> expand() {
        return inner.expand();
    }

}
