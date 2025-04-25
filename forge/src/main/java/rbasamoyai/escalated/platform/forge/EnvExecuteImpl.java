package rbasamoyai.escalated.platform.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

public class EnvExecuteImpl {

    public static void executeOnClient(Supplier<Runnable> run) { DistExecutor.unsafeRunWhenOn(Dist.CLIENT, run); }

}
