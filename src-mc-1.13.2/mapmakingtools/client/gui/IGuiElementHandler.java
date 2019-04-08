package mapmakingtools.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;

public interface IGuiElementHandler {
	
	public List<GuiLabel> getLabelList();
	public GuiButton addButtonToGui(GuiButton buttonIn);
	public GuiTextField addTextFieldToGui(GuiTextField fieldIn);
	public GuiLabel addLabelToGui(GuiLabel labelIn);
	public List<GuiButton> getButtonList();
	public List<GuiTextField> getTextBoxList();
	
	public <T extends IGuiEventListener> T addListenerToGui(T listenerIn);
}
