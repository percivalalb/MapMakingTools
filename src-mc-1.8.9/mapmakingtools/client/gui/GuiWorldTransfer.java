package mapmakingtools.client.gui;

import org.lwjgl.opengl.GL11;

import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.container.ContainerWorldTransfer;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class GuiWorldTransfer extends GuiContainer {

	public GuiWorldTransfer() {
		super(new ContainerWorldTransfer());
	}
	
	@Override
    public void initGui() {
    	super.initGui();
    	this.buttonList.clear();
    	this.labelList.clear();
    	
    	int topX = (this.width - 183) / 2;
        int topY = (this.height - 215) / 2;

        int i = 0;
        for(String name : WorldTransferList.getNameList()) {
        	this.buttonList.add(new GuiSmallButton(i, topX + 135, topY + 50 + i * this.fontRendererObj.FONT_HEIGHT, 30, this.fontRendererObj.FONT_HEIGHT, "----"));
        	i += 1;
        }
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
	    	
		if (button.enabled) {
	         if(button.id >= 0 && button.id < WorldTransferList.getSize()) {
	        	 ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.worldtransfer.gui.delete", WorldTransferList.getName(button.id));
	     		 chatComponent.getChatStyle().setItalic(true);
	     		 this.mc.thePlayer.addChatMessage(chatComponent);
	        	 WorldTransferList.delete(button.id);
	        	 this.mc.setIngameFocus();
	         }
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.worldTransfer);
        int topX = (this.width - 183) / 2;
        int topY = (this.height - 215) / 2;
        this.drawTexturedModalRect(topX, topY, 0, 0, 183, 215);
        
        GL11.glPushMatrix();
		double scale = 1.7D;
		GL11.glScaled(scale, scale, scale);
		this.fontRendererObj.drawStringWithShadow("World Transfer", (int)((topX + 13) / scale), (int)((topY + 13) / scale), -1);
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		scale = 0.8D;
		GL11.glScaled(scale, scale, scale);
		this.fontRendererObj.drawString("Cross-world copy and pasting", (int)((topX + 13) / scale), (int)((topY + 28) / scale), 0);
		GL11.glPopMatrix();
		
		this.fontRendererObj.drawStringWithShadow("Name", topX + 13, topY + 38, 4210752);
		
		this.fontRendererObj.drawStringWithShadow("Block No", topX + 75, topY + 38, 4210752);
		
		this.fontRendererObj.drawStringWithShadow("Delete", topX + 135, topY + 38, 4210752);
		
		int i = 0;
		for(String name : WorldTransferList.getNameList()) {
			this.fontRendererObj.drawStringWithShadow(name, topX + 13, topY + 50 + i * this.fontRendererObj.FONT_HEIGHT, -1);
			this.fontRendererObj.drawStringWithShadow("" + WorldTransferList.getAreaFromName(name).size(), topX + 75, topY + 50 + i * this.fontRendererObj.FONT_HEIGHT, -1);
			//GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			//this.drawRect(topX + 135, topY + 50 + i * this.fontRendererObj.FONT_HEIGHT, topX + 135 + 7, topY + 50 + i * this.fontRendererObj.FONT_HEIGHT + 7, Color.red.getRGB());
			
			//GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			i += 1;
		}
		
	}
}
