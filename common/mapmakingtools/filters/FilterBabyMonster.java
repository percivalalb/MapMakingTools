package mapmakingtools.filters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiButton;
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
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketBabyMonster;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;

/**
 * @author ProPercivalalb
 */
public class FilterBabyMonster implements IFilter {
	
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.babyMonster");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:babyMonster");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String id = SpawnerHelper.getMobId(tile);
			if(id.equals("Zombie") || id.equals("PigZombie")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiButton btn_covert;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btn_covert = new GuiButton(0, k + 20, l + 37, 200, 20, "Turn into Baby");
        gui.getButtonList().add(this.btn_covert);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			boolean isChild = SpawnerHelper.getBabyMonster(tile);
			if(isChild) {
				this.btn_covert.displayString = "Turn into Adult";
			}
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - gui.ySize()) / 2;
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

        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_covert);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketBabyMonster(gui.x, gui.y, gui.z));
                	ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
            }
        }
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		return false;
	}

	@Override
	public boolean isMouseOverSlot(GuiFilterMenu gui, Slot slot, int mouseX,
			int mouseY) {
		// TODO Auto-generated method stub
		return false;
	}
}
