package mapmakingtools.client.gui.textfield;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceLib;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

/**
 * @author ProPercivalalb
 */
public class GuiTextFieldColour extends GuiTextFieldAdvanced {

	public int textColourIndex = TextColour.GOLD.ordinal();
	private boolean colourTab = false;
	private int xClick = 0;
	private int yClick = 0;
	
	private FontRenderer font;
	private int xWidth;
	private int yHeight;
	
	public GuiTextFieldColour(int componentId, FontRenderer font, int xPosition, int yPosition, int width, int height) {
		super(componentId, font, xPosition, yPosition, width, height);
		this.font = font;
		this.x = xPosition;
		this.y = yPosition;
		this.xWidth = width;
		this.yHeight = height;
	}

	@Override
    public void drawTextField(int mouseX, int mouseY, float partialTicks) {
    	super.drawTextField(mouseX, mouseY, partialTicks);
	}
	
	public void drawToolTip(int xMouse, int yMouse) {
		if(this.colourTab) {
            
			drawRect(this.x + this.xClick - 1, this.y + this.yClick - 1, this.x + this.xClick + 60 + 1, this.y + this.yClick + 30 + 1, -14079702);
			drawRect(this.x + this.xClick, this.y + this.yClick, this.x + this.xClick + 60, this.y + this.yClick + 26, -6908265);
		  	TextColour colour = getCurrentColour(textColourIndex);
			ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.BUTTON_TEXT_COLOUR);
	        GlStateManager.color4f(colour.red / 255F, colour.green / 255F, colour.blue / 255F, 1.0F);
	        boolean field_146123_n = xMouse >= this.x + this.xClick + 3 && yMouse >= this.y + this.yClick + 3 && xMouse < this.x + this.xClick + 3 + 20 && yMouse < this.y + this.yClick + 3 + 20;
	        int hoverState = field_146123_n ? 2 : 1;
	        this.drawTexturedModalRect(this.x + this.xClick + 3, y + this.yClick + 3, 0, 46 + hoverState * 20, 20 / 2, 20 / 2);//top left
	        this.drawTexturedModalRect(x + this.xClick + 20 / 2 + 3, y + this.yClick + 3, 200 - 20 / 2, 46 + hoverState * 20, 20 / 2, 20 / 2);//top right
	        this.drawTexturedModalRect(x + this.xClick + 3, y + this.yClick + 3 + 20 / 2, 0, 46 + hoverState * 20 + 20 - 20 / 2, 20 / 2, 20 / 2);//bottom left
	        this.drawTexturedModalRect(x + this.xClick + 3 + 20 / 2, y + this.yClick + 3 + 20 / 2, 200 - 20 / 2, 46 + hoverState * 20 + 20 - 20 / 2, 20 / 2, 20 / 2);//bottom right
	        
	        if(colour.name.length() > 6) {
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name.substring(0, 6), x + this.xClick + 20 + 5, y + this.yClick + 3, -1);
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name.substring(6, colour.name.length()), x + this.xClick + 20 + 5, y + this.yClick + 3 + this.font.FONT_HEIGHT, -1);
	        }
	        else
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name, x + this.xClick + 20 + 5, y + this.yClick + 3, -1);
	        
	        
		}
	}
	
	public void textChange() {
		
	}
	
	@Override
	public boolean charTyped(char par1, int par2) {
		boolean bool = super.charTyped(par1, par2);
		this.textChange();
    	return bool;
    }
	
	public boolean preMouseClick(double mouseX, double mouseY, int buttonIndex) {
		if(this.colourTab && mouseX >= this.x + this.xClick && mouseY >= this.y + this.yClick && mouseX < this.x + this.xClick + 60 && mouseY < this.y + this.yClick + 30) {
			
			if(this.isFocused()) {
			  	TextColour colour = getCurrentColour(textColourIndex);
				if(buttonIndex == 0)
					leftClick();
				else if(buttonIndex == 1)
					rightClick();
				else if(buttonIndex == 2) {
					this.setText(this.getText().substring(0, this.getCursorPosition()) + colour.getColour() + this.getText().substring(this.getCursorPosition(), this.getText().length()));
					this.textChange();
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

		this.colourTab = false;
		
		if(this.isFocused() && mouseButton == 1) {
			this.colourTab = true;
			this.xClick = (int) (mouseX - this.x);
			this.yClick = (int) (mouseY - this.y);
			
			return super.mouseClicked(mouseX, mouseY, 0);
			//this.setText(this.getText().substring(0, this.getCursorPosition()) + TextColour.RED.getColour() + this.getText().substring(this.getCursorPosition(), this.getText().length()));
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
		
	}
	
	public TextColour getCurrentColour(int index) {
		if(index <= 0 || index >= TextColour.values().length)
			return TextColour.BLACK;
		return TextColour.values()[index];
	}
	
	public void leftClick() {
		textColourIndex--;
		while(textColourIndex < 0)
			textColourIndex += TextColour.values().length;
		while(textColourIndex > TextColour.values().length - 1)
			textColourIndex = 0;
	}
	
	public void rightClick() {
		textColourIndex++;
		while(textColourIndex < 0)
			textColourIndex += TextColour.values().length;
		while(textColourIndex > TextColour.values().length - 1)
			textColourIndex = 0;
	}
	
	public enum TextColour {
		
		BLACK(0, 0, 0, TextFormatting.BLACK, "Black"),
		DARK_BLUE(0, 0, 170, TextFormatting.DARK_BLUE, "Dark  Blue"),
		DARK_GREEN(0, 170, 0, TextFormatting.DARK_GREEN, "Dark  Green"),
		DARK_AQUA(0, 170, 170, TextFormatting.DARK_AQUA, "Dark  Aqua"),
		DARK_RED(170, 0, 0, TextFormatting.DARK_RED, "Red"),
		PURPLE(170, 0, 170, TextFormatting.DARK_PURPLE, "Purple"),
		GOLD(255, 170, 0, TextFormatting.GOLD, "Gold"),
		GREY(170, 170, 170, TextFormatting.GRAY, "Gray"),
		DARK_GREY(85, 85, 85, TextFormatting.DARK_GRAY, "Dark  Gray"),
		BLUE(85, 85, 255, TextFormatting.BLUE, "Blue"),
		GREEN(85, 255, 85, TextFormatting.GREEN, "Green"),
		AQUA(85, 255, 255, TextFormatting.AQUA, "Aqua"),
		RED(255, 85, 85, TextFormatting.RED, "Red"),
		LIGHT_PURPLE(255, 85, 255, TextFormatting.LIGHT_PURPLE, "Light Purple"),
		YELLOW(255, 255, 85, TextFormatting.YELLOW, "Yellow"),
		WHITE(255, 255, 255, TextFormatting.WHITE, "White"),
		OBFUSCATED(255, 255, 255, TextFormatting.OBFUSCATED, "Obfuscated"),
		BOLD(255, 255, 255, TextFormatting.BOLD, "Bold"),
		STRIKETHROUGHT(255, 255, 255, TextFormatting.STRIKETHROUGH, "Strike"),
		UNDERLINE(255, 255, 255, TextFormatting.UNDERLINE, "Underline"),
		ITALIC(255, 255, 255, TextFormatting.ITALIC, "Italic"),
		RESET(255, 255, 255, TextFormatting.RESET, "Reset");
		
		int red, green, blue;
		TextFormatting prefix;
		String name;
		
		TextColour(int red, int green, int blue, TextFormatting prefix, String name) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.prefix = prefix;
			this.name = name;
		}
		
		public String getName() {
			return this.getColour() + this.name();
		}
		
		public String getColour() {
			return prefix.toString();
		}
	}
}
