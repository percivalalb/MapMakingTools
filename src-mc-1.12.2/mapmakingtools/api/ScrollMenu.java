package mapmakingtools.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import mapmakingtools.lib.ResourceLib;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;

/**
 * @author ProPercivalalb
 */
public abstract class ScrollMenu<T> {

	private static int ELEMENT_HEIGHT = 14;
	
	protected ScaledResolution scaling;
	public int scrollY = 0;
    private int scrollHeight = 0;
    private int listHeight = 0;
    
    //private int totalListHeight = 0;
    public List<Integer> selected = new ArrayList<>();
    private boolean isScrolling = false;
	
    public GuiScreen screen;
    
    // Position of port
	public int x;
	public int y;
	
	// View port dimensions
	public int width;
	public int height;
	
	// Size of table
	public int noCol;
	public int noRow;
	
	// Elements in table, index increases across columns
	public List<T> elements;
	
	// The maximum number of selected items, -1 indicates unlimited can be selected 
	public int maxSelected;
	public boolean canHaveNoneSelected;
	
	
	public ScrollMenu(GuiScreen screen, int xPosition, int yPosition, int width, int height, int noColumns, List<T> elements) {
		this(screen, xPosition, yPosition, width, height, noColumns);
		this.elements = ImmutableList.copyOf(elements);
	}
	
	public ScrollMenu(GuiScreen screen, int xPosition, int yPosition, int width, int height, int noColumns) {
		this.screen = screen;
		this.x = xPosition;
		this.y = yPosition;
		this.width = width;
		this.height = height;
		this.noCol = noColumns;
		this.elements = Collections.emptyList();
		this.maxSelected = 1;
		this.canHaveNoneSelected = true;
	}
	
	public void initGui() {
		this.scaling = new ScaledResolution(this.screen.mc);
		
		if(this.elements.size() > 0) {
			this.noRow = MathHelper.ceil(this.elements.size() / (double)this.noCol);
			
        	this.listHeight = this.noRow * ELEMENT_HEIGHT - this.height;
        	this.scrollHeight = (int)((this.height / (double)(this.listHeight + this.height)) * this.height);
        	
        	if(this.scrollHeight < 20) this.scrollHeight = 20;
		}
		else {
			this.noRow = 0;
			
			this.listHeight = this.height;
			this.scrollHeight = this.height;
		}
	}
	
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
		this.clipToSize();
		this.screen.mc.getTextureManager().bindTexture(ResourceLib.SCREEN_SCROLL);
		
    	int columnSize = this.width / this.noCol;
    	for(int c = 0; c < this.noCol; c++) {
    		for(int r = 0; r < this.noRow; r++) {
        		int index = r * this.noCol + c;
        		if(index >= this.elements.size()) break;
        		
	        	
	            int posX = this.x + 2 + (columnSize * c);
	            int posY = this.y + (ELEMENT_HEIGHT * r) + 2 - this.scrollY;
	
	            this.screen.drawTexturedModalRect(posX, posY, 0 + (this.selected.contains(index) ? 8 : 0), 135, 8, 9);
        	 }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        
        if(this.scrollHeight <= this.height && this.elements.size() > 0)
            this.drawScrollBar();
        
        this.drawScrollList();
        
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        
        if (this.scrollHeight <= this.height && this.elements.size() > 0) {
            mouseX -= this.x;
            mouseY -= this.y;

            if(Mouse.isButtonDown(0)) {
                if(mouseX >= this.width - 19 && mouseX < this.width - 2 && mouseY >= 6 && mouseY < this.height - 6) {
                    this.isScrolling = true;
                }
            }
            else { 
            	this.isScrolling = false;
            }

            if(this.isScrolling) {
                this.scrollY = (mouseY - 6) * this.listHeight / (this.height - (this.scrollHeight >> 1));

                if(this.scrollY < 0) this.scrollY = 0;
                if(this.scrollY > this.listHeight) this.scrollY = this.listHeight;
            }

            int mouseWheel = Mouse.getDWheel();

            if(mouseWheel < 0) {
                this.scrollY += ELEMENT_HEIGHT;

                if(this.scrollY > this.listHeight) this.scrollY = this.listHeight;
            }
            else if(mouseWheel > 0) {
                this.scrollY -= ELEMENT_HEIGHT;

                if(this.scrollY < 0) this.scrollY = 0;
            }
        }
	}
	
