package mapmakingtools.api;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;

/**
 * @author ProPercivalalb
 */
public abstract class ScrollMenu {

	protected ScaledResolution scaling;
	public int scrollY = 0;
    private int scrollHeight = 0;
    private int listHeight = 0;
    public int selected = -1;
    private boolean isScrolling = false;
	
    public GuiScreen screen;
	public int xPosition;
	public int yPosition;
	public int width;
	public int height;
	public int noColumns;
	public List<String> strRefrence;
	
	public ScrollMenu(GuiScreen screen, int xPosition, int yPosition, int width, int height, int noColumns, List<String> strRefrence) {
		this.screen = screen;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.width = width;
		this.height = height;
		this.noColumns = noColumns;
		this.strRefrence = strRefrence;
	}
	
	public void initGui() {
		this.scaling = new ScaledResolution(ClientHelper.getClient());
		if(this.strRefrence.size() > 0) {
        	this.listHeight = MathHelper.ceil(this.strRefrence.size() / (double)this.noColumns) * 14 - this.height;
        	
        	this.scrollHeight = (int) ((this.height / (double)(this.listHeight + this.height)) * this.height);
        	if(this.scrollHeight < 20)
        		this.scrollHeight = 20;
        	//FMLLog.info("" + this.scrollHeight);
        	//if(this.scrollHeight)
        	
        	/**
        	if(this.listHeight % 14 != 0) {
            	if(this.strRefrence.size() % this.noColumns == 1)
            		this.listHeight += 14 + this.listHeight % 14 - 4;
            	else
            		this.listHeight += this.listHeight % 14 - 4;
            }
        	
            this.scrollHeight = (int)((double)this.height / (double)(this.listHeight + this.height) * (double)this.height);

            if (this.scrollHeight <= 0 || this.scrollHeight >= height) {
                this.scrollHeight = height;
            }**/
		}
		else {
			this.listHeight = this.height;
			this.scrollHeight = this.height;
		}
	}
	
	public void drawGuiContainerBackgroundLayer(float partialTicks, int xMouse, int yMouse) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.clipToSize();
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceReference.SCREEN_SCROLL);
        int var9;
        for(var9 = 0; var9 < this.strRefrence.size(); ++var9) {
    		int row = (var9 - (var9 % this.noColumns)) / this.noColumns;
    		int column = var9;
    		while(column >= this.noColumns)
    			column -= this.noColumns;
        	int columnSize = this.width / (this.noColumns) - column;
        	
            int var10 = this.xPosition + 2 + (columnSize * column);
            int var11 = this.yPosition + (14 * row) + 2 - this.scrollY;

            screen.drawTexturedModalRect(var10, var11, 0 + (this.selected != var9 ? 0 : 8), 135, 8, 9);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        RenderHelper.enableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        if (this.scrollHeight <= this.height && this.strRefrence.size() > 0)
            this.drawScrollBar();
        
        this.drawScrollList();
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        
        if (this.scrollHeight <= this.height && this.strRefrence.size() > 0) {
            xMouse -= this.xPosition;
            yMouse -= this.yPosition;

            if (Mouse.isButtonDown(0)) {
                if (xMouse >= this.width - 19 && xMouse < this.width - 2 && yMouse >= 6 && yMouse < this.height - 6) {
                    this.isScrolling = true;
                }
            }
            else {
                this.isScrolling = false;
            }

            if (this.isScrolling) {
                this.scrollY = (yMouse - 6) * this.listHeight / ((this.height) - (this.scrollHeight >> 1));

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
	}
	
	private void drawScrollBar() {
		int width = this.xPosition + this.width;
		int height = this.yPosition + this.scrollY * (this.height - this.scrollHeight) / this.listHeight;
	    screen.drawTexturedModalRect(width - 18, height, 0, 144, 15, 1);
	    int var3;
	    for(var3 = height + 1; var3 < height + this.scrollHeight - 2; ++var3) {
	    	screen.drawTexturedModalRect(width - 18, var3, 0, 145, 15, 1);
	    }
	    screen.drawTexturedModalRect(width - 18, var3, 0, 146, 15, 1);
	}  
	
	private void drawScrollList() {
	    this.clipToSize();

        for (int var3 = 0; var3 < this.strRefrence.size(); ++var3) {
    		int row = (var3 - (var3 % noColumns)) / noColumns;
    		int column = var3;
    		while(column >= noColumns)
    			column -= noColumns;
        	int columnSize = this.width / (noColumns) - column;
        	
            int var4 = this.xPosition + 12 + (columnSize * column);
            int var5 = this.yPosition + (14 * row) + 2 - this.scrollY;
            String displayStr = getDisplayString((String)this.strRefrence.get(var3));
            ClientHelper.getClient().fontRenderer.drawString(displayStr, var4, var5, 16777215);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	private boolean mouseInRadioButton(int xMouse, int yMouse, int buttonIndex) {
		int row = (buttonIndex - (buttonIndex % this.noColumns)) / this.noColumns;
		int column = buttonIndex;
		while(column >= this.noColumns)
			column -= this.noColumns;
    	int columnSize = this.width / (this.noColumns) - column;

        int var4 = 2 + columnSize * column;
	    int var5 = (14 * row) + 2 - this.scrollY;
	    return xMouse >= var4 - 1 && xMouse < var4 + 9 && yMouse >= var5 - 1 && yMouse < var5 + 10;
	}
	
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) {
	     xMouse -= this.xPosition;
         yMouse -= this.yPosition;

	     if(mouseButton == 0 && xMouse >= 0 && xMouse < this.width && yMouse >= 0 && yMouse < this.height + 6) {
	         for(int buttonIndex = 0; buttonIndex < this.strRefrence.size(); ++buttonIndex) {
	             if(this.mouseInRadioButton(xMouse, yMouse, buttonIndex)) {
	                 this.selected = buttonIndex;
	                 this.onSetButton();
	                 break;
	             }
	         }
	     }
	}
	
	protected boolean func_146978_c(int topX, int topY, int width, int height, int xMouse, int yMouse) {
	    xMouse -= this.xPosition;
        yMouse -= this.yPosition;
	    return xMouse >= topX - 1 && xMouse < topX + width + 1 && yMouse >= topY - 1 && yMouse < topY + height + 1;
	}
	
	public void clipToSize() {
		int scaleFactor = this.scaling.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(this.xPosition * scaleFactor, (this.scaling.getScaledHeight() - (this.yPosition + this.height)) * scaleFactor, this.width * scaleFactor, this.height * scaleFactor);
	}
	
	public void setSelected(int index) {
		if(index <= 0 || index >= this.strRefrence.size())
			return;
		this.selected = index;
	}
	
	/** Checks if index is outside display list size **/
	public boolean isIndexValid() {
		return this.selected != -1 && this.selected < this.strRefrence.size();
	}
	
	public abstract void onSetButton();
	
	public abstract String getDisplayString(String listStr);
}
