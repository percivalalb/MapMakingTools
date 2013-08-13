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
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;

/**
 * @author ProPercivalalb
 */
public class FilterCreeperExplosion implements IFilter {
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.creeperExplosion");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:explosion");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String id = SpawnerHelper.getMobId(tile);
			if(id.equals("Creeper")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiTextField txt_radius;
    private GuiTextField txt_fuse;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 66, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, k + 40, l + 66, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_radius = new GuiTextField(gui.getFont(), k + 120, l + 37, 90, 20);
        this.txt_radius.setMaxStringLength(7);
        this.txt_fuse = new GuiTextField(gui.getFont(), k + 20, l + 37, 90, 20);
        this.txt_fuse.setMaxStringLength(7);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			this.txt_radius.setText("" + SpawnerHelper.getCreeperExplosionRadius(tile));
			this.txt_fuse.setText("" + SpawnerHelper.getCreeperFuse(tile));
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - gui.ySize()) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        gui.getFont().drawString("Fuse Time", k + 20, l + 25, 4210752);
        gui.getFont().drawString("Explosion Radius", k + 120, l + 25, 4210752);
        this.txt_fuse.drawTextBox();
        this.txt_radius.drawTextBox();
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        this.txt_fuse.updateCursorCounter();
        this.txt_radius.updateCursorCounter();
        boolean validFuse = false;
        boolean validExplosion = false;
		try {
			new Integer(txt_fuse.getText());
			validFuse = true;
		}
		catch(Exception e) {}
		try {
			new Integer(txt_radius.getText());
			validExplosion = true;
		}
		catch(Exception e) {}
        btn_ok.enabled = validFuse && validExplosion;
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 this.txt_fuse.mouseClicked(var1, var2, var3);
		 this.txt_radius.mouseClicked(var1, var2, var3);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        this.txt_fuse.textboxKeyTyped(var1, var2);
        this.txt_radius.textboxKeyTyped(var1, var2);
        
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
            gui.actionPerformed(btn_cancel);
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled)
        {
            switch (var1.id)
            {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketCreeperProperties(gui.x, gui.y, gui.z, txt_fuse.getText(), txt_radius.getText()));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
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
