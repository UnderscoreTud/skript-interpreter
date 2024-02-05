package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.expressions.ExprString;
import me.tud.skriptinterpreter.elements.expressions.ExprTest;
import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lang.SyntaxElement;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import me.tud.skriptinterpreter.util.Result;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

        SyntaxRegistry<Expression<?, ?>> expressionRegistry = SyntaxRegistry.of(skript, SyntaxElement.class);
        expressionRegistry.register(ExprTest.class, ExprTest::new, "test");

//        Context<Object> context = new Context<>(skript, new Object());
//        Result<String> result = new ExprString<>("test").get(context);
//        System.out.println(result.getKeyed());
//        Effect<Object> effect = new EffPrint<>();
//        effect.init(new Expressions<>(new ExprString<>("test")), skript);
//        effect.execute(context);

        skript.cleanup();
    }

}
