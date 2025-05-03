package rbasamoyai.escalated.advancements;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import rbasamoyai.escalated.index.EscalatedTriggers;

import java.util.Iterator;
import java.util.Map;

public class WalkwayTravelTracker {

    private static final Reference2ObjectArrayMap<Player, WalkwayTravelInfo> PLAYER_STATS = new Reference2ObjectArrayMap<>();

    public static void tick() {
        for (Iterator<Map.Entry<Player, WalkwayTravelInfo>> iter = PLAYER_STATS.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Player, WalkwayTravelInfo> entry = iter.next();
            Player player = entry.getKey();
            WalkwayTravelInfo info = entry.getValue();
            if (player.isRemoved() || !player.isAlive() || player.level().dimension() != info.attemptDimension) {
                iter.remove();
                continue;
            }
            if (info.onWalkway) {
                info.lastY = player.getBlockY();
                if (info.lastY - info.startY >= 100) {
                    EscalatedTriggers.ESCALATOR_100.tryAwardingTo(player);
                    if (player.level().dimension() == Level.NETHER)
                        EscalatedTriggers.ESCALATOR_100_NETHER.tryAwardingTo(player);
                    iter.remove();
                }
            } else {
                int yDiff = player.getBlockY() - info.lastY;
                --info.ticksOnWalkway;
                if (Math.abs(yDiff) > 3 || info.ticksOnWalkway < 1)
                    iter.remove();
            }
        }
    }

    public static void clearList() { PLAYER_STATS.clear(); }

    public static void stopTrackingPlayerOnWalkway(Player player) {
        if (!PLAYER_STATS.containsKey(player))
            return;
        WalkwayTravelInfo info = PLAYER_STATS.get(player);
        info.onWalkway = false;
    }

    public static void trackPlayerOnWalkway(Player player, int time) {
        if (!PLAYER_STATS.containsKey(player)) {
            WalkwayTravelInfo info = new WalkwayTravelInfo();
            info.startY = player.getBlockY();
            info.attemptDimension = player.level().dimension();
            PLAYER_STATS.put(player, info);
        }
        WalkwayTravelInfo info = PLAYER_STATS.get(player);
        info.ticksOnWalkway = time;
        info.lastY = player.getBlockY();
        info.onWalkway = true;
    }

    static class WalkwayTravelInfo {
        int ticksOnWalkway;
        boolean onWalkway;
        int startY;
        int lastY;
        ResourceKey<Level> attemptDimension;
    }

}
