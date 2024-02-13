package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class SkriptPattern implements SkriptProperty {

    private final Skript skript;
    private PatternElement head, tail;
    private int size;

    public SkriptPattern(Skript skript, PatternElement head) {
        this(skript);
        append(head);
    }

    public SkriptPattern(Skript skript) {
        this.skript = Objects.requireNonNull(skript, "skript");
    }

    public @Nullable MatchResult match(String input) {
        return match(new StringReader(input), MatchResult.builder(input));
    }

    public @Nullable MatchResult match(StringReader reader, MatchResult.Builder builder) {
        if (!head().match(reader, builder, true) || reader.canRead()) return null;
        return builder.build();
    }

    public SkriptPattern append(PatternElement element) {
        if (element == null) return this;
        if (head == null) {
            head = element;
            tail = getLastElement();
        } else {
            if (tail == null) tail = getLastElement();
            assert tail != null;
            tail.next(tail = element);
            size++;
        }
        return this;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    public PatternElement head() {
        return head;
    }
    
    public PatternElement tail() {
        return tail;
    }

    public int size() {
        return size;
    }

    private PatternElement getLastElement() {
        size = 0;
        PatternElement element = head;
        while (element != null) {
            size++;
            if (element.next() == null) return element;
            element = element.next();
        }
        return null;
    }

    @Contract(pure = true)
    public @UnmodifiableView List<PatternElement> elements() {
        List<PatternElement> list = new ArrayList<>(size);
        forEach(list::add);
        return Collections.unmodifiableList(list);
    }

    @Override
    public Skript skript() {
        return skript;
    }

    private void forEach(Consumer<PatternElement> consumer) {
        PatternElement current = head;
        while (current != null) {
            consumer.accept(current);
            current = current.next();
        }
    }

    @Override
    public String toString() {
        return head().toFullString();
    }

}
