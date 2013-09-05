package mapmakingtools.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.api.ScrollMenu;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketMobType;

/**
 * @author ProPercivalalb
 */
public class FilterMobType implements IFilter {

    private List<String> mobTypes = new ArrayList<String>();
    public ScrollMenu menu;
    
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.mobSpawnerType");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:mobType");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(135);
		this.mobTypes.clear();
        MobSpawnerType.addToList(mobTypes);
        Collections.sort(mobTypes);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 135) / 2;
        if(menu == null)
			menu = new ScrollMenu(gui, 31, 14, 208, 108, 2, mobTypes) {
				@Override
				public void onSetButton() {
					if(screen instanceof GuiFilterMenu) {
						GuiFilterMenu filter = (GuiFilterMenu)screen;
						PacketTypeHandler.populatePacketAndSendToServer(new PacketMobType(filter.x, filter.y, filter.z, mobTypes.get(selected)));
						ClientHelper.mc.displayGuiScreen(null);
						ClientHelper.mc.setIngameFocus();
					}
				}
	
				@Override
				public String getDisplayString(String listStr) {
		            String unlocalised = String.format("entity.%s.name", listStr);
		            if(!unlocalised.equalsIgnoreCase(listStr))
		            	return listStr;
		            return StatCollector.translateToLocal(unlocalised);
				}
			};
		else 
			menu = this.copy(gui);
		this.menu.initGui();
    	TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			for(int count = 0; count < mobTypes.size(); ++count) {
				if(mobTypes.get(count).equals(SpawnerHelper.getMobId(tile))) {
					menu.setSelected(count);
					break;
				}
			}
		}
	}
	
	public ScrollMenu copy(GuiContainer guiContainer) {
		ScrollMenu copy = new ScrollMenu(guiContainer, menu.xOffset, menu.yOffset, menu.screenSizeX, menu.screenSizeY, menu.numberOfColumns, menu.list) {
			@Override
			public void onSetButton() {
				if(screen instanceof GuiFilterMenu) {
					GuiFilterMenu filter = (GuiFilterMenu)screen;
					PacketTypeHandler.populatePacketAndSendToServer(new PacketMobType(filter.x, filter.y, filter.z, mobTypes.get(selected)));
					ClientHelper.mc.displayGuiScreen(null);
					ClientHelper.mc.setIngameFocus();
				}
			}

			@Override
			public String getDisplayString(String listStr) {
	            String unlocalised = String.format("entity.%s.name", listStr);
	            if(!unlocalised.equalsIgnoreCase(listStr))
	            	return listStr;
	            return StatCollector.translateToLocal(unlocalised);
			}
		};
		copy.setSelected(menu.selected);
		copy.scrollY = menu.scrollY;
		
		return copy;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float particleTicks, int xMouse, int yMouse) {
	    int width = (gui.width - gui.xSize()) / 2;
	    int height = (gui.height - 135) / 2;
		this.menu.drawGuiContainerBackgroundLayer(xMouse, yMouse, particleTicks);
        gui.getFont().drawString(getFilterName(), width + 6, height + 6, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
       
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int xMouse, int yMouse, int mouseButton) {
	    int width = (gui.width - gui.xSize()) / 2;
	    int height = (gui.height - 135) / 2;
		this.menu.mouseClicked(xMouse, yMouse, mouseButton);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {

        if (var2 == Keyboard.KEY_RETURN) {
           // gui.actionPerformed(btn_covert);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenScroll);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 135) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 135);
		return true;
	}
}
