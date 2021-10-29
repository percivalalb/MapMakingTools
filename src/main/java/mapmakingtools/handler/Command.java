package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketUpdateLastCommand;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
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
            CommandSourceStack source = e.getParseResults().getContext().getSource();
            Entity entity = source.getEntity();
            if (entity instanceof ServerPlayer) {
                CommandTracker tracker = WorldData.get(server).getCommandTracker();
                tracker.setLastCommand((Player) entity, e.getParseResults().getReader().getString());
                MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity),
                        new PacketUpdateLastCommand(tracker.getLastCommand((Player) entity)));
            }
        }
    }
}
