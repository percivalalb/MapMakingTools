package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketUpdateLastCommand;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Command {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(Command::onCommand);
    }

    public static void onCommand(CommandEvent e) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            CommandSource source = e.getParseResults().getContext().getSource();
            Entity entity = source.getEntity();
            if (entity instanceof ServerPlayerEntity) {
                CommandTracker tracker = WorldData.get(server).getCommandTracker();
                tracker.setLastCommand((PlayerEntity) entity, e.getParseResults().getReader().getString());
                MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity),
                        new PacketUpdateLastCommand(tracker.getLastCommand((PlayerEntity) entity)));
            }
        }
    }
}
