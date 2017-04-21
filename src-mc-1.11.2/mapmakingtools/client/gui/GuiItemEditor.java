package mapmakingtools.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.container.ContainerItemEditor;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * @author ProPercivalalb
 */
public class GuiItemEditor extends GuiContainer implements IGuiItemEditor {
	
	private int slotIndex;
	private EntityPlayer player;
	private ScaledResolution resolution;
	private ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();

	private Hashtable<IItemAttribute, Boolean> itemMap;
	private List<IItemAttribute> itemList;
	private static IItemAttribute itemCurrent;
	private int currentPage = 1;
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
		if(getStack() == null)
			return null;
		ItemStack stack = getStack();
		
		
		if(itemCurrent != null)
    		itemCurrent.onItemCreation(stack, data);
		
		if(itemCurrent != null)
    		itemCurrent.populateFromItem(this, stack, false);
		
		return stack;
	}

	@Override
    public void initGui() {
    	super.initGui();
		this.resolution = new ScaledResolution(this.mc);
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
    	
    	int temp = 0;
    	while((temp + 1) * 15 + topY + 30 < this.guiTop + this.ySize)
    		temp += 1;
    	
    	if(temp < size) {
    		size = temp;
    		this.buttonList.add(new GuiButton(98, topX + 32, topY + 7, 20, 20, "<"));
    	    this.buttonList.add(new GuiButton(99, topX + 55, topY + 7, 20, 20, ">"));
    	}
    	
    	if(size < 1)
    		size = 1;
    	
		this.maxPages = MathHelper.ceil(this.itemList.size() / (double)size);
		
		if(this.currentPage > this.maxPages)
    		this.currentPage = 1;
		
    	for(int i = 0; i < size; ++i) {
    		if(((this.currentPage - 1) * size + i) >= this.itemList.size())
    			continue;
    		IItemAttribute item = this.itemList.get((this.currentPage - 1) * size + i);
    		GuiSmallButton button = new GuiSmallButton(100 + (this.currentPage - 1) * size + i, topX + 5, topY + 30 + i * 15, 80, 15, item.getAttributeName());
    		button.enabled = this.itemMap.get(item);
    		this.buttonList.add(button);
    	}
    	
    	if(itemCurrent != null)
    		itemCurrent.initGui(this, this.getStack(), 150, this.guiTop, this.guiLeft + this.xSize - 150, this.ySize);
    	
    	if(itemCurrent != null)
    		itemCurrent.populateFromItem(this, this.getStack(), true);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.getTextureManager().bindTexture(ResourceReference.itemEditor);
        int guiXCentre = this.width / 2;
        int guiYCentre = this.height / 2;
        this.xSize = this.width - 26;
		this.ySize = this.height - 26;
		int topX = (this.width - this.xSize) / 2;
        int topY = (this.height - this.ySize) / 2;
        //this.drawTexturedModalRect(guiXCentre - 175 / 2, guiYCentre - 132 / 2, 0, 0, 175, 132);
        
        this.mc.getTextureManager().bindTexture(ResourceReference.itemEditorSlot);
        this.drawTexturedModalRect(topX, topY, 0, 0, 35, 35);
        GL11.glScaled(0.5F, 0.5F, 0.5F);
        this.mc.fontRendererObj.drawString("Slot: " + slotIndex, topX + 32, topY + 21, 0);
        GL11.glScaled(2.0F, 2.0F, 2.0F);

        for(GuiTextField field : this.textboxList)
        	field.drawTextBox();
        
        this.drawRect(150, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, new Color(255, 255, 255, 75).getRGB());
        
        if(itemCurrent != null)
        	itemCurrent.drawInterface(this, 150, this.guiTop, this.guiLeft + this.xSize - 150, this.ySize);
        
        if(itemCurrent != null)
        	itemCurrent.drawGuiContainerBackgroundLayer(this, partialTicks, mouseX, mouseY);
        
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		super.drawGuiContainerForegroundLayer(xMouse, yMouse);
		
		if(itemCurrent != null)
			itemCurrent.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		
        //for(GuiTextField field : this.textboxList)
        //	field.drawToolTip(xMouse, yMouse);
	}
	
	@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) 
			field.updateCursorCounter();
		
		if(this.getStack() == null) {
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
		if (button.enabled) {
			switch(button.id) {
			case 98:
        		if(this.currentPage > 1) {
        			--currentPage;
        			this.initGui();
        		}
        		break;
        	case 99:
        		if(this.currentPage < maxPages) {
        			++currentPage;
        			this.initGui();
        		}
        		break;
			default:
				break;
			}
			
			if(button.id >= 100 && button.id < 100 + this.itemList.size()) {
				this.itemCurrent = this.itemList.get(button.id - 100);
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
	public List getLabelList() {
		return this.labelList;
	}

	@Override
	public List getButtonList() {
		return this.buttonList;
	}

	@Override
	public List getTextBoxList() {
		return this.textboxList;
	}
	
	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}
	
	@Override
	public void sendUpdateToServer(int data) {
		PacketDispatcher.sendToServer(new PacketItemEditorUpdate(this.createNewStack(data), this.slotIndex));
	}
}
