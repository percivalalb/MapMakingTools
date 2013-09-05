package mapmakingtools.filters;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonMerchant;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiButtonTextColour;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.client.gui.GuiButtonTextColour.TextColour;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.FilterHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.filters.server.FilterServerVillagerShop;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.SlotFake;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketMobPosition;
import mapmakingtools.network.packet.PacketMobVelocity;
import mapmakingtools.network.packet.PacketSpawnerTimings;
import mapmakingtools.network.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.network.packet.PacketVillagerShop;

/**
 * @author ProPercivalalb
 */
public class FilterVillagerShop implements IFilter {
	
	public static Icon icon;
	private Minecraft mc = Minecraft.getMinecraft();
	public static int[] recipeUses = new int[9];
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.villagerShop");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:villagerShop");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		if(entity != null && entity instanceof EntityVillager) {
			return true;
		}
		return false;
	}

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
    
    //TODO add number of times the trade can be used!!!
    //TODO add number of times the trade can be used!!!
    //TODO add number of times the trade can be used!!!
    //TODO add number of times the trade can be used!!!	
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(190);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 190) / 2;
        this.btn_ok = new GuiButton(0, k + 12, l + 108, 20, 20, "OK");
        this.btn_add = new GuiSmallButton(2, k + 17 + 9 * 23, l + 68, 13, 12, "+");
        this.btn_remove = new GuiSmallButton(3, k - 1 + 17 + 9 * 23, l + 54, 13, 12, "-");
        this.btn_trade_1 = new GuiSmallButton(4, k - 1 + 1 * 23, l + 88, 13, 12, "?");
        this.btn_trade_2 = new GuiSmallButton(5, k - 1 + 2 * 23, l + 88, 13, 12, "?");
        this.btn_trade_3 = new GuiSmallButton(6, k - 1 + 3 * 23, l + 88, 13, 12, "?");
        this.btn_trade_4 = new GuiSmallButton(7, k - 1 + 4 * 23, l + 88, 13, 12, "?");
        this.btn_trade_5 = new GuiSmallButton(8, k - 1 + 5 * 23, l + 88, 13, 12, "?");
        this.btn_trade_6 = new GuiSmallButton(9, k - 1 + 6 * 23, l + 88, 13, 12, "?");
        this.btn_trade_7 = new GuiSmallButton(10, k - 1 + 7 * 23, l + 88, 13, 12, "?");
        this.btn_trade_8 = new GuiSmallButton(11, k - 1 + 8 * 23, l + 88, 13, 12, "?");
        this.btn_trade_9 = new GuiSmallButton(12, k - 1 + 9 * 23, l + 88, 13, 12, "?");
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
    	int recipeAmounts = ((FilterServerVillagerShop)gui.getContainer().current).getAmountRecipes(gui.entityPlayer);
    	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerRecipeAmounts(recipeAmounts));
		this.btn_add.xPosition = k + 17 + recipeAmounts * 23;
    	this.btn_remove.xPosition = k + 17 + recipeAmounts * 23;
    	if(recipeAmounts >= 9) {
    		this.btn_add.enabled = false;
    	}
    	else {
    		this.btn_add.enabled = true;
    	}
    	
    	if(recipeAmounts <= 1) {
    		this.btn_remove.enabled = false;
    	}
    	else {
    		this.btn_remove.enabled = true;
    	}
    	
    	this.btn_trade_1.enabled = recipeAmounts >= this.btn_trade_1.id - 3;
    	this.btn_trade_2.enabled = recipeAmounts >= this.btn_trade_2.id - 3;
    	this.btn_trade_3.enabled = recipeAmounts >= this.btn_trade_3.id - 3;
    	this.btn_trade_4.enabled = recipeAmounts >= this.btn_trade_4.id - 3;
    	this.btn_trade_5.enabled = recipeAmounts >= this.btn_trade_5.id - 3;
    	this.btn_trade_6.enabled = recipeAmounts >= this.btn_trade_6.id - 3;
    	this.btn_trade_7.enabled = recipeAmounts >= this.btn_trade_7.id - 3;
    	this.btn_trade_8.enabled = recipeAmounts >= this.btn_trade_8.id - 3;
    	this.btn_trade_9.enabled = recipeAmounts >= this.btn_trade_9.id - 3;
    	
    	Entity entity = gui.entityPlayer.worldObj.getEntityByID(gui.entityId);
    	if(entity != null && entity instanceof EntityVillager) {
    		EntityVillager villager = (EntityVillager)entity;
    		MerchantRecipeList recipeList = villager.getRecipes(gui.entityPlayer);
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
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 190) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int xMouse, int yMouse) {
		GL11.glTranslatef((float)-gui.guiLeft(), (float)-gui.guiTop(), 0.0F);
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt.id >= 4 && listBt.id <= 12) {
        		if(listBt.mousePressed(mc, xMouse, yMouse)) {
        			List<String> list = Arrays.asList(EnumChatFormatting.BLUE + "Trade " + (listBt.id - 3), "Uses: " + this.recipeUses[listBt.id - 4], "Left Click = " + EnumChatFormatting.RED+ "-1", "Right Click = " + EnumChatFormatting.GREEN + "+1");
        			gui.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    	}
		GL11.glTranslatef((float)gui.guiLeft(), (float)gui.guiTop(), 0.0F);
	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 190) / 2;
		int recipeAmounts = ((FilterServerVillagerShop)gui.getContainer().current).getAmountRecipes(gui.entityPlayer);
		this.btn_add.xPosition = k + 17 + recipeAmounts * 23;
    	this.btn_remove.xPosition = k + 17 + recipeAmounts * 23;
    	if(recipeAmounts >= 9) {
    		this.btn_add.enabled = false;
    	}
    	else {
    		this.btn_add.enabled = true;
    	}
    	
    	if(recipeAmounts <= 1) {
    		this.btn_remove.enabled = false;
    	}
    	else {
    		this.btn_remove.enabled = true;
    	}
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
	public void mouseClicked(GuiFilterMenu gui, int xMouse, int yMouse, int mouseButton) {
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.mousePressed(this.mc, xMouse, yMouse)) {
                	//gui.selectedButton = guibutton;
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    if(guibutton.id >= 4 && guibutton.id <= 12) {
                    	this.recipeUses[guibutton.id - 4] = this.recipeUses[guibutton.id - 4] + 1;
      
                    	if(this.recipeUses[guibutton.id - 4] < 1) {
                    		this.recipeUses[guibutton.id - 4] = 1;
                    	}
                    }
                }
            }
		}
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	ClientHelper.mc.displayGuiScreen(null);
            ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton guibutton) {
		if (guibutton.enabled) {
			if(guibutton.id >= 4 && guibutton.id <= 12) {
            	this.recipeUses[guibutton.id - 4] = this.recipeUses[guibutton.id - 4] - 1;

            	if(this.recipeUses[guibutton.id - 4] < 1) {
            		this.recipeUses[guibutton.id - 4] = 1;
            	}
            }
			
        	int recipeAmounts = 0;
            switch (guibutton.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerShop(gui.entityId, this.recipeUses));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
                case 2:
                	recipeAmounts = ((FilterServerVillagerShop)gui.getContainer().current).getAmountRecipes(gui.entityPlayer);
                	((FilterServerVillagerShop)gui.getContainer().current).maxRecipesMap.put(PlayerHelper.usernameLowerCase(gui.entityPlayer), recipeAmounts + 1);
                	((FilterServerVillagerShop)gui.getContainer().current).addOnlySlots(((ContainerFilter)gui.inventorySlots));
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerRecipeAmounts(recipeAmounts + 1));
                    break;
                case 3:
                	recipeAmounts = ((FilterServerVillagerShop)gui.getContainer().current).getAmountRecipes(gui.entityPlayer);
                	((FilterServerVillagerShop)gui.getContainer().current).maxRecipesMap.put(PlayerHelper.usernameLowerCase(gui.entityPlayer), recipeAmounts - 1);
                	((FilterServerVillagerShop)gui.getContainer().current).addOnlySlots(((ContainerFilter)gui.inventorySlots));
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerRecipeAmounts(recipeAmounts - 1));
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenVillagerShop);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 190) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 190);
		int i;
	    for (i = 0; i < ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(((ContainerFilter)gui.inventorySlots).player) && i < 9; ++i) {
	    	gui.drawTexturedModalRect(k + 19 + (i * 18) + (i * 5), l + 23, 0, 190, 18, 58);
	    }
		return true;
	}
	
	static {
		Arrays.fill(recipeUses, 7);
	}
}
