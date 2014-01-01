package mapmakingtools.filters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.FilterHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketItemSpawner;

/**
 * @author ProPercivalalb
 */
public class FilterItemSpawner implements IFilter {
	
	@Override
	public Icon getDisplayIcon() {
		return Item.beefCooked.getIconFromDamage(0);
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.changeItem");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String id = SpawnerHelper.getMobId(tile);
			if(id.equals("Item")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}
    
	public GuiButton btn_ok;
	
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(104);
		int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 104) / 2;
        this.btn_ok = new GuiButton(0, k + 20, l + 61, 20, 20, "OK");
        gui.getButtonList().add(this.btn_ok);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 104) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
		
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {

	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        if (var2 == Keyboard.KEY_ESCAPE) {
        	ClientHelper.mc.displayGuiScreen(null);
            ClientHelper.mc.setIngameFocus();
        }
        
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketItemSpawner(gui.x, gui.y, gui.z));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenOneSlot);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 104) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 104);
		return true;
	}
}
