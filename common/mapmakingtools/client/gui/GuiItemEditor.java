package mapmakingtools.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.ScrollMenu;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;

public class GuiItemEditor extends GuiContainer {
	
	private int slotNo;
	private EntityPlayer player;
	private ScaledResolution resolution;
	private ArrayList<GuiTextField> textList = new ArrayList<GuiTextField>();
	
	private static GuiTextField itemNameField;
	private static GuiTextField itemDamageField;
	private static GuiListSlot scrollList;
	
	public GuiItemEditor(EntityPlayer player, int slotNo) {
		super(new ContainerItemEditor(player, slotNo));
		this.slotNo = slotNo;
		this.player = player;
	}
	
	public ItemStack getStack() {
		return player.inventory.getStackInSlot(slotNo);
	}
	
	public ItemStack createNewStack() {
		if(getStack() == null)
			return null;
		ItemStack stack = getStack().copy();
		stack.setItemName(MapMakingTools.sectionSign + "r" + itemNameField.getText());
		try {
			stack.setItemDamage(Integer.valueOf(itemDamageField.getText()));
		}
		catch(Exception e) {}
		return stack;
	}

	@Override
    public void initGui() {
		this.resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.xSize = this.width - 6;
		this.ySize = this.height - 26;
    	super.initGui();
    	this.textList.clear();
    	this.itemNameField = new GuiTextField(mc.fontRenderer, this.guiLeft + 40, this.guiTop + 18, 80, 12) {
    		@Override
    		public boolean textboxKeyTyped(char par1, int par2) {
    	    	boolean bool = super.textboxKeyTyped(par1, par2);
    	    	PacketItemEditorUpdate packet = new PacketItemEditorUpdate(createNewStack(), slotNo);
    	    	PacketTypeHandler.populatePacketAndSendToServer(packet);
    	    	packet.execute(null, player);
    	    	return bool;
    	    }
    	};
    	this.itemDamageField = new GuiTextField(mc.fontRenderer, this.guiLeft + 130, this.guiTop + 18, 80, 12) {
    		@Override
    		public boolean textboxKeyTyped(char par1, int par2) {
    	    	boolean bool = super.textboxKeyTyped(par1, par2);
    	    	PacketItemEditorUpdate packet = new PacketItemEditorUpdate(createNewStack(), slotNo);
    	    	PacketTypeHandler.populatePacketAndSendToServer(packet);
    	    	packet.execute(null, player);
    	    	return bool;
    	    }
    	};
    	this.scrollList = new GuiListSlot(this);
    	this.itemDamageField.setMaxStringLength(7);
    	
    	this.itemNameField.setFocused(false);
    	this.itemDamageField.setFocused(false);
    	
    	this.textList.add(itemNameField);
    	this.textList.add(itemDamageField);
    	//this.scrollList.registerScrollButtons(7, 8);
    	
    	ItemStack stack = getStack();
    	if(stack != null) {
    		String name = stack.getDisplayName();
    		if(name.startsWith(MapMakingTools.sectionSign + "r")) 
    			name = name.substring(2, name.length());
    		this.itemNameField.setText(name);
    		this.itemDamageField.setText("" + stack.getItemDamage());
    	}
    }
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glScaled(getScaleFactor(), getScaleFactor(), getScaleFactor());
		super.drawScreen(par1, par2, par3);
		this.scrollList.drawScreen(par1, par2, par3);
		GL11.glPopMatrix();
    }
	
	@Override
	public void drawDefaultBackground() {
		this.drawGradientRect(0, 0, (int)(this.width / this.getScaleFactor()), (int)(this.height / this.getScaleFactor()), -1072689136, -804253680);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.getTextureManager().bindTexture(ResourceReference.itemEditor);
        int guiXCentre = this.width / 2;
        int guiYCentre = this.height / 2;
        //this.drawTexturedModalRect(guiXCentre - 175 / 2, guiYCentre - 132 / 2, 0, 0, 175, 132);
        
        this.mc.getTextureManager().bindTexture(ResourceReference.itemEditorSlot);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 35, 35);
        GL11.glScaled(0.5F, 0.5F, 0.5F);
        this.mc.fontRenderer.drawString("Slot: " + slotNo, 25, 34, 0);
        GL11.glScaled(2.0F, 2.0F, 2.0F);
        
        this.mc.fontRenderer.drawString("Item Name", 60, 20, -1);
        this.mc.fontRenderer.drawString("Item Damage", 145, 20, -1);
        for(GuiTextField field : textList) {
        	field.drawTextBox();
        }
    }

	public float getScaleFactor() {
		int scale = resolution.getScaleFactor();
		if(scale == 1)
			return 1F;
		if(scale == 2)
			return 1F;
		if(scale == 3)
			return 1F / 3F * 2F;
		return 1F;
	}
	
	@Override
	public void updateScreen() {
		for(GuiTextField field : textList) {
			field.updateCursorCounter();
		}
		if(player.inventory.getStackInSlot(slotNo) == null) {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("itemeditor.closeNoItem"));
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
		}
		if(!player.capabilities.isCreativeMode) {
			player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.creativeModeNeed"));
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
		}
	}
	
	@Override
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) {	
		xMouse = (int)(xMouse / this.getScaleFactor());
		yMouse = (int)(yMouse / this.getScaleFactor());
		super.mouseClicked(xMouse, yMouse, mouseButton);
		for(GuiTextField field : textList) {
			field.mouseClicked(xMouse, yMouse, mouseButton);
		}
	}
	
	@Override
	protected boolean isPointInRegion(int par1, int par2, int par3, int par4, int xMouse, int yMouse) {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        xMouse = (int)(xMouse / this.getScaleFactor());
		yMouse = (int)(yMouse / this.getScaleFactor());
        xMouse -= k1;
        yMouse -= l1;
        return xMouse >= par1 - 1 && xMouse < par1 + par3 + 1 && yMouse >= par2 - 1 && yMouse < par2 + par4 + 1;
    }
	
	@Override
	public void keyTyped(char var1, int var2) {
		
		if (var2 == Keyboard.KEY_ESCAPE) {
    		this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            return;
    	}
		for(GuiTextField field : textList) {
			field.textboxKeyTyped(var1, var2);
		}
	}
	
	protected void drawHoveringText(List par1List, int xMouse, int yMouse, FontRenderer font) {
		if (!par1List.isEmpty()) {
			xMouse = (int)(xMouse / this.getScaleFactor());
			yMouse = (int)(yMouse / this.getScaleFactor());
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	        RenderHelper.disableStandardItemLighting();
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        int k = 0;
	        Iterator iterator = par1List.iterator();

	        while (iterator.hasNext()) {
	            String s = (String)iterator.next();
	            int l = font.getStringWidth(s);

	            if (l > k) {
	                k = l;
	            }
	        }

	        int i1 = xMouse + 12;
	        int j1 = yMouse - 12;
	        int k1 = 8;

	        if (par1List.size() > 1) {
	                k1 += 2 + (par1List.size() - 1) * 10;
	            }

	        if (i1 + k > this.width) {
	            i1 -= 28 + k;
	        }

	            if (j1 + k1 + 6 > this.height)
	            {
	                j1 = this.height - k1 - 6;
	            }

	            this.zLevel = 300.0F;
	            itemRenderer.zLevel = 300.0F;
	            int l1 = -267386864;
	            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
	            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
	            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
	            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
	            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
	            int i2 = 1347420415;
	            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
	            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
	            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
	            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
	            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

	            for (int k2 = 0; k2 < par1List.size(); ++k2)
	            {
	                String s1 = (String)par1List.get(k2);
	                font.drawStringWithShadow(s1, i1, j1, -1);

	                if (k2 == 0)
	                {
	                    j1 += 2;
	                }

	                j1 += 10;
	            }

	            this.zLevel = 0.0F;
	            itemRenderer.zLevel = 0.0F;
	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_DEPTH_TEST);
	            RenderHelper.enableStandardItemLighting();
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        }
	    }
}
