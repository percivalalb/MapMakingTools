package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.IFilter;
import mapmakingtools.client.util.ButtonType;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class GuiTabSelect extends GuiButton {
    
	private Minecraft mc = Minecraft.getMinecraft();
	public boolean isSelected;
	public ButtonType type;
	public IFilter filter;
	
	public GuiTabSelect(int id, int xPosition, int yPosition, ButtonType type, IFilter filter) {
        super(id, xPosition, yPosition, 29, 27, "");
        this.isSelected = false;
        this.type = type;
        this.filter = filter;
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int i, int j) {
        if(this.drawButton) {
        	FontRenderer fontRenderer = minecraft.fontRenderer;
        	minecraft.func_110434_K().func_110577_a(ResourceReference.tabs);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int difference = isSelected ? 32 : 30;
            this.drawTexturedModalRect(xPosition + (isSelected && type == ButtonType.RIGHT ? -2 : 0), yPosition, 0 + (isSelected ? 30 : 0), 28 * (type == ButtonType.LEFT ? 1 : 0), difference, 27 * (type == ButtonType.LEFT ? 2 : 1));//top left
            Icon icon = filter.getDisplayIcon();
            minecraft.func_110434_K().func_110577_a(TextureMap.field_110576_c);
            if(icon != null) {
            	this.drawTexturedModelRectFromIcon(xPosition + 5 + (type == ButtonType.LEFT ? 3 : 0), yPosition + 5, icon, 16, 16);
            }
            this.mouseDragged(minecraft, i, j);
    	}
	}
    
    public boolean isMouseAbove(int par2, int par3) {
    	return par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
    }
}
