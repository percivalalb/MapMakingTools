package mapmakingtools.client.gui.textfield;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public class GuiTextFieldAdvanced extends GuiTextField {

	public FontRenderer fontRenderer;
	
    public GuiTextFieldAdvanced(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
    	super(componentId, fontrendererObj, x, y, width, height);
    	this.fontRenderer = fontrendererObj;
	}

    
    /**
    public void deleteFromCursor(int p_146175_1_)
    {
        if (this.text.length() != 0)
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                boolean flag = p_146175_1_ < 0;
                int j = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int k = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String s = "";

                //MAP MAKING TOOLS (START)
                int change = 0;
                if(j > 0 || k < this.text.length() - 2)
                  	if(this.text.substring(j - (flag ? 1 : -1), k - (flag ? 1 : -1)).equals(Symbols.selectionSign))
                  		change = 1;
                
                if (j >= 0)
                    s = this.text.substring(0, j - (flag ? change : 0));
                //MAP MAKING TOOLS (END)
                
                if (k < this.text.length())
                    s = s + this.text.substring(k - (!flag ? change : 0));

                this.text = s;

                if (flag)
                {
                    this.moveCursorBy(p_146175_1_);
                }
            }
        }
    }
**/

    @Override
    public void drawTextBox() {
    	super.drawTextBox();
    	/**
        if(this.getVisible()) {
            if(this.getEnableBackgroundDrawing()) {
                drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.trimStringToWidth(this.getText().substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;

            //MAP MAKING TOOLS (START)
	        ScaledResolution scaling = new ScaledResolution(ClientHelper.getClient());
            int scaleFactor = scaling.getScaleFactor();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
    		GL11.glScissor(this.x * scaleFactor, (scaling.getScaledHeight() - (this.y + this.height)) * scaleFactor, this.width * scaleFactor, this.height * scaleFactor);
    		this.fontRenderer.drawStringWithShadow(this.getText(), l - this.fontRenderer.getStringWidth(this.getText().substring(0, this.lineScrollOffset)), i1, i);
    		
            if(k > s.length())
                k = s.length();

            boolean flag2 = this.cursorPosition < this.getText().length() || this.getText().length() >= this.getMaxStringLength();

            if (flag1 && !flag2) {
            	int u = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.fontRenderer.drawStringWithShadow("_", u, i1, i);
            }

            if (flag1 && flag2) {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(l1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
            
	        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	        //MAP MAKING TOOLS (END)
	        
        }**/
    }
}