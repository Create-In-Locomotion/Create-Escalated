package rbasamoyai.escalated.index;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.escalated.CreateEscalated;

public class EscalatedPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() { return CreateEscalated.MOD_ID; }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        EscalatedPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        EscalatedPonderTags.register(helper);
    }
}

