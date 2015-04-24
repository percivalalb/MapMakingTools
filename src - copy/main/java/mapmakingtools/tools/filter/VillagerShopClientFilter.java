package mapmakingtools.tools.filter;

import java.util.Arrays;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import mapmakingtools.tools.filter.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.tools.filter.packet.PacketVillagerShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class VillagerShopClientFilter extends IFilterClient {

	public static int[] recipeUses = new int[9];
	
	private GuiButton btn_ok;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_trade_1;
	private GuiButton btn_trade_2;
	private GuiButton btn_trade_3;
	private GuiButton btn_trade_4;
	private GuiButton btn_trade_5;
	private GuiButton btn_trade_6;
	private GuiButton btn_trade_7;
	private GuiButton btn_trade_8;
	private GuiButton btn_trade_9;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.villagershop.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:villagerShop";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(190);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 190) / 2;
	    this.btn_ok = new GuiButton(0, topX + 12, topY + 108, 20, 20, "OK");
	    this.btn_add = new GuiSmallButton(2, topX + 17 + 9 * 23, topY + 68, 13, 12, "+");
	    this.btn_remove = new GuiSmallButton(3, topX - 1 + 17 + 9 * 23, topY + 54, 13, 12, "-");
	    this.btn_trade_1 = new GuiSmallButton(4, topX - 1 + 1 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_2 = new GuiSmallButton(5, topX - 1 + 2 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_3 = new GuiSmallButton(6, topX - 1 + 3 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_4 = new GuiSmallButton(7, topX - 1 + 4 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_5 = new GuiSmallButton(8, topX - 1 + 5 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_6 = new GuiSmallButton(9, topX - 1 + 6 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_7 = new GuiSmallButton(10, topX - 1 + 7 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_8 = new GuiSmallButton(11, topX - 1 + 8 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_9 = new GuiSmallButton(12, topX - 1 + 9 * 23, topY + 88, 13, 12, "?");
	    gui.getButtonList().add(this.btn_ok);
	    gui.getButtonList().add(this.btn_add);
	    gui.getButtonList().add(this.btn_remove);
	    gui.getButtonList().add(this.btn_trade_1);
	    gui.getButtonList().add(this.btn_trade_2);
	    gui.getButtonList().add(this.btn_trade_3);
	    gui.getButtonList().add(this.btn_trade_4);
	    gui.getButtonList().add(this.btn_trade_5);
	    gui.getButtonList().add(this.btn_trade_6);
	    gui.getButtonList().add(this.btn_trade_7);
	    gui.getButtonList().add(this.btn_trade_8);
	    gui.getButtonList().add(this.btn_trade_9);
	    
	    Arrays.fill(recipeUses, 7);
	    
	    int recipeAmounts = ((VillagerShopServerFilter)FilterManager.getServerFilterFromClass(VillagerShopServerFilter.class)).getAmountRecipes(gui.getPlayer());
	    MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketVillagerRecipeAmounts(recipeAmounts));
		
	    
	    this.btn_add.xPosition = topX + 17 + recipeAmounts * 23;
	    this.btn_remove.xPosition = topX + 17 + recipeAmounts * 23;
	    
	    
	    this.btn_add.enabled = recipeAmounts < 9;
	    this.btn_remove.enabled = recipeAmounts > 1;
	    
	    this.btn_trade_1.enabled = recipeAmounts >= this.btn_trade_1.id - 3;
	    this.btn_trade_2.enabled = recipeAmounts >= this.btn_trade_2.id - 3;
	    this.btn_trade_3.enabled = recipeAmounts >= this.btn_trade_3.id - 3;
	    this.btn_trade_4.enabled = recipeAmounts >= this.btn_trade_4.id - 3;
	    this.btn_trade_5.enabled = recipeAmounts >= this.btn_trade_5.id - 3;
	    this.btn_trade_6.enabled = recipeAmounts >= this.btn_trade_6.id - 3;
	    this.btn_trade_7.enabled = recipeAmounts >= this.btn_trade_7.id - 3;
	    this.btn_trade_8.enabled = recipeAmounts >= this.btn_trade_8.id - 3;
	    this.btn_trade_9.enabled = recipeAmounts >= this.btn_trade_9.id - 3;
	    
	    Entity entity = gui.getWorld().getEntityByID(gui.getEntityId());
	    if(entity != null && entity instanceof EntityVillager) {
	    	EntityVillager villager = (EntityVillager)entity;
	    	MerchantRecipeList recipeList = villager.getRecipes(gui.getPlayer());
	    	if(recipeList == null)
	    		return;
	    	if(recipeList.size() >= 1)
	    		this.recipeUses[0] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(1 - 1), 4);
	    	if(recipeList.size() >= 2)
	    		this.recipeUses[1] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(2 - 1), 4);
	    	if(recipeList.size() >= 3)
	    		this.recipeUses[2] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(3 - 1), 4);
	    	if(recipeList.size() >= 4)
	    		this.recipeUses[3] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(4 - 1), 4);
	    	if(recipeList.size() >= 5)
	    		this.recipeUses[4] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(5 - 1), 4);
	    	if(recipeList.size() >= 6)
	    		this.recipeUses[5] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(6 - 1), 4);
	    	if(recipeList.size() >= 7)
	    		this.recipeUses[6] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(7 - 1), 4);
	    	if(recipeList.size() >= 8)
	    		this.recipeUses[7] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(8 - 1), 4);
	    	if(recipeList.size() >= 9)
	    		this.recipeUses[8] = ReflectionHelper.getField(MerchantRecipe.class, Integer.TYPE, (MerchantRecipe)recipeList.get(9 - 1), 4);
	    }
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.villagershop.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 190) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(IGuiFilter gui, int xMouse, int yMouse) {
		GL11.glTranslatef((float)-gui.getGuiLeft(), (float)-gui.getGuiTop(), 0.0F);
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt.id >= 4 && listBt.id <= 12) {
        		if(listBt.mousePressed(ClientHelper.mc, xMouse, yMouse)) {
        			List<String> list = Arrays.asList(EnumChatFormatting.BLUE + "Trade " + (listBt.id - 3), "Uses: " + this.recipeUses[listBt.id - 4], "Left Click = " + EnumChatFormatting.RED+ "-1", "Right Click = " + EnumChatFormatting.GREEN + "+1");
        			gui.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    	}
		GL11.glTranslatef((float)gui.getGuiLeft(), (float)gui.getGuiTop(), 0.0F);
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 190) / 2;
		
		int recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
		this.btn_add.xPosition = topX + 17 + recipeAmounts * 23;
		this.btn_remove.xPosition = topX + 17 + recipeAmounts * 23;
		  
		    
		this.btn_add.enabled = recipeAmounts < 9;
		this.btn_remove.enabled = recipeAmounts > 1;
		  
		this.btn_trade_1.enabled = recipeAmounts >= this.btn_trade_1.id - 3;
		this.btn_trade_2.enabled = recipeAmounts >= this.btn_trade_2.id - 3;
		this.btn_trade_3.enabled = recipeAmounts >= this.btn_trade_3.id - 3;
		this.btn_trade_4.enabled = recipeAmounts >= this.btn_trade_4.id - 3;
		this.btn_trade_5.enabled = recipeAmounts >= this.btn_trade_5.id - 3;
		this.btn_trade_6.enabled = recipeAmounts >= this.btn_trade_6.id - 3;
		this.btn_trade_7.enabled = recipeAmounts >= this.btn_trade_7.id - 3;
		this.btn_trade_8.enabled = recipeAmounts >= this.btn_trade_8.id - 3;
		this.btn_trade_9.enabled = recipeAmounts >= this.btn_trade_9.id - 3;
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.mousePressed(ClientHelper.mc, xMouse, yMouse)) {
                	//gui.selectedButton = guibutton;
                    if(guibutton.id >= 4 && guibutton.id <= 12) {
                    	this.recipeUses[guibutton.id - 4] = this.recipeUses[guibutton.id - 4] + 1;
      
                    	if(this.recipeUses[guibutton.id - 4] < 1)
                    		this.recipeUses[guibutton.id - 4] = 1;
                    }
                }
            }
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		
		if (button.enabled) {
			if(button.id >= 4 && button.id <= 12) {
            	this.recipeUses[button.id - 4] = this.recipeUses[button.id - 4] - 1;

            	if(this.recipeUses[button.id - 4] < 1) {
            		this.recipeUses[button.id - 4] = 1;
            	}
            }
			
        	int recipeAmounts = 0;
            switch (button.id) {
                case 0:
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketVillagerShop(gui.getEntityId(), this.recipeUses));
                    
                case 1:
                    ClientHelper.mc.setIngameFocus();
                    break;
                case 2:
                	recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).maxRecipesMap.put(gui.getPlayer().getCommandSenderName(), recipeAmounts + 1);
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).addOnlySlots(gui.getFilterContainer());
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketVillagerRecipeAmounts(recipeAmounts + 1));
                    break;
                case 3:
                	recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).maxRecipesMap.put(gui.getPlayer().getCommandSenderName(), recipeAmounts - 1);
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).addOnlySlots(gui.getFilterContainer());
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketVillagerRecipeAmounts(recipeAmounts - 1));
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenVillagerShop);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 190) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 190);
	    for(int i = 0; i < ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer()) && i < 9; ++i)
	    	gui.drawTexturedModalRectangle(topX + 19 + (i * 18) + (i * 5), topY + 23, 0, 190, 18, 58);
		return true;
	}
}
