package mapmakingtools.tools.filter.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public class PacketVillagerShop extends IPacket {

	public int entityId;
	public int[] recipeUses;
	
	public PacketVillagerShop() {}
	public PacketVillagerShop(int entityId, int[] recipeUses) {
		this.entityId = entityId;
		this.recipeUses = recipeUses;
	}

	@Override
	public void read(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		this.recipeUses = new int[9];
		for(int i = 0; i < this.recipeUses.length; i++)
			this.recipeUses[i] = data.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.entityId);
		for(int i = 0; i < this.recipeUses.length; i++)
			dos.writeInt(this.recipeUses[i]);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof IContainerFilter) {
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
				if(entityId == 0)
					return;
		        World world = player.worldObj;
		        Entity entity = world.getEntityByID(entityId);
		        if(entity != null && entity instanceof EntityVillager) {
		        	EntityVillager villager = (EntityVillager)entity;
		        	MerchantRecipeList recipeList = new MerchantRecipeList();
		        	
		        	for(int i = 0; i < shop.getAmountRecipes(container.getPlayer()); ++i) {
		        		ItemStack input1 = shop.getInventory(container).getStackInSlot(i * 3);
		        		ItemStack input2 = shop.getInventory(container).getStackInSlot(i * 3 + 1);
		        		ItemStack output = shop.getInventory(container).getStackInSlot(i * 3 + 2);
		        		if(input1 == null && input2 != null) {
		        			input1 = input2.copy();
		        			input2 = null;
		        		}
		        		if(input1 == null) {
		        			//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.villagerShop.inputNull", new Object[0]));
		        			return;
		        		}
		        		if(output == null) {
		        			//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.villagerShop.outputNull", new Object[0]));
		        			return;
		        		}
		        		MerchantRecipe recipe = new MerchantRecipe(input1, input2, output);
		        		ReflectionHelper.setField(MerchantRecipe.class, recipe, 4, this.recipeUses[i]);
		        		recipeList.add(recipe);
		        	}
		        	ReflectionHelper.setField(EntityVillager.class, villager, 5, recipeList);
		        	
		        	ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.villagershop.complete");
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
		        }
		    }
		}
	}

}
