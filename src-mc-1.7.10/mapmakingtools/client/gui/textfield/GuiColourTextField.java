package mapmakingtools.client.gui.textfield;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import mapmakingtools.client.gui.button.GuiAdvancedTextField;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class GuiColourTextField extends GuiAdvancedTextField {

	public int textColourIndex = TextColour.GOLD.ordinal();
	private boolean colourTab = false;
	private int xClick = 0;
	private int yClick = 0;
	
	private FontRenderer font;
	private int xPos;
	private int yPos;
	private int xWidth;
	private int yHeight;
	
	public GuiColourTextField(FontRenderer font, int xPosition, int yPosition, int width, int height) {
		super(font, xPosition, yPosition, width, height);
		this.font = font;
		this.xPos = xPosition;
		this.yPos = yPosition;
		this.xWidth = width;
		this.yHeight = height;
	}

	@Override
	public void drawTextBox() {
		super.drawTextBox();
	}
	
	public void drawToolTip(int xMouse, int yMouse) {
		if(this.colourTab) {
            
			drawRect(this.xPos + this.xClick - 1, this.yPos + this.yClick - 1, this.xPos + this.xClick + 60 + 1, this.yPos + this.yClick + 30 + 1, -14079702);
			drawRect(this.xPos + this.xClick, this.yPos + this.yClick, this.xPos + this.xClick + 60, this.yPos + this.yClick + 26, -6908265);
		  	TextColour colour = getCurrentColour(textColourIndex);
			ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.buttonTextColour);
	        GL11.glColor4f(colour.red / 255F, colour.green / 255F, colour.blue / 255F, 1.0F);
	        boolean field_146123_n = xMouse >= this.xPos + this.xClick + 3 && yMouse >= this.yPos + this.yClick + 3 && xMouse < this.xPos + this.xClick + 3 + 20 && yMouse < this.yPos + this.yClick + 3 + 20;
	        int hoverState = this.getHoverState(field_146123_n);
	        this.drawTexturedModalRect(this.xPos + this.xClick + 3, yPos + this.yClick + 3, 0, 46 + hoverState * 20, 20 / 2, 20 / 2);//top left
	        this.drawTexturedModalRect(xPos + this.xClick + 20 / 2 + 3, yPos + this.yClick + 3, 200 - 20 / 2, 46 + hoverState * 20, 20 / 2, 20 / 2);//top right
	        this.drawTexturedModalRect(xPos + this.xClick + 3, yPos + this.yClick + 3 + 20 / 2, 0, 46 + hoverState * 20 + 20 - 20 / 2, 20 / 2, 20 / 2);//bottom left
	        this.drawTexturedModalRect(xPos + this.xClick + 3 + 20 / 2, yPos + this.yClick + 3 + 20 / 2, 200 - 20 / 2, 46 + hoverState * 20 + 20 - 20 / 2, 20 / 2, 20 / 2);//bottom right
	        
	        if(colour.name.length() > 6) {
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name.substring(0, 6), xPos + this.xClick + 20 + 5, yPos + this.yClick + 3, -1);
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name.substring(6, colour.name.length()), xPos + this.xClick + 20 + 5, yPos + this.yClick + 3 + this.font.FONT_HEIGHT, -1);
	        }
	        else
	        	this.font.drawStringWithShadow(colour.getColour() + colour.name, xPos + this.xClick + 20 + 5, yPos + this.yClick + 3, -1);
	        
	        
		}
	}
	
	protected int getHoverState(boolean p_146114_1_) {
	    byte b0 = 1;

	    if (p_146114_1_) {
	        b0 = 2;
	    }

	    return b0;
	}
	
	public void textChange() {
		
	}
	
	@Override
	public boolean textboxKeyTyped(char par1, int par2) {
		boolean bool = super.textboxKeyTyped(par1, par2);
		this.textChange();
    	return bool;
    }
	
	public boolean preMouseClick(int xMouse, int yMouse, int buttonIndex) {
		if(this.colourTab && xMouse >= this.xPos + this.xClick && yMouse >= this.yPos + this.yClick && xMouse < this.xPos + this.xClick + 60 && yMouse < this.yPos + this.yClick + 30) {
			
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
	public void mouseClicked(int xMouse, int yMouse, int buttonIndex) {
		
		super.mouseClicked(xMouse, yMouse, buttonIndex);
		
		this.colourTab = false;
		
		if(this.isFocused() && buttonIndex == 1) {
			FMLLog.info("click");
			this.colourTab = true;
			this.xClick = xMouse - this.xPos;
			this.yClick = yMouse - this.yPos;
			
			super.mouseClicked(xMouse, yMouse, 0);
			//this.setText(this.getText().substring(0, this.getCursorPosition()) + TextColour.RED.getColour() + this.getText().substring(this.getCursorPosition(), this.getText().length()));
		}
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
		
		BLACK(0, 0, 0, EnumChatFormatting.BLACK, "Black"),
		DARK_BLUE(0, 0, 170, EnumChatFormatting.DARK_BLUE, "Dark  Blue"),
		DARK_GREEN(0, 170, 0, EnumChatFormatting.DARK_GREEN, "Dark  Green"),
		DARK_AQUA(0, 170, 170, EnumChatFormatting.DARK_AQUA, "Dark  Aqua"),
		DARK_RED(170, 0, 0, EnumChatFormatting.DARK_RED, "Red"),
		PURPLE(170, 0, 170, EnumChatFormatting.DARK_PURPLE, "Purple"),
		GOLD(255, 170, 0, EnumChatFormatting.GOLD, "Gold"),
		GREY(170, 170, 170, EnumChatFormatting.GRAY, "Gray"),
		DARK_GREY(85, 85, 85, EnumChatFormatting.DARK_GRAY, "Dark  Gray"),
		BLUE(85, 85, 255, EnumChatFormatting.BLUE, "Blue"),
		GREEN(85, 255, 85, EnumChatFormatting.GREEN, "Green"),
		AQUA(85, 255, 255, EnumChatFormatting.AQUA, "Aqua"),
		RED(255, 85, 85, EnumChatFormatting.RED, "Red"),
		LIGHT_PURPLE(255, 85, 255, EnumChatFormatting.LIGHT_PURPLE, "Light Purple"),
		YELLOW(255, 255, 85, EnumChatFormatting.YELLOW, "Yellow"),
		WHITE(255, 255, 255, EnumChatFormatting.WHITE, "White"),
		OBFUSCATED(255, 255, 255, EnumChatFormatting.OBFUSCATED, "Obfuscated"),
		BOLD(255, 255, 255, EnumChatFormatting.BOLD, "Bold"),
		STRIKETHROUGHT(255, 255, 255, EnumChatFormatting.STRIKETHROUGH, "Strike"),
		UNDERLINE(255, 255, 255, EnumChatFormatting.UNDERLINE, "Underline"),
		ITALIC(255, 255, 255, EnumChatFormatting.ITALIC, "Italic"),
		RESET(255, 255, 255, EnumChatFormatting.RESET, "Reset");
		
		int red, green, blue;
		EnumChatFormatting prefix;
		String name;
		
		TextColour(int red, int green, int blue, EnumChatFormatting prefix, String name) {
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
