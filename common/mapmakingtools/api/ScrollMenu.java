package mapmakingtools.api;

import java.awt.Toolkit;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StatCollector;

import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketMobType;

/**
 * @author ProPercivalalb
 */
public abstract class ScrollMenu {

	private Minecraft mc = Minecraft.getMinecraft();
	
	public GuiScreen screen;
	public List<String> list;
	protected ScaledResolution scaling = null;
    public int scrollY = 0;
    private int scrollHeight = 0;
    private int listHeight = 0;
    public int selected = -1;
    private boolean isScrolling = false;
    
    /** Instance properties, can't be changed **/
    public final int screenSizeX;
    public final int screenSizeY;
    public final int placementX;
    public final int placementY;
    public final int xOffset;
    public final int yOffset;
    public final int numberOfColumns;
	
	public ScrollMenu(GuiScreen guiScreen, int placementX, int placementY, int xOffset, int yOffset, int screenSizeX, int screenSizeY, int numberOfColumns, List<String> list) {
		this.screen = guiScreen;
		this.list = list;
		this.screenSizeX = screenSizeX;
		this.screenSizeY = screenSizeY;
		this.placementX = placementX;
		this.placementY = placementY;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.numberOfColumns = numberOfColumns;
	}
	
	/**
	 * 
	 * @param guiContainer The GuiContainer that the scroll menu is to be drawn in
	 * @param xOffset 
	 * @param yOffset
	 * @param screenSizeX
	 * @param screenSizeY
	 * @param list The list of
	 */
	public ScrollMenu(GuiContainer guiContainer, int xOffset, int yOffset, int screenSizeX, int screenSizeY, int numberOfColumns, List<String> list) {
		this.screen = guiContainer;
		this.list = list;
		this.screenSizeX = screenSizeX;
		this.screenSizeY = screenSizeY;
		this.placementX = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, guiContainer, 5);
		this.placementY = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, guiContainer, 6);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.numberOfColumns = numberOfColumns;
	}
	
	public void initGui() {
		this.scaling = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        if(list.size() > 0) {
        	if(numberOfColumns == 1) {
        		int value = screenSizeY;
        		if(value % 14 != 0)
        			value -= 14 - (value % 14);
        		this.listHeight = (14 * ((list.size() - (list.size() & 1) - 1)) - value);
        	}
        	else if(numberOfColumns == 2) {
        		 this.listHeight = (14 * ((list.size() + (list.size() & 1))) / (numberOfColumns + 1));
        	}
        	else if(numberOfColumns == 3 || numberOfColumns == 4) {
	            this.listHeight = (14 * ((list.size() + (list.size() & (numberOfColumns - 1))))) / (numberOfColumns * (numberOfColumns - 1));
        	}
        	else {
        		//this.listHeight = (14 * ((list.size() + (list.size() & (numberOfColumns - 1))))) / (numberOfColumns * times);
        	}
            if(this.listHeight % 14 != 0)
            	this.listHeight += 14 - (this.listHeight % 14) + 4;
            this.scrollHeight = (int)((double)screenSizeY / (double)(this.listHeight + screenSizeY) * (double)screenSizeY);

            if (this.scrollHeight <= 0 || this.scrollHeight >= screenSizeY) {
                this.scrollHeight = screenSizeY;
            }
        }
	}
	
	public void drawGuiContainerBackgroundLayer(int xMouse, int yMouse, float particleTicks) {
	    this.clip();
        ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenScroll);
        int var9;
        for(var9 = 0; var9 < list.size(); ++var9) {
    		int row = (var9 - (var9 % numberOfColumns)) / numberOfColumns;
    		int column = var9;
    		while(column >= numberOfColumns)
    			column -= numberOfColumns;
        	int columnSize = screenSizeX / (numberOfColumns) - column;
        	
            int var10 = placementX + xOffset + 10 + (columnSize * column);
            int var11 = placementY + yOffset + (14 * row) + 7 - this.scrollY;

            screen.drawTexturedModalRect(var10, var11, 0 + (this.selected != var9 ? 0 : 8), 135, 8, 9);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.scrollHeight != screenSizeY) {
            this.drawScrollBar();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPushMatrix();
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.drawScrollList();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();

        if (this.scrollHeight != screenSizeY) {
            xMouse -= placementX + xOffset;
            yMouse -= placementY + yOffset;

            if (Mouse.isButtonDown(0)) {
                if (xMouse >= 216 && xMouse < 233 && yMouse >= 6 && yMouse < 114) {
                    this.isScrolling = true;
                }
            }
            else {
                this.isScrolling = false;
            }

            if (this.isScrolling) {
                this.scrollY = (yMouse - 6) * this.listHeight / ((screenSizeY  + yOffset) - (this.scrollHeight >> 1));

                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }

                if (this.scrollY > this.listHeight) {
                    this.scrollY = this.listHeight;
                }
            }

            var9 = Mouse.getDWheel();

            if (var9 < 0) {
                this.scrollY += 14;

                if (this.scrollY > this.listHeight) {
                    this.scrollY = this.listHeight;
                }
            }
            else if (var9 > 0) {
                this.scrollY -= 14;

                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }
            }
        }
	    //int k = (gui.width - gui.xSize()) / 2;
	    //int l = (gui.height - 135) / 2;
        //gui.getFont().drawString(getFilterName(), k + 6, l + 6, 0);
	}
	
	private void drawScrollBar() {
		int width = this.placementX + xOffset + 227;
		int height = this.placementY + yOffset + 5 + this.scrollY * (screenSizeY - this.scrollHeight) / this.listHeight;
	    screen.drawTexturedModalRect(width - 10, height, 0, 144, 15, 1);
	    int var3;
	    for(var3 = height + 1; var3 < height + this.scrollHeight - 1; ++var3) {
	    	screen.drawTexturedModalRect(width - 10, var3, 0, 145, 15, 1);
	    }
	    screen.drawTexturedModalRect(width - 10, var3, 0, 146, 15, 1);
	}  
	
	private void drawScrollList() {
	    this.clip();

        for (int var3 = 0; var3 < list.size(); ++var3) {
    		int row = (var3 - (var3 % numberOfColumns)) / numberOfColumns;
    		int column = var3;
    		while(column >= numberOfColumns)
    			column -= numberOfColumns;
        	int columnSize = screenSizeX / (numberOfColumns) - column;
        	
            int var4 = placementX + xOffset + 20 + (columnSize * column);
            int var5 = placementY + yOffset + (14 * row) + 7 - this.scrollY;
            String displayStr = getDisplayString((String)list.get(var3));
            ClientHelper.mc.fontRenderer.drawString(displayStr, var4, var5, 16777215);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public abstract void clip();
	
	private boolean mouseInRadioButton(int xMouse, int yMouse, int buttonIndex) {
		int row = (buttonIndex - (buttonIndex % numberOfColumns)) / numberOfColumns;
		int column = buttonIndex;
		while(column >= numberOfColumns)
			column -= numberOfColumns;
    	int columnSize = screenSizeX / (numberOfColumns) - column;

        int var4 = 10 + columnSize * column;
	    int var5 = (14 * row) + 7 - this.scrollY;
	    return xMouse >= var4 - 1 && xMouse < var4 + 9 && yMouse >= var5 - 1 && yMouse < var5 + 10;
	}
	
	
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) {
	     xMouse -= (placementX + xOffset);
	     yMouse -= (placementY + yOffset);

	     if (mouseButton == 0 && xMouse >= 8 && xMouse < screenSizeX && yMouse >= 6 && yMouse < screenSizeY + 6) {
	         for (int buttonIndex = 0; buttonIndex < list.size(); ++buttonIndex) {
	             if (this.mouseInRadioButton(xMouse, yMouse, buttonIndex)) {
	                 this.selected = buttonIndex;
	                 this.onSetButton();
	                 break;
	             }
	         }
	     }
	}
	
	public void setSelected(int index) {
		if(index <= 0 || index >= list.size())
			return;
		this.selected = index;
	}
	
	public abstract void onSetButton();
	
	public abstract String getDisplayString(String listStr);
	
}
