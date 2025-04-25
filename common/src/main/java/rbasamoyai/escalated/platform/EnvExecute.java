package rbasamoyai.escalated.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Supplier;

public class EnvExecute {

    @ExpectPlatform public static void executeOnClient(Supplier<Runnable> run) { throw new AssertionError(); }

}
