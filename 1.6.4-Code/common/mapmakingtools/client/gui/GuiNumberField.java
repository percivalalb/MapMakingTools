package mapmakingtools.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

public class GuiNumberField extends GuiTextField {

    public final int xPos;
    public final int yPos;

    /** The width of this text field. */
    public final int width;
    public final int height;
	
	public GuiNumberField(int xPos, int yPos, int defaultValue) {
		super(Minecraft.getMinecraft().fontRenderer, xPos, yPos, 48, 12);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = 80;
		this.height = 16;
		this.setEnableBackgroundDrawing(false);
		this.setMaxStringLength(7);
		this.setText(String.valueOf(defaultValue));
	}
}
