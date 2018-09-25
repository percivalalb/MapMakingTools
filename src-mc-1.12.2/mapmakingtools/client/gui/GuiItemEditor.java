package mapmakingtools.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * @author ProPercivalalb
 */
public class GuiItemEditor extends GuiContainer implements IGuiItemEditor {
	
	private static final int BUTTON_ID_START = 100;
	
	private int slotIndex;
	private EntityPlayer player;
	private ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();

	private Hashtable<IItemAttribute, Boolean> itemMap;
	private List<IItemAttribute> itemList;
	private static IItemAttribute itemCurrent;
	private int currentPage = 0;
	private int maxPages = 1;
	
	public GuiItemEditor(EntityPlayer player, int slotIndex) {
		super(new ContainerItemEditor(player, slotIndex));
		this.slotIndex = slotIndex;
		this.player = player;
		this.itemMap = ItemEditorManager.getItems(this.player, this.getStack());
		this.itemList = ItemEditorManager.getItemList(player, this.getStack());
		
		
		if(itemCurrent != null && !itemMap.get(itemCurrent))
			itemCurrent = null;
	}
	
	public ItemStack getStack() {
		return this.player.inventory.getStackInSlot(this.slotIndex);
	}
	
	public ItemStack createNewStack(int data) {
		if(this.getStack().isEmpty())
			return ItemStack.EMPTY;
		
		ItemStack stack = this.getStack();
		
		if(itemCurrent != null) {
    		itemCurrent.onItemCreation(stack, data);
    		itemCurrent.populateFromItem(this, stack, false);
		}
		
		return stack;
	}

	@Override
    public void initGui() {
    	super.initGui();
		this.xSize = this.width - 26;
		this.ySize = this.height - 26;
		int topX = (this.width - this.xSize) / 2;
        int topY = (this.height - this.ySize) / 2;
        this.guiLeft = topX;
        this.guiTop = topY;
    	Keyboard.enableRepeatEvents(true);
    	this.textboxList.clear();
    	this.buttonList.clear();
    	this.labelList.clear();
    	
    	int size = this.itemList.size();
    	int perPage = Math.max(MathHelper.floor((this.ySize - 25) / 15D), 1);
    	
    	if(perPage < size) {
    		this.buttonList.add(new GuiButton(98, topX + 32, topY + 7, 20, 20, "<"));
    	    this.buttonList.add(new GuiButton(99, topX + 55, topY + 7, 20, 20, ">"));
    	}
    	
		this.maxPages = MathHelper.ceil(size / (double)perPage);
		
		if(this.currentPage >= this.maxPages)
    		this.currentPage = 0;
		
    	for(int i = 0; i < perPage; ++i) {
    		int index = this.currentPage * perPage + i;
    		if(index >= size) continue;
    		IItemAttribute item = this.itemList.get(index);
    		GuiButtonSmall button = new GuiButtonSmall(BUTTON_ID_START + index, topX + 5, topY + 30 + i * 15, 80, 15, item.getAttributeName());
    		button.enabled = this.itemMap.get(item);
    		this.buttonList.add(button);
    	}
    	
    	if(itemCurrent != null) {
    		itemCurrent.initGui(this, this.getStack(), 150, this.guiTop, this.guiLeft + this.xSize - 150, this.ySize);
    		itemCurrent.populateFromItem(this, this.getStack(), true);
    	}
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.mc.getTextureManager().bindTexture(ResourceLib.ITEM_EDITOR_SLOT);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 35, 35);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        this.mc.fontRenderer.drawString("Slot: " + slotIndex, this.guiLeft + 32, this.guiTop + 21, 0);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);

        for(GuiTextField field : this.textboxList)
        	field.drawTextBox();
        
        Gui.drawRect(150, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, new Color(255, 255, 255, 75).getRGB());
        
        if(itemCurrent != null) {
        	this.fontRenderer.drawString(itemCurrent.getAttributeName(), 152, this.guiTop + 2, 1);
        	itemCurrent.drawInterface(this, 150, this.guiTop, this.guiLeft + this.xSize - 150, this.ySize);
        	itemCurrent.drawGuiContainerBackgroundLayer(this, partialTicks, mouseX, mouseY);
        }
        
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		super.drawGuiContainerForegroundLayer(xMouse, yMouse);
		
		if(itemCurrent != null) {
			itemCurrent.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
			
			GlStateManager.translate((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
			itemCurrent.drawToolTips(this, xMouse, yMouse);
			GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
		}
	}
	
	@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) 
			field.updateCursorCounter();
		
		if(this.getStack().isEmpty()) {
			//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("itemeditor.closeNoItem"));
            this.mc.player.closeScreen();
		}
		
		if(itemCurrent != null)
			itemCurrent.updateScreen(this);
	}
	
	@Override
	public void mouseClicked(int xMouse, int yMouse, int mouseButton) throws IOException {	

		//for(GuiTextField field : this.textboxList)
		//	if(field instanceof GuiColourTextField)
		//		if(((GuiColourTextField)field).preMouseClick(xMouse, yMouse, mouseButton))
		//			return;
		
		super.mouseClicked(xMouse, yMouse, mouseButton);
		
		for(GuiTextField field : this.textboxList)
			field.mouseClicked(xMouse, yMouse, mouseButton);
		
		if(itemCurrent != null)
			itemCurrent.mouseClicked(this, xMouse, yMouse, mouseButton);
	}
	
	@Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 98) {
        		if(this.currentPage > 0) {
        			--currentPage;
        			this.initGui();
        		}
			}
        	else if(button.id == 99) {
        		if(this.currentPage < this.maxPages - 1) {
        			++currentPage;
        			this.initGui();
        		}
			}
			
        	else if(button.id >= BUTTON_ID_START && button.id < BUTTON_ID_START + this.itemList.size()) {
				itemCurrent = this.itemList.get(button.id - BUTTON_ID_START);
				this.initGui();
			}
		}
		
		if(itemCurrent != null)
			itemCurrent.actionPerformed(this, button);
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.player.closeScreen();
            return;
		}
		
		for(GuiTextField field : this.textboxList)
			if(field.textboxKeyTyped(typedChar, keyCode))
				if(itemCurrent != null)
					itemCurrent.textboxKeyTyped(this, typedChar, keyCode, field);
		
		if(itemCurrent != null)
			itemCurrent.keyTyped(this, typedChar, keyCode);
	}

	@Override
	public List<GuiLabel> getLabelList() {
		return this.labelList;
	}

	@Override
	public List<GuiButton> getButtonList() {
		return this.buttonList;
	}

	@Override
	public List<GuiTextField> getTextBoxList() {
		return this.textboxList;
	}
	
	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
	
	@Override
	public void sendUpdateToServer(int data) {
		PacketDispatcher.sendToServer(new PacketItemEditorUpdate(this.createNewStack(data), this.slotIndex));
	}
}
