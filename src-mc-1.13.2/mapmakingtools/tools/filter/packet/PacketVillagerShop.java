package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketVillagerShop {

	public int entityId;
	public int[] recipeUses;
	
	public PacketVillagerShop(int entityId, int[] recipeUses) {
		this.entityId = entityId;
		this.recipeUses = recipeUses;
	}
	
	public static void encode(PacketVillagerShop msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
		for(int i = 0; i < msg.recipeUses.length; i++)
			buf.writeInt(msg.recipeUses[i]);
	}
	
	public static PacketVillagerShop decode(PacketBuffer buf) {
		int entityId = buf.readInt();
		int[] recipeUses = new int[9];
		for(int i = 0; i < recipeUses.length; i++)
			recipeUses[i] = buf.readInt();
		return new PacketVillagerShop(entityId, recipeUses);
	}
	
	public static class Handler {
        public static void handle(final PacketVillagerShop msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		MapMakingTools.LOGGER.info("dawe " + msg.entityId);
        		if(player.openContainer instanceof IFilterContainer) {
        			MapMakingTools.LOGGER.info("filter " + msg.entityId);
        			IFilterContainer container = (IFilterContainer)player.openContainer;
        			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
        				MapMakingTools.LOGGER.info("shop " + msg.entityId);
        				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
        		        World world = player.world;
        		        Entity entity = world.getEntityByID(msg.entityId);
        		        if(entity instanceof EntityVillager) {
        		        	MapMakingTools.LOGGER.info("villager " + msg.entityId);
        		        	EntityVillager villager = (EntityVillager)entity;
        		        	MerchantRecipeList recipeList = new MerchantRecipeList();
        		        	
        		        	for(int i = 0; i < shop.getAmountRecipes(container.getPlayer()); ++i) {
        			        	MapMakingTools.LOGGER.info("amount " + i);
        		        		ItemStack input1 = shop.getInventory(container).getStackInSlot(i * 3);
        		        		ItemStack input2 = shop.getInventory(container).getStackInSlot(i * 3 + 1);
        		        		ItemStack output = shop.getInventory(container).getStackInSlot(i * 3 + 2);
        		        		if(input1 != null)
        		        			MapMakingTools.LOGGER.info("input1 " + input1.toString());
        		        		if(input2 != null)
        		        			MapMakingTools.LOGGER.info("input2 " + input2.toString());
        		        		if(output != null)
        		        			MapMakingTools.LOGGER.info("output " + output.toString());
        		        		
        		        		if(input1 == null && input2 != null) {
        		        			input1 = input2.copy();
        		        			input2 = null;
        		        		}
        		        		if(input1 == null) {
        		        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.inputnull");
        							chatComponent.getStyle().setItalic(true);
        							chatComponent.getStyle().setColor(TextFormatting.RED);
        							player.sendMessage(chatComponent);
        		        			return;
        		        		}
        		        		if(output == null) {
        		        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.outputnull");
        							chatComponent.getStyle().setItalic(true);
        							chatComponent.getStyle().setColor(TextFormatting.RED);
        							player.sendMessage(chatComponent);
        		        			return;
        		        		}
        		        		MerchantRecipe recipe = new MerchantRecipe(input1, input2, output, 0, msg.recipeUses[i]);
        		        		recipeList.add(recipe);
        		        	}
        		        	ReflectionHelper.setField(EntityVillager.class, villager, 7, recipeList);
        		        	
        		        	TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.complete");
        					chatComponent.getStyle().setItalic(true);
        					player.sendMessage(chatComponent);
        		        }
        		    }
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
