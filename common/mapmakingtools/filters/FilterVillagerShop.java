package mapmakingtools.filters;

import java.util.Hashtable;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.ReflectionHelper;
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
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiSmallButton;
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
        this.btn_remove = new GuiSmallButton(3, k + 17 + 9 * 23, l + 54, 13, 12, "-");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_add);
        gui.getButtonList().add(this.btn_remove);
    	int recipeAmounts = ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(gui.entityPlayer);
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
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 190) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 190) / 2;
		int recipeAmounts = ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(gui.entityPlayer);
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
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 
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
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
        	int recipeAmounts = 0;
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerShop(gui.entityId));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
                case 2:
                	recipeAmounts = ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(gui.entityPlayer);
                	((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).maxRecipesMap.put(PlayerHelper.usernameLowerCase(gui.entityPlayer), recipeAmounts + 1);
                	((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).addOnlySlots(((ContainerFilter)gui.inventorySlots));
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerRecipeAmounts(recipeAmounts + 1));
                    break;
                case 3:
                	recipeAmounts = ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(gui.entityPlayer);
                	((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).maxRecipesMap.put(PlayerHelper.usernameLowerCase(gui.entityPlayer), recipeAmounts - 1);
                	((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).addOnlySlots(((ContainerFilter)gui.inventorySlots));
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketVillagerRecipeAmounts(recipeAmounts - 1));
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.func_110434_K().func_110577_a(ResourceReference.screenVillagerShop);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 190) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 190);
		int i;
	    for (i = 0; i < ((FilterServerVillagerShop)((ContainerFilter)gui.inventorySlots).current).getAmountRecipes(((ContainerFilter)gui.inventorySlots).player) && i < 9; ++i) {
	    	gui.drawTexturedModalRect(k + 19 + (i * 18) + (i * 5), l + 23, 0, 190, 18, 58);
	    }
		
		
		return true;
	}
}
