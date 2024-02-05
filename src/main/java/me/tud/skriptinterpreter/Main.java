package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.expressions.ExprString;
import me.tud.skriptinterpreter.elements.expressions.ExprTest;
import me.tud.skriptinterpreter.elements.expressions.ExprTest2;
import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lang.SyntaxElement;
import me.tud.skriptinterpreter.parser.SkriptParser;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import me.tud.skriptinterpreter.util.Result;

import java.util.EnumSet;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

        SyntaxRegistry<Expression<?, ?>> expressionRegistry = SyntaxRegistry.of(skript, SyntaxElement.class);
        expressionRegistry.register(ExprTest.class, ExprTest::new, "test");
        expressionRegistry.register(ExprTest2.class, ExprTest2::new, "<\"(.+)\">", "<'(.+)'>");

        SkriptParser parser = SkriptParser.create(skript, "'Hello, World'", EnumSet.allOf(SkriptParser.Flag.class));
        Expression<?, ?> expression = parser.parse(expressionRegistry);

        Context<Object> context = new Context<>(skript, new Object());
        expression.get((Context) context);

        Effect<Object> effect = new EffPrint<>();
        effect.init(skript, new Expressions(expression), null);
        effect.execute(context);

        skript.cleanup();
    }

}
