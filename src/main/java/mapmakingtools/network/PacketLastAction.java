package mapmakingtools.network;

import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.storage.WorldData;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLastAction {


    public PacketLastAction() {

    }

    public static void encode(PacketLastAction msg, FriendlyByteBuf buf) {

    }

    public static PacketLastAction decode(FriendlyByteBuf buf) {
        return new PacketLastAction();
    }

    public static void handle(final PacketLastAction msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (!FeatureAvailability.canEdit(player)) {
                return;
            }

            CommandTracker tracker = WorldData.get(player.getCommandSenderWorld()).getCommandTracker();
            String lastCommand = tracker.getLastCommand(player);

            if (lastCommand == null) {
                return;
            }

            player.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), lastCommand);
        });

        ctx.get().setPacketHandled(true);
    }
}
