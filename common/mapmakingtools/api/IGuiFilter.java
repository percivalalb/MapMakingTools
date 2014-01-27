package mapmakingtools.api;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IGuiFilter {

	public int xFakeSize();
	public int yFakeSize();
	public int getWidth();
	public int getHeight();
	public void setYSize(int newYSize);
	public void drawHoveringText(List text, int mouseX, int mouseY);
	
	public int getX();
	public int getY();
	public int getZ();
	public int getEntityId();
	public World getWorld();
	public EntityPlayer getPlayer();
	public List<GuiLabel> getLabelList();
	public List<GuiButton> getButtonList();
	public List<GuiTextField> getTextBoxList();
	public FontRenderer getFont();
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6);
}
