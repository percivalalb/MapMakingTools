package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GuiItemEditor extends GuiContainer {
	
	public GuiItemEditor(EntityPlayer player, int slotNo) {
		super(new ContainerItemEditor(player, slotNo));
		this.xSize = 175;
		this.ySize = 132;
	}

    public void initGui() {
    	super.initGui();
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.itemEditor);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

}
