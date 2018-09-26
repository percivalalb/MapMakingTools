package mapmakingtools.api.filter;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public interface IFilterGui extends IFilterBase {

	public int xFakeSize();
	public int getScreenWidth();
	public int getScreenHeight();
	public int getGuiY();
	public int getGuiX();
	public IFilterContainer getFilterContainer();
	public void setYSize(int newYSize);
	public void drawHoveringTooltip(List<String> text, int mouseX, int mouseY);
	
	public List<GuiLabel> getLabelList();
	public List<GuiButton> getButtonList();
	public List<GuiTextField> getTextBoxList();
	public FontRenderer getFont();
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6);
}
