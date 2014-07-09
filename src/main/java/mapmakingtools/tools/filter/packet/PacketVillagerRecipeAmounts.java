package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerRecipeAmounts extends IPacket {

	public int recipeAmount;
	
	public PacketVillagerRecipeAmounts() {}
	public PacketVillagerRecipeAmounts(int recipeAmount) {
		this.recipeAmount = recipeAmount;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.recipeAmount = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.recipeAmount);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof IContainerFilter) {
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
				shop.maxRecipesMap.put(player.getCommandSenderName(), this.recipeAmount);
				shop.addOnlySlots(container);
			}
		}

	}
}
