package rbasamoyai.escalated.platform.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;

import java.util.function.Supplier;

public class EnvExecuteImpl {

    public static void executeOnClient(Supplier<Runnable> run) { EnvExecutor.runWhenOn(EnvType.CLIENT, run); }

}
