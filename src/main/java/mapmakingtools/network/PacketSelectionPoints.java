package mapmakingtools.network;

import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.client.ClientSelection;
import mapmakingtools.worldeditor.Selection;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketSelectionPoints {

    public ISelection selection;

    public PacketSelectionPoints(ISelection selection) {
        this.selection = selection;
    }

    public static void encode(PacketSelectionPoints msg, PacketBuffer buf) {
        msg.selection.write(buf);
    }

    public static PacketSelectionPoints decode(PacketBuffer buf) {
        return new PacketSelectionPoints(Selection.read(buf));
    }

    public static void handle(final PacketSelectionPoints msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientSelection.SELECTION = msg.selection;
        });

        ctx.get().setPacketHandled(true);
    }

    public static void writeBlockPos(PacketBuffer buf, @Nullable BlockPos pos) {
        if (pos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(pos);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static BlockPos readBlockPos(PacketBuffer buf) {
        if (buf.readBoolean()) {
            return buf.readBlockPos();
        }

        return null;
    }
}
