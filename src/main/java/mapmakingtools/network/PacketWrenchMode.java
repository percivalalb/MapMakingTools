package mapmakingtools.network;

import mapmakingtools.item.WrenchItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketWrenchMode {

    public final int slotIdx;
    public final boolean up;

    public PacketWrenchMode(int slotIdx, boolean up) {
        this.slotIdx = slotIdx;
        this.up = up;
    }

    public static void encode(PacketWrenchMode msg, PacketBuffer buf) {
        buf.writeInt(msg.slotIdx);
        buf.writeBoolean(msg.up);
    }

    public static PacketWrenchMode decode(PacketBuffer buf) {
        return new PacketWrenchMode(buf.readInt(), buf.readBoolean());
    }

    public static void handle(final PacketWrenchMode msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            ItemStack stack = player.inventory.getStackInSlot(msg.slotIdx).copy();

            WrenchItem.Mode mode = WrenchItem.getMode(stack);
            WrenchItem.Mode tmp;
            for (int i = 1; i < WrenchItem.Mode.values().length; i++) {
                tmp = WrenchItem.Mode.values()[(mode.ordinal() + (msg.up ? i : WrenchItem.Mode.values().length - i)) % WrenchItem.Mode.values().length];
                if (tmp.isVisible()) {
                    mode = tmp;
                    break;
                }
            }
            stack.getOrCreateTag().putString("mode", mode.getModeName());
            player.inventory.setInventorySlotContents(msg.slotIdx, stack);
        });

        ctx.get().setPacketHandled(true);
    }
}
