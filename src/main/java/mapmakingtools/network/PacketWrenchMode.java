package mapmakingtools.network;

import mapmakingtools.item.WrenchItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketWrenchMode(int slotIdx, boolean up) {

    public static void encode(PacketWrenchMode msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slotIdx);
        buf.writeBoolean(msg.up);
    }

    public static PacketWrenchMode decode(FriendlyByteBuf buf) {
        return new PacketWrenchMode(buf.readInt(), buf.readBoolean());
    }

    public static void handle(final PacketWrenchMode msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            ItemStack stack = player.getInventory().getItem(msg.slotIdx).copy();

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
            player.getInventory().setItem(msg.slotIdx, stack);
        });

        ctx.get().setPacketHandled(true);
    }
}
