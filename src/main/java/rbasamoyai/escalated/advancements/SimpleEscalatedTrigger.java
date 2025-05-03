package rbasamoyai.escalated.advancements;

import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.escalated.CreateEscalated;

public class SimpleEscalatedTrigger extends SimpleCreateTrigger {

    private final ResourceLocation id;

    public SimpleEscalatedTrigger(String id) {
        super(id);
        this.id = CreateEscalated.resource(id);
    }

    @Override public ResourceLocation getId() { return this.id; }

    public void tryAwardingTo(Player player) {
        if (player instanceof ServerPlayer splayer)
            this.trigger(splayer);
    }

}
