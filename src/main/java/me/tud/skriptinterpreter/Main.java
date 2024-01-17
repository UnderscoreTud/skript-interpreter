package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.patterns.SkriptPattern;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

//        Context<Object> context = new Context<>(skript, new Object());
//        Effect<Object> effect = new EffPrint<>();
//        effect.init(new Expressions<>(new ExprString<>("test")), skript);
//        effect.execute(context);

        SkriptPattern pattern = skript.patterCompiler().compile("te\\st:hi");
        System.out.println(pattern.head());

        skript.cleanup();
    }

}
