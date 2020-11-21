package mapmakingtools.network;

import java.util.function.Supplier;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketItemEditorUpdate {

    public int slotIndex;
    public IItemAttribute attributeManager;
    public PacketBuffer data;

    public PacketItemEditorUpdate(int slotIndex, IItemAttribute attributeManager, PacketBuffer data) {
        this.slotIndex = slotIndex;
        this.attributeManager = attributeManager;
        this.data = data;
    }

    public static void encode(PacketItemEditorUpdate msg, PacketBuffer buf) {
        buf.writeInt(msg.slotIndex);
        buf.writeRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES, msg.attributeManager);
        buf.writeBytes(msg.data);
    }

    public static PacketItemEditorUpdate decode(PacketBuffer buf) {
        int slotId = buf.readInt();
        IItemAttribute attributeManager = buf.readRegistryIdUnsafe(Registries.ITEM_ATTRIBUTES);
        return new PacketItemEditorUpdate(slotId, attributeManager, new PacketBuffer(buf.discardReadBytes()));
    }

    public static void handle(final PacketItemEditorUpdate msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            ItemStack stack = player.inventory.getStackInSlot(msg.slotIndex).copy();
            stack = msg.attributeManager.read(stack, msg.data);
            msg.data.clear(); // clears data to free memory
            player.inventory.setInventorySlotContents(msg.slotIndex, stack);
        });

        ctx.get().setPacketHandled(true);
    }
}
