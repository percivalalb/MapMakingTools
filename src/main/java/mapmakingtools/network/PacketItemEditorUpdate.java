package mapmakingtools.network;

import io.netty.buffer.ByteBuf;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketItemEditorUpdate(int slotIndex, IItemAttribute attributeManager, ByteBuf data) {

    public static void encode(PacketItemEditorUpdate msg, ByteBuf _buf) {
        FriendlyByteBuf buf = friendly(_buf);
        buf.writeInt(msg.slotIndex);
        buf.writeRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES.get(), msg.attributeManager);
        buf.writeBytes(msg.data);
    }

    public static PacketItemEditorUpdate decode(FriendlyByteBuf buf) {
        int slotId = buf.readInt();
        IItemAttribute attributeManager = buf.readRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES.get());
        return new PacketItemEditorUpdate(slotId, attributeManager, buf.discardReadBytes().copy());
    }

    public static void handle(final PacketItemEditorUpdate msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            ItemStack stack = player.getInventory().getItem(msg.slotIndex).copy();

            try {
                // Protected servers against people by-passing the GUI and
                // sending illegal packets directly
                if (!msg.attributeManager.canUse()) {
                    throw new IllegalAccessException("The feature is not enabled.");
                }

                stack = msg.attributeManager.read(stack, friendly(msg.data), player);
                player.getInventory().setItem(msg.slotIndex, stack);
            } catch (Exception e) {
                MapMakingTools.LOGGER.warn("Failed to edit item", e);
            } finally {
                msg.data.clear(); // clears data to free memory
            }
        });

        ctx.get().setPacketHandled(true);
    }

    private static FriendlyByteBuf friendly(ByteBuf buf) {
        return buf instanceof FriendlyByteBuf bufF ? bufF : new FriendlyByteBuf(buf);
    }
}
