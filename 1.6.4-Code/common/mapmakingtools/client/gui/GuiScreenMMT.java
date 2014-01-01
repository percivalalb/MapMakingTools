package mapmakingtools.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public class GuiScreenMMT extends GuiScreen {

    protected List textFieldList = new ArrayList();
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.textFieldList.clear();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void updateScreen() {
    	for (int k = 0; k < this.textFieldList.size(); ++k) {
        	GuiTextField guibutton = (GuiTextField)this.textFieldList.get(k);
            guibutton.updateCursorCounter();
        }
    }
    
    @Override
    protected void keyTyped(char var1, int keyId) {
        if (keyId == Keyboard.KEY_ESCAPE) { //Exit Screen
        	this.mc.displayGuiScreen((GuiScreen)null);
           	this.mc.setIngameFocus();
           	return;
        }
    	for (int k = 0; k < this.textFieldList.size(); ++k) {
        	GuiTextField guibutton = (GuiTextField)this.textFieldList.get(k);
            guibutton.textboxKeyTyped(var1, keyId);
        }
    }

    @Override
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        for (int k = 0; k < this.textFieldList.size(); ++k) {
        	GuiTextField guibutton = (GuiTextField)this.textFieldList.get(k);
            guibutton.mouseClicked(var1, var2, var3);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        for (int k = 0; k < this.textFieldList.size(); ++k) {
        	GuiTextField guibutton = (GuiTextField)this.textFieldList.get(k);
            guibutton.drawTextBox();
        }
    }
}
