package mapmakingtools.network;

import mapmakingtools.client.ClientSelection;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateLastCommand {

    private String lastCommand;

    public PacketUpdateLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public static void encode(PacketUpdateLastCommand msg, PacketBuffer buf) {
        buf.writeBoolean(msg.lastCommand != null);
        if (msg.lastCommand != null) {
            buf.writeString(msg.lastCommand, 256);
        }
    }

    public static PacketUpdateLastCommand decode(PacketBuffer buf) {
        return new PacketUpdateLastCommand(buf.readBoolean() ? buf.readString(256) : null);
    }

    public static void handle(final PacketUpdateLastCommand msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientSelection.LAST_COMMAND = msg.lastCommand;
        });

        ctx.get().setPacketHandled(true);
    }
}
