package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketUpdateLastCommand;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.PacketDistributor;

public class World {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(World::onJoinWorld);
        forgeEventBus.addListener(World::onLeaveWorld);
        forgeEventBus.addListener(EventPriority.LOWEST, World::changeDimension);
    }

    public static void onJoinWorld(final EntityJoinWorldEvent e) {
        if (e.getWorld().isClientSide) {
            return;
        }

        Entity entity = e.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) entity;

        // TODO Combine
        DimensionData.get(e.getWorld()).getSelectionManager().sync(player);
        CommandTracker tracker = WorldData.get(e.getWorld()).getCommandTracker();
        MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> player),
                new PacketUpdateLastCommand(tracker.getLastCommand(player)));
    }

    public static void onLeaveWorld(final EntityLeaveWorldEvent e) {

    }

    public static void changeDimension(final PlayerEvent.PlayerChangedDimensionEvent e) {
        DimensionData.get(e.getEntity().getCommandSenderWorld().getServer().getLevel(e.getTo())).getSelectionManager().sync(e.getPlayer());
    }
}
