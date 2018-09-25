package mapmakingtools.tools.filter;

import java.util.Arrays;
import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.tools.filter.packet.PacketVillagerShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * @author ProPercivalalb
 */
public class VillagerShopClientFilter extends FilterClient {

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
		return "mapmakingtools:textures/filter/villager_shop.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(190);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
	    int topY = gui.getGuiY();
	    this.btn_ok = new GuiButton(0, topX + 12, topY + 108, 20, 20, "OK");
	    this.btn_add = new GuiButtonSmall(2, topX + 224, topY + 68, 13, 12, "+");
	    this.btn_remove = new GuiButtonSmall(3, topX + 224, topY + 54, 13, 12, "-");
	    this.btn_trade_1 = new GuiButtonSmall(4, topX - 1 + 1 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_2 = new GuiButtonSmall(5, topX - 1 + 2 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_3 = new GuiButtonSmall(6, topX - 1 + 3 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_4 = new GuiButtonSmall(7, topX - 1 + 4 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_5 = new GuiButtonSmall(8, topX - 1 + 5 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_6 = new GuiButtonSmall(9, topX - 1 + 6 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_7 = new GuiButtonSmall(10, topX - 1 + 7 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_8 = new GuiButtonSmall(11, topX - 1 + 8 * 23, topY + 88, 13, 12, "?");
	    this.btn_trade_9 = new GuiButtonSmall(12, topX - 1 + 9 * 23, topY + 88, 13, 12, "?");
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
	    PacketDispatcher.sendToServer(new PacketVillagerRecipeAmounts(recipeAmounts));
	    
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
	    
	    Entity entity = gui.getEntity();
	    if(entity instanceof EntityVillager) {
	    	EntityVillager villager = (EntityVillager)entity;
	    	MerchantRecipeList recipeList = villager.getRecipes(gui.getPlayer());
	    	
	    	for(int i = 0; i < recipeList.size(); ++i)
	    		this.recipeUses[i] = ((MerchantRecipe)recipeList.get(i)).getMaxTradeUses();
	    }
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.villagershop.info"));
	}

	@Override
	public void drawGuiContainerForegroundLayer(IFilterGui gui, int xMouse, int yMouse) {
		GlStateManager.translate((float)-gui.getGuiX(), (float)-gui.getGuiY(), 0.0F);
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt.id >= 4 && listBt.id <= 12) {
        		if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        			List<String> list = Arrays.asList(TextFormatting.BLUE + "Trade " + (listBt.id - 3), "Uses: " + this.recipeUses[listBt.id - 4], "Left Click = " + TextFormatting.RED+ "-1", "Right Click = " + TextFormatting.GREEN + "+1");
        			gui.drawHoveringTooltip(list, xMouse, yMouse);
        		}
    		}
    	}
		GlStateManager.translate((float)gui.getGuiX(), (float)gui.getGuiY(), 0.0F);
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		
		int recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
		    
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
	public void mouseClicked(IFilterGui gui, int xMouse, int yMouse, int mouseButton) {
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
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
	public void actionPerformed(IFilterGui gui, GuiButton button) {
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
                	PacketDispatcher.sendToServer(new PacketVillagerShop(gui.getEntityId(), this.recipeUses));
                    
                case 1:
                    ClientHelper.getClient().player.closeScreen();
                    break;
                case 2:
                	recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).maxRecipesMap.put(gui.getPlayer().getUniqueID(), recipeAmounts + 1);
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).addOnlySlots(gui.getFilterContainer());
                	PacketDispatcher.sendToServer(new PacketVillagerRecipeAmounts(recipeAmounts + 1));
                    break;
                case 3:
                	recipeAmounts = ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer());
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).maxRecipesMap.put(gui.getPlayer().getUniqueID(), recipeAmounts - 1);
                	((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).addOnlySlots(gui.getFilterContainer());
                	PacketDispatcher.sendToServer(new PacketVillagerRecipeAmounts(recipeAmounts - 1));
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_VILLAGER_SHOP);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getScreenHeight() - 190) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 190);
	    for(int i = 0; i < ((VillagerShopServerFilter)gui.getFilterContainer().getCurrentFilter()).getAmountRecipes(gui.getPlayer()) && i < 9; ++i)
	    	gui.drawTexturedModalRectangle(topX + 19 + (i * 18) + (i * 5), topY + 23, 0, 190, 18, 58);
		return true;
	}
}
