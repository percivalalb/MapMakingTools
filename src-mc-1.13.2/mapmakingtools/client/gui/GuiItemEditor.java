package mapmakingtools.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.inventory.ContainerItemEditor;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class GuiItemEditor extends GuiContainer implements IGuiItemEditor {
	
	private static final int BUTTON_ID_START = 100;
	
	private int slotIndex;
	private EntityPlayer player;
	private ArrayList<GuiTextField> textfields = new ArrayList<GuiTextField>();

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
        this.mc.keyboardListener.enableRepeatEvents(true);
    	this.textfields.clear();
    	this.buttons.clear();
    	this.labels.clear();
    	
    	int size = this.itemList.size();
    	int perPage = Math.max(MathHelper.floor((this.ySize - 25) / 15D), 1);
    	
    	if(perPage < size) {
    		this.addButton(new GuiButton(98, topX + 32, topY + 7, 20, 20, "<") {
        		@Override
    			public void onClick(double mouseX, double mouseY) {
        			if(currentPage > 0) {
            			--currentPage;
            			initGui();
            		}
        		}
        	});
    	    this.addButton(new GuiButton(99, topX + 55, topY + 7, 20, 20, ">") {
        		@Override
    			public void onClick(double mouseX, double mouseY) {
        			if(currentPage < maxPages - 1) {
            			++currentPage;
            			initGui();
            		}
        		}
        	});
    	}
    	
		this.maxPages = MathHelper.ceil(size / (double)perPage);
		
		if(this.currentPage >= this.maxPages)
    		this.currentPage = 0;
		
    	for(int i = 0; i < perPage; ++i) {
    		int index = this.currentPage * perPage + i;
    		if(index >= size) continue;
    		IItemAttribute item = this.itemList.get(index);
    		GuiButtonSmall button = new GuiButtonSmall(BUTTON_ID_START + index, topX + 5, topY + 30 + i * 15, 80, 15, item.getAttributeName()) {
        		@Override
    			public void onClick(double mouseX, double mouseY) {
        			itemCurrent = itemList.get(index);
    				initGui();
        		}
        	};
    		button.enabled = this.itemMap.get(item);
    		this.addButton(button);
    	}
    	
    	if(itemCurrent != null) {
    		itemCurrent.initGui(this, this.getStack(), 150, this.guiTop, this.guiLeft + this.xSize - 150, this.ySize);
    		itemCurrent.populateFromItem(this, this.getStack(), true);
    	}
    	
    	this.children.addAll(this.textfields);
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.mc.getTextureManager().bindTexture(ResourceLib.ITEM_EDITOR_SLOT);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 35, 35);
        GlStateManager.scalef(0.5F, 0.5F, 0.5F);
        this.mc.fontRenderer.drawString("Slot: " + slotIndex, this.guiLeft + 32, this.guiTop + 21, 0);
        GlStateManager.scalef(2.0F, 2.0F, 2.0F);

        for(GuiTextField field : this.textfields)
        	field.drawTextField(mouseX, mouseY, partialTicks);
        
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
			
			GlStateManager.translatef((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
			itemCurrent.drawToolTips(this, xMouse, yMouse);
			GlStateManager.translatef((float)this.guiLeft, (float)this.guiTop, 0.0F);
		}
	}
	
	@Override
	public void tick() {
		for(GuiTextField field : this.textfields) 
			field.tick();
		
		if(this.getStack().isEmpty()) {
			//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("itemeditor.closeNoItem"));
            this.mc.player.closeScreen();
		}
		
		if(itemCurrent != null)
			itemCurrent.updateScreen(this);
	}
    /**
	@Override
	public void mouseClicked(double mouseX, double mouseY, int mouseButton) throws IOException {	

		//for(GuiTextField field : this.textfields)
		//	if(field instanceof GuiColourTextField)
		//		if(((GuiColourTextField)field).preMouseClick(mouseX, mouseY, mouseButton))
		//			return;
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for(GuiTextField field : this.textfields)
			field.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(itemCurrent != null)
			itemCurrent.mouseClicked(this, mouseX, mouseY, mouseButton);
	}
	**/
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClicked(mouseX, mouseY, mouseButton)) {
        	return true;
        }
        
        if(itemCurrent != null)
			itemCurrent.mouseClicked(this, mouseX, mouseY, mouseButton);
       
        for(GuiTextField textField : this.textfields) {
        	if(textField.mouseClicked(mouseX, mouseY, mouseButton)) {
        		return true;
        	}
        }
        
        return false;
    }
	
	@Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        this.mc.keyboardListener.enableRepeatEvents(false);
    }
	/**
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.player.closeScreen();
            return;
		}
		
		for(GuiTextField field : this.textfields)
			if(field.textboxKeyTyped(typedChar, keyCode))
				if(itemCurrent != null)
					itemCurrent.textboxKeyTyped(this, typedChar, keyCode, field);
		
		if(itemCurrent != null)
			itemCurrent.keyTyped(this, typedChar, keyCode);
	}**/

	@Override
	public List<GuiLabel> getLabelList() {
		return this.labels;
	}

	@Override
	public List<GuiButton> getButtonList() {
		return this.buttons;
	}

	@Override
	public List<GuiTextField> getTextBoxList() {
		return this.textfields;
	}
	
	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
	
	@Override
	public void sendUpdateToServer(int data) {
		PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketItemEditorUpdate(this.createNewStack(data), this.slotIndex));
	}
	
	@Override
	public GuiButton addButtonToGui(GuiButton buttonIn) {
		return this.addButton(buttonIn);
	}
	
	@Override
	public GuiTextField addTextFieldToGui(GuiTextField fieldIn) {
		this.textfields.add(fieldIn);
		this.children.add(fieldIn);
		return fieldIn;
	}
	
	@Override
	public GuiLabel addLabelToGui(GuiLabel labelIn) {
		this.labels.add(labelIn);
		this.children.add(labelIn);
		return labelIn;
	}

	@Override
	public <T extends IGuiEventListener> T addListenerToGui(T listenerIn) {
		this.children.add(listenerIn);
		return listenerIn;
	}
}
