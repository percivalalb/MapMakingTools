package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import mapmakingtools.client.gui.GuiSpawnerSettings;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.GeneralHelper;
import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.filters.server.FilterServerMobArmor;
import mapmakingtools.filters.server.FilterServerVillagerShop;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;

public class PacketVillagerShop extends PacketMMT {

	public int entityId;
	public int[] recipeUses;
	
	public PacketVillagerShop() {
		super(PacketTypeHandler.VILLAGER_SHOP, false);
		this.recipeUses = new int[9];
	}
	
	public PacketVillagerShop(int entityId, int[] recipeUses) {
		this();
		this.entityId = entityId;
		this.recipeUses = recipeUses;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		for(int i = 0; i < recipeUses.length; i++) {
			this.recipeUses[i] = data.readInt();
		}
	}

	@Override
	public void writeData(DataOutputStream dos) throws IOException {
		dos.writeInt(entityId);
		for(int i = 0; i < recipeUses.length; i++) {
			dos.writeInt(this.recipeUses[i]);
		}
	}

	@Override
	public void execute(INetworkManager network, EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			if(playerMP.openContainer != null && playerMP.openContainer instanceof ContainerFilter) {
				ContainerFilter container = (ContainerFilter)playerMP.openContainer;
				if(container.current != null && container.current instanceof FilterServerVillagerShop) {
					FilterServerVillagerShop shop = (FilterServerVillagerShop)container.current;
					if(entityId != 0) {
			        	World world = player.worldObj;
			        	Entity entity = world.getEntityByID(entityId);
			        	if(entity != null && entity instanceof EntityVillager) {
			        		EntityVillager villager = (EntityVillager)entity;
			        		MerchantRecipeList recipeList = new MerchantRecipeList();
			        		
			        		for (int i = 0; i < shop.getAmountRecipes(container.player); ++i) {
			        			ItemStack input1 = container.getSlot(i * 3).getStack();
			        			ItemStack input2 = container.getSlot(i * 3 + 1).getStack();
			        			ItemStack output = container.getSlot(i * 3 + 2).getStack();
			        			if(input1 == null && input2 != null) {
			        				input1 = input2.copy();
			        				input2 = null;
			        			}
			        			if(input1 == null) {
			        				player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.villagerShop.inputNull", new Object[0]));
			        				return;
			        			}
			        			if(output == null) {
			        				player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.villagerShop.outputNull", new Object[0]));
			        				return;
			        			}
			        			MerchantRecipe recipe = new MerchantRecipe(input1, input2, output);
			        			ReflectionHelper.setField(MerchantRecipe.class, recipe, 4, this.recipeUses[i]);
			        			recipeList.add(recipe);
			        		}
			        		ReflectionHelper.setField(EntityVillager.class, villager, 5, recipeList);
			        		
			        		player.sendChatToPlayer(ChatMessageComponent.func_111082_b("filter.villagerShop.complete", new Object[0]));
			        	}
			        }
				}
			}
		}
	}

}
