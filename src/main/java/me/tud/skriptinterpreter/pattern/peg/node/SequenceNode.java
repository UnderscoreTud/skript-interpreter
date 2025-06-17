package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SequenceNode(List<PegNode> elements) implements PegNode {

    @Override
    public List<String> expand() {
        List<String> result = Collections.emptyList();
        for (PegNode element : elements)
            result = combine(result, element.expand());
        return result;
    }

    private static List<String> combine(List<String> prefix, List<String> suffix) {
        if (prefix.isEmpty()) return suffix;
        if (suffix.isEmpty()) return prefix;

        List<String> result = new ArrayList<>(prefix.size() * suffix.size());
        for (String s : prefix)
            for (String t : suffix)
                result.add(s + t);
        return result;
    }

}
