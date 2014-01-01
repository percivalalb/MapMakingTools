package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.filters.server.FilterServerVillagerShop;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketVillagerRecipeAmounts extends PacketMMT {

	public int recipeAmounts;
	
	public PacketVillagerRecipeAmounts() {
		super(PacketTypeHandler.VILLAGER_RECIPE_AMOUNR, false);
	}
	
	public PacketVillagerRecipeAmounts(int recipeAmounts) {
		this();
		this.recipeAmounts = recipeAmounts;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.recipeAmounts = data.readInt();
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(recipeAmounts);
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			if(playerMP.openContainer instanceof ContainerFilter) {
				ContainerFilter container = (ContainerFilter)playerMP.openContainer;
				if(container.current != null && container.current instanceof FilterServerVillagerShop) {
					FilterServerVillagerShop shop = (FilterServerVillagerShop)container.current;
					shop.maxRecipesMap.put(PlayerHelper.usernameLowerCase(player), recipeAmounts);
					shop.addOnlySlots(container);
				}
			}
		}
	}

}
