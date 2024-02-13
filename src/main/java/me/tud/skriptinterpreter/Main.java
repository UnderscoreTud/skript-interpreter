package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.expressions.ExprString;
import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.parser.SkriptParser;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.patterns.ParseTagPatternElement;
import me.tud.skriptinterpreter.patterns.SkriptPattern;
import me.tud.skriptinterpreter.types.Type;

import java.util.EnumSet;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

        skript.typeRegistry().register(Type.builder(String.class)
                .codename("string")
                .name("string", "strings")
                .name("text", "texts")
                .build());
        
        skript.effects().register(EffPrint.class, EffPrint::new, "print %string%");
        skript.expressions().register(ExprString.class, ExprString.PARSER);

        SkriptParser parser = SkriptParser.create(skript, "print \"test\"\"\"", EnumSet.allOf(SkriptParser.Flag.class));
        Effect<?> effect = parser.parse(skript.effects());
        effect.execute(null);

//        SkriptParser parser = SkriptParser.create(skript, "'Hello, World'", EnumSet.allOf(SkriptParser.Flag.class));
//        Expression<?, ?> expression = parser.parse(expressionRegistry);
//
//        Context<Object> context = new Context<>(skript, new Object());
//        expression.get((Context) context);
//
//        Effect<Object> effect = new EffPrint<>();
//        effect.init(skript, new Expressions(expression), null);
//        effect.execute(context);

        skript.cleanup();
    }

}
