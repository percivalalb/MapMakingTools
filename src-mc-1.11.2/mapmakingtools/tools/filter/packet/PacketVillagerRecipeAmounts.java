package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerRecipeAmounts extends AbstractServerMessage {

	public int recipeAmount;
	
	public PacketVillagerRecipeAmounts() {}
	public PacketVillagerRecipeAmounts(int recipeAmount) {
		this.recipeAmount = recipeAmount;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.recipeAmount = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.recipeAmount);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(player.openContainer instanceof IContainerFilter) {
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
				shop.maxRecipesMap.put(player.getUniqueID(), this.recipeAmount);
				shop.addOnlySlots(container);
			}
		}

	}
}
