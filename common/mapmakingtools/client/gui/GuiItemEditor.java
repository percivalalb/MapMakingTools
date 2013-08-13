package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GuiItemEditor extends GuiContainer {
	
	public GuiItemEditor(EntityPlayer player) {
		super(new ContainerItemEditor(player));
		this.xSize = 175;
		this.ySize = 132;
	}

    public void initGui() {
    	super.initGui();
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(ResourceReference.itemEditor);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

}
