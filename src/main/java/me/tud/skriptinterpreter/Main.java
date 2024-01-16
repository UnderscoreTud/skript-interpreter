package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.expressions.ExprString;
import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expressions;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

        Context<Object> context = new Context<>(skript, new Object());
        Effect<Object> effect = new EffPrint<>();
        effect.init(new Expressions<>(new ExprString<>("test")), skript);
        effect.execute(context);

        skript.cleanup();
    }

}
