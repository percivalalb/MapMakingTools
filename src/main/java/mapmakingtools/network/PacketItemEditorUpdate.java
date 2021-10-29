package mapmakingtools.network;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItemEditorUpdate {

    public int slotIndex;
    public IItemAttribute attributeManager;
    public FriendlyByteBuf data;

    public PacketItemEditorUpdate(int slotIndex, IItemAttribute attributeManager, FriendlyByteBuf data) {
        this.slotIndex = slotIndex;
        this.attributeManager = attributeManager;
        this.data = data;
    }

    public static void encode(PacketItemEditorUpdate msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slotIndex);
        buf.writeRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES, msg.attributeManager);
        buf.writeBytes(msg.data);
    }

    public static PacketItemEditorUpdate decode(FriendlyByteBuf buf) {
        int slotId = buf.readInt();
        IItemAttribute attributeManager = buf.readRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES);
        return new PacketItemEditorUpdate(slotId, attributeManager, new FriendlyByteBuf(buf.discardReadBytes()));
    }

    public static void handle(final PacketItemEditorUpdate msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            ItemStack stack = player.inventory.getItem(msg.slotIndex).copy();

            try {
                // Protected servers against people by-passing the GUI and
                // sending illegal packets directly
                if (!msg.attributeManager.canUse()) {
                    throw new IllegalAccessException("The feature is not enabled.");
                }

                stack = msg.attributeManager.read(stack, msg.data, player);
                player.inventory.setItem(msg.slotIndex, stack);
            } catch (Exception e) {
                MapMakingTools.LOGGER.warn("Failed to edit item", e);
            } finally {
                msg.data.clear(); // clears data to free memory
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
