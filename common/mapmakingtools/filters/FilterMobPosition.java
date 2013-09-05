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
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketMobPosition;
import mapmakingtools.network.packet.PacketMobVelocity;
import mapmakingtools.network.packet.PacketSpawnerTimings;

/**
 * @author ProPercivalalb
 */
public class FilterMobPosition implements IFilter {
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.position");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:location");
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

    private GuiTextField txt_xPosition;
    private GuiTextField txt_yPosition;
    private GuiTextField txt_zPosition;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private GuiButton btn_type = new GuiButton(2, 0, 0, 70, 20, "Relative");
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(135);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 135) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 101, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, k + 40, l + 101, 60, 20, "Cancel");
        btn_type.xPosition = k + 130;
        btn_type.yPosition = l + 72;
        //this.btn_type = new GuiButton(2, k + 130, l + 101, 70, 20, "Relative");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getButtonList().add(this.btn_type);
        this.txt_xPosition = new GuiTextField(gui.getFont(), k + 20, l + 37, 90, 20);
        this.txt_xPosition.setMaxStringLength(7);
        this.txt_yPosition = new GuiTextField(gui.getFont(), k + 120, l + 37, 90, 20);
        this.txt_yPosition.setMaxStringLength(7);
        this.txt_zPosition = new GuiTextField(gui.getFont(), k + 20, l + 72, 90, 20);
        this.txt_zPosition.setMaxStringLength(7);
        this.redoTextOnBoxs(gui);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 135) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        gui.getFont().drawString("X Corridate", k + 20, l + 27, 4210752);
        gui.getFont().drawString("Y Corridate", k + 120, l + 27, 4210752);
        gui.getFont().drawString("Z Corridate", k + 20, l + 62, 4210752);
        this.txt_xPosition.drawTextBox();
        this.txt_yPosition.drawTextBox();
        this.txt_zPosition.drawTextBox();
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        this.txt_xPosition.updateCursorCounter();
        this.txt_yPosition.updateCursorCounter();
        this.txt_zPosition.updateCursorCounter();
        
        btn_ok.enabled = FilterHelper.isDouble(txt_xPosition.getText()) && FilterHelper.isDouble(txt_yPosition.getText()) && FilterHelper.isDouble(txt_zPosition.getText());
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 this.txt_xPosition.mouseClicked(var1, var2, var3);
		 this.txt_yPosition.mouseClicked(var1, var2, var3);
		 this.txt_zPosition.mouseClicked(var1, var2, var3);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        this.txt_xPosition.textboxKeyTyped(var1, var2);
        this.txt_yPosition.textboxKeyTyped(var1, var2);
        this.txt_zPosition.textboxKeyTyped(var1, var2);
        
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
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketMobPosition(gui.x, gui.y, gui.z, txt_xPosition.getText(), txt_yPosition.getText(), txt_zPosition.getText(), (btn_type.displayString.equals("Relative") ? true : false)));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
                case 2:
                	if(this.btn_type.displayString.equals("Relative")) {
                		this.btn_type.displayString = "Exact";
                	}
                	else {
                		this.btn_type.displayString = "Relative";
                	}
                	this.redoTextOnBoxs(gui);
                    break;
            }
        }
	}

	public void redoTextOnBoxs(GuiFilterMenu gui) {
		TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			String xM = "" + (SpawnerHelper.getPositionX(tile, gui.x) - (btn_type.displayString.equals("Relative") ? gui.x : 0));
			String yM = "" + (SpawnerHelper.getPositionY(tile, gui.y) - (btn_type.displayString.equals("Relative") ? gui.y : 0));
			String zM = "" + (SpawnerHelper.getPositionZ(tile, gui.z) - (btn_type.displayString.equals("Relative") ? gui.z : 0));
			LogHelper.logDebug(xM);
			LogHelper.logDebug(yM);
			LogHelper.logDebug(zM);
			if(xM.endsWith(".0")) xM.replace(".0", "");
			if(yM.endsWith(".0")) yM.replace(".0", "");
			if(zM.endsWith(".0")) zM.replace(".0", "");
			this.txt_xPosition.setText(xM);
			this.txt_yPosition.setText(yM);
			this.txt_zPosition.setText(zM);
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