	private void drawScrollBar() {
		int width = this.x + this.width;
		int height = this.y + this.scrollY * (this.height - this.scrollHeight) / this.listHeight;
	    this.screen.drawTexturedModalRect(width - 18, height, 0, 144, 15, 1);
	    for(int i = height + 1; i < height + this.scrollHeight - 2; ++i) {
	    	this.screen.drawTexturedModalRect(width - 18, i, 0, 145, 15, 1);
	    }
	    this.screen.drawTexturedModalRect(width - 18, height + this.scrollHeight - 1, 0, 146, 15, 1);
	}  
	
	private void drawScrollList() {
	    this.clipToSize();

	    int columnSize = this.width / this.noCol;
    	for(int c = 0; c < this.noCol; c++) {
    		for(int r = 0; r < this.noRow; r++) {
        		int index = r * this.noCol + c;
        		if(index >= this.elements.size()) break;
        		
	        	
	            int posX = this.x + 12 + (columnSize * c);
	            int posY = this.y + (ELEMENT_HEIGHT * r) + 2 - this.scrollY;
	            String displayStr = this.getDisplayString(this.elements.get(index));
	            this.screen.mc.fontRenderer.drawString(displayStr, posX, posY, 16777215);
    		}
    	}

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	private boolean mouseInRadioButton(int mouseX, int mouseY, int buttonIndex) {
		int row = MathHelper.floor(buttonIndex / (double)this.noCol);
		int column = buttonIndex % this.noCol;
    	int columnSize = this.width / this.noCol;

        int var4 = 2 + columnSize * column;
	    int var5 = (14 * row) + 2 - this.scrollY;
	    return mouseX >= var4 - 1 && mouseX < var4 + 9 && mouseY >= var5 - 1 && mouseY < var5 + 10;
	}
	
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) {
	     xMouse -= this.x;
         yMouse -= this.y;

	     if(mouseButton == 0 && xMouse >= 0 && xMouse < this.width && yMouse >= 0 && yMouse < this.height + 6) {
	         for(int buttonIndex = 0; buttonIndex < this.elements.size(); ++buttonIndex) {
	             if(this.mouseInRadioButton(xMouse, yMouse, buttonIndex)) {
	                 if(this.setSelected(buttonIndex))
	                	 this.onSetButton();
	                 //else
	                 //	 this.onRemoveButton();
	                	 
	                 break;
	             }
	         }
	     }
	}
	
	protected boolean func_146978_c(int topX, int topY, int width, int height, int xMouse, int yMouse) {
	    xMouse -= this.x;
        yMouse -= this.y;
	    return xMouse >= topX - 1 && xMouse < topX + width + 1 && yMouse >= topY - 1 && yMouse < topY + height + 1;
	}
	
	public void clipToSize() {
		int scaleFactor = this.scaling.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(this.x * scaleFactor, (this.scaling.getScaledHeight() - (this.y + this.height)) * scaleFactor, this.width * scaleFactor, this.height * scaleFactor);
	}
	
	public boolean setSelected(int index) {
		if(index < 0 || index >= this.elements.size())
			return false;
		
		int selIndex = this.selected.indexOf(index);
		
		// Index is not currently selected
		if(selIndex == -1) {
			this.selected.add(index);
			
			if(this.maxSelected != -1 && this.selected.size() > this.maxSelected)
				this.selected.remove(0);
			
			return true;
		} 
		else if(this.canHaveNoneSelected || this.selected.size() > 1) {
			this.selected.remove(selIndex);
		}
		
		return false;
	}
	
	/** Checks if index is outside display list size **/
	public boolean hasSelection() {
		return this.selected.size() > 0;
	}
	
	public void clearSelected() {
		this.selected.clear();
	}
	
	public int getRecentIndex() {
		return this.selected.get(this.selected.size() - 1);
	}
	
	public int getOldestIndex() {
		return this.selected.get(0);
	}
	
	public List<T> getSelected() {
		List<T> selected = new ArrayList<>();
		for(int i : this.selected) {
			selected.add(this.elements.get(i));
		}
		return selected;
	}
	
	public T getRecentSelection() {
		return this.elements.get(this.getRecentIndex());
	}
	
	public abstract void onSetButton();
	
	public String getDisplayString(T listStr) {
		return listStr.toString();
	}
}
