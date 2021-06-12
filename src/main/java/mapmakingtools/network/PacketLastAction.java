package mapmakingtools.network;

import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLastAction {


    public PacketLastAction() {

    }

    public static void encode(PacketLastAction msg, PacketBuffer buf) {

    }

    public static PacketLastAction decode(PacketBuffer buf) {
        return new PacketLastAction();
    }

    public static void handle(final PacketLastAction msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (!FeatureAvailability.canEdit(player)) {
                return;
            }

            CommandTracker tracker = WorldData.get(player.getEntityWorld()).getCommandTracker();
            String lastCommand = tracker.getLastCommand(player);

            if (lastCommand == null) {
                return;
            }

            player.getServer().getCommandManager().handleCommand(player.getCommandSource(), lastCommand);
        });

        ctx.get().setPacketHandled(true);
    }
}
