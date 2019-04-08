package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerRecipeAmounts {

	public int recipeAmount;
	
	public PacketVillagerRecipeAmounts(int recipeAmount) {
		this.recipeAmount = recipeAmount;
	}
	
	public static void encode(PacketVillagerRecipeAmounts msg, PacketBuffer buf) {
		buf.writeInt(msg.recipeAmount);
	}
	
	public static PacketVillagerRecipeAmounts decode(PacketBuffer buf) {
		int recipeAmount = buf.readInt();
		return new PacketVillagerRecipeAmounts(recipeAmount);
	}
	
	public static class Handler {
        public static void handle(final PacketVillagerRecipeAmounts msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(player.openContainer instanceof IFilterContainer) {
        			IFilterContainer container = (IFilterContainer)player.openContainer;
        			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
        				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
        				shop.maxRecipesMap.put(player.getUniqueID(), msg.recipeAmount);
        				shop.addOnlySlots(container);
        			}
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
