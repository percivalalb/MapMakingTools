package mapmakingtools.filters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketMobVelocity;
import mapmakingtools.network.packet.PacketSpawnerTimings;

/**
 * @author ProPercivalalb
 */
public class FilterMobVelocity implements IFilter {

	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.mobVelocity");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:velocity");
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

    private GuiTextField txt_xMotion;
    private GuiTextField txt_yMotion;
    private GuiTextField txt_zMotion;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(135);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 135) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 101, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, k + 40, l + 101, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_xMotion = new GuiTextField(gui.getFont(), k + 20, l + 37, 90, 20);
        this.txt_xMotion.setMaxStringLength(7);
        this.txt_yMotion = new GuiTextField(gui.getFont(), k + 120, l + 37, 90, 20);
        this.txt_yMotion.setMaxStringLength(7);
        this.txt_zMotion = new GuiTextField(gui.getFont(), k + 60, l + 72, 90, 20);
        this.txt_zMotion.setMaxStringLength(7);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String xM = "" + SpawnerHelper.getMotionX(tile);
			String yM = "" + SpawnerHelper.getMotionY(tile);
			String zM = "" + SpawnerHelper.getMotionZ(tile);
			if(xM.endsWith(".0")) xM.replace(".0", "");
			if(yM.endsWith(".0")) yM.replace(".0", "");
			if(zM.endsWith(".0")) zM.replace(".0", "");
			this.txt_xMotion.setText(xM);
			this.txt_yMotion.setText(yM);
			this.txt_zMotion.setText(zM);
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 135) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        gui.getFont().drawString("X Motion (East)", k + 20, l + 27, 4210752);
        gui.getFont().drawString("Y Motion (Up)", k + 120, l + 27, 4210752);
        gui.getFont().drawString("Z Motion (South)", k + 55, l + 62, 4210752);
        this.txt_xMotion.drawTextBox();
        this.txt_yMotion.drawTextBox();
        this.txt_zMotion.drawTextBox();
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        this.txt_xMotion.updateCursorCounter();
        this.txt_yMotion.updateCursorCounter();
        this.txt_zMotion.updateCursorCounter();
        
        btn_ok.enabled = FilterHelper.isDouble(txt_xMotion.getText()) && FilterHelper.isDouble(txt_yMotion.getText()) && FilterHelper.isDouble(txt_zMotion.getText());
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 this.txt_xMotion.mouseClicked(var1, var2, var3);
		 this.txt_yMotion.mouseClicked(var1, var2, var3);
		 this.txt_zMotion.mouseClicked(var1, var2, var3);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        this.txt_xMotion.textboxKeyTyped(var1, var2);
        this.txt_yMotion.textboxKeyTyped(var1, var2);
        this.txt_zMotion.textboxKeyTyped(var1, var2);
        
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
            gui.actionPerformed(btn_cancel);
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketMobVelocity(gui.x, gui.y, gui.z, txt_xMotion.getText(), txt_yMotion.getText(), txt_zMotion.getText()));
                    
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
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenMedium);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 135) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 135);
		return true;
	}
}
