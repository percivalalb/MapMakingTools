package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketUpdateLastCommand;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;

public class World {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(World::onJoinWorld);
        forgeEventBus.addListener(World::onLeaveWorld);
        forgeEventBus.addListener(EventPriority.LOWEST, World::changeDimension);
    }

    public static void onJoinWorld(final EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide) {
            return;
        }

        Entity entity = e.getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }

        ServerPlayer player = (ServerPlayer) entity;

        // TODO Combine
        DimensionData.get(e.getLevel()).getSelectionManager().sync(player);
        CommandTracker tracker = WorldData.get(e.getLevel()).getCommandTracker();
        MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> player),
                new PacketUpdateLastCommand(tracker.getLastCommand(player)));
    }

    public static void onLeaveWorld(final EntityLeaveLevelEvent e) {

    }

    public static void changeDimension(final PlayerEvent.PlayerChangedDimensionEvent e) {
        DimensionData.get(e.getEntity().getCommandSenderWorld().getServer().getLevel(e.getTo())).getSelectionManager().sync(e.getEntity());
    }
}
