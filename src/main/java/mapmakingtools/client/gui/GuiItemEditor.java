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
import mapmakingtools.client.gui.button.GuiAdvancedTextField;
import mapmakingtools.client.gui.textfield.GuiColourTextField;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.lib.Symbols;
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
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class GuiItemEditor extends GuiContainer {
	
	private int slotIndex;
	private EntityPlayer player;
	private ScaledResolution resolution;
	private ArrayList<GuiColourTextField> textList = new ArrayList<GuiColourTextField>();
	
	private static GuiColourTextField itemNameField;
	private static GuiColourTextField itemDamageField;
	//private static GuiListSlot scrollList;
	
	public GuiItemEditor(EntityPlayer player, int slotIndex) {
		super(new ContainerItemEditor(player, slotIndex));
		this.slotIndex = slotIndex;
		this.player = player;
	}
	
	public ItemStack getStack() {
		return player.inventory.getStackInSlot(this.slotIndex);
	}
	
	public ItemStack createNewStack() {
		if(getStack() == null)
			return null;
		ItemStack stack = getStack();
		stack.setStackDisplayName(EnumChatFormatting.RESET + itemNameField.getText());
		
		if(NumberParse.isInteger(this.itemDamageField.getText()))
			stack.setItemDamage(NumberParse.getInteger(itemDamageField.getText()));

		return stack;
	}

	@Override
    public void initGui() {
    	super.initGui();
		this.resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		this.xSize = this.width - 6;
		this.ySize = this.height - 26;
		int topX = (this.width - this.xSize) / 2;
        int topY = (this.height - this.ySize) / 2;
    	Keyboard.enableRepeatEvents(true);
    	this.textList.clear();
    	this.itemNameField = new GuiColourTextField(mc.fontRenderer, this.guiLeft + 40, this.guiTop + 18, 80, 12) {
    		@Override
    		public void textChange() {
    			MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketItemEditorUpdate(createNewStack(), slotIndex));
    		}
    	};
    	this.itemDamageField = new GuiColourTextField(mc.fontRenderer, this.guiLeft + 130, this.guiTop + 18, 80, 12) {
    		@Override
    		public void textChange() {
    			MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketItemEditorUpdate(createNewStack(), slotIndex));
    		}
    	};
    	//this.scrollList = new GuiListSlot(this);
    	this.itemDamageField.setMaxStringLength(7);
    	
    	this.itemNameField.setFocused(false);
    	this.itemDamageField.setFocused(false);
    	
    	this.textList.add(itemNameField);
    	this.textList.add(itemDamageField);
    	//this.scrollList.registerScrollButtons(7, 8);
    	
    	ItemStack stack = getStack();
    	if(stack != null) {
    		String name = stack.getDisplayName();
    		if(name.startsWith(EnumChatFormatting.RESET.toString())) 
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
		//this.scrollList.drawScreen(par1, par2, par3);
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
        this.mc.fontRenderer.drawString("Slot: " + slotIndex, 25, 34, 0);
        GL11.glScaled(2.0F, 2.0F, 2.0F);
        
        this.mc.fontRenderer.drawString("Item Name", 60, 20, -1);
        this.mc.fontRenderer.drawString("Item Damage", 145, 20, -1);
        for(GuiAdvancedTextField field : textList) {
        	field.drawTextBox();
        }
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		super.drawGuiContainerForegroundLayer(xMouse, yMouse);
        for(GuiColourTextField field : textList)
        	field.drawToolTip(xMouse, yMouse);
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
		for(GuiAdvancedTextField field : textList) {
			field.updateCursorCounter();
		}
		if(this.getStack() == null) {
			//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("itemeditor.closeNoItem"));
            this.mc.setIngameFocus();
		}
	}
	
	@Override
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) {	
		xMouse = (int)(xMouse / this.getScaleFactor());
		yMouse = (int)(yMouse / this.getScaleFactor());
		for(GuiColourTextField field : textList) {
			if(field.preMouseClick(xMouse, yMouse, mouseButton)) {
				return;
			}
		}
		
		super.mouseClicked(xMouse, yMouse, mouseButton);
		for(GuiAdvancedTextField field : textList) {
			field.mouseClicked(xMouse, yMouse, mouseButton);
		}
	}
	
	@Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	protected boolean func_146978_c(int par1, int par2, int par3, int par4, int xMouse, int yMouse) {
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
            this.mc.setIngameFocus(); 
            return;
		}
		
		for(GuiAdvancedTextField field : textList) {
			field.textboxKeyTyped(var1, var2);
		}
	}
}
