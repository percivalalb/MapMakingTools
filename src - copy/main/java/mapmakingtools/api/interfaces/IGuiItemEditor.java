package mapmakingtools.api.interfaces;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public interface IGuiItemEditor {

	public FontRenderer getFontRenderer();
	public List<GuiLabel> getLabelList();
	public List<GuiButton> getButtonList();
	public List<GuiTextField> getTextBoxList();
	public void sendUpdateToServer(int data);
}
