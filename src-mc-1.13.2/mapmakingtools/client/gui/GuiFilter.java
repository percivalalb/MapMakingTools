package mapmakingtools.client.gui;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.button.GuiHorizontalTab;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.network.packet.PacketSelectedFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class GuiFilter extends GuiContainer implements IFilterGui {

	public static final int FAKE_HEIGHT = 100;
	public static final int FAKE_WIDTH = 240;
	
	//Extra data the class holds
	public EntityPlayer player;
    public BlockPos pos;
    public int entityId;
	public TargetType mode;
	
	private List<FilterClient> filterList;
	private static FilterClient filter;
	
	private static int currentPage = 0;
	private int maxPages = 1;
	
	private List<GuiTextField> textfields = new ArrayList<>();
	
	private GuiFilter(List<FilterClient> filters, EntityPlayer player) {
		super(new ContainerFilter(FilterManager.getServerFiltersFromList(filters), player));
		this.xSize = FAKE_WIDTH + 31 * 2; // This stops items being dropped as it covers the tabs
        this.ySize = FAKE_HEIGHT;
        this.filterList = filters;
        this.player = player;
        this.maxPages = MathHelper.ceil(this.filterList.size() / 6D);
        
        //If the last selected filter is not in this new gui reset it
        if(!this.filterList.contains(filter)) {
        	filter = null;
        	currentPage = 0;
        }
        else {
        	int index = this.filterList.indexOf(filter);
        	PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketSelectedFilter(index));
        	this.getContainerFilter().setSelected(index);
        }
	}
	
	public GuiFilter(List<FilterClient> filters, EntityPlayer player, BlockPos pos) {
		this(filters, player);
		this.pos = pos;
	    this.mode = TargetType.BLOCK;
	    this.getContainerFilter().setBlockPos(pos);
	}
	    
	public GuiFilter(List<FilterClient> filters, EntityPlayer player, int entityId) {
		this(filters, player);
	    this.entityId = entityId;
	    this.mode = TargetType.ENTITY;
	    this.getContainerFilter().setEntityId(entityId);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int topXF = (this.width - FAKE_WIDTH) / 2;
        int topY = (this.height - this.ySize) / 2;
        
		if(filter == null || !filter.drawBackground(this)) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_SMALL);
			this.drawTexturedModalRect(topXF, topY, 0, 0, FAKE_WIDTH, FAKE_HEIGHT);
		}
		
		if(filter != null) {
			filter.drawGuiContainerBackgroundLayer(this, partialTicks, mouseX, mouseY);
			this.fontRenderer.drawString(filter.getFilterName(), topXF - this.fontRenderer.getStringWidth(filter.getFilterName()) / 2 + FAKE_WIDTH / 2, topY + 6, 1);
		}
		else {
			GlStateManager.pushMatrix();
			double scale = 1.7D;
			GlStateManager.scaled(scale, scale, scale);
			this.fontRenderer.drawString("Minecraft Filters", (int)((topXF + 10) / scale), (int)((topY + 15) / scale), 0);
			GlStateManager.scaled(0.588D, 0.588D, 0.588D);
			GlStateManager.popMatrix();
		}
		
		for(GuiTextField field : this.textfields) 
			field.drawTextField(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		int topXF = (this.width - FAKE_WIDTH) / 2;
        int topY = (this.height - this.ySize) / 2;
        
		if(filter != null)
			filter.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		
		GlStateManager.translatef((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
		for(GuiButton button : this.buttons) {
			
    		if(button instanceof GuiHorizontalTab) {
        		GuiHorizontalTab tabButton = (GuiHorizontalTab)button;
        		if(tabButton.isMouseOver()) {
        			
        			List<String> list = new ArrayList<String>();
        			list.add(tabButton.filter.getFilterName());
        			if(tabButton.filter.showErrorIcon(this)) {
        				String errorMessage = tabButton.filter.getErrorMessage(this);
        				if(errorMessage != null)
        					list.add(errorMessage);
        			}
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		else if(button.id == 156) {
    			GuiButtonSmall smallButton = (GuiButtonSmall)button;
        		if(smallButton.isMouseOver()) {
        			List<String> list = filter.getFilterInfo(this);
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		//if(listBt instanceof GuiButtonCancel) {
    		//	GuiButtonCancel tab = (GuiButtonCancel)listBt;
        	//	if(tab.isMouseAbove(xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList(I18n.format("gui.cancel"));
        	//		this.drawHoveringText(list, mouseX, mouseY, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 148) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Prev. Page");
        	//		this.drawHoveringText(list, mouseX, mouseY, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 149) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Next. Page");
        	//		this.drawHoveringText(list, mouseX, mouseY, this.mc.fontRenderer);
        	//	}
    		//}
    	}
		if(filter != null)
			filter.drawToolTips(this, xMouse, yMouse);
		
		GlStateManager.translatef((float)this.guiLeft, (float)this.guiTop, 0.0F);
    }

	@Override
    public void initGui() {
    	super.initGui();
    	
    	this.mc.keyboardListener.enableRepeatEvents(true);
    	this.textfields.clear();
    	this.buttons.clear();
    	this.labels.clear();
    	
    	this.setYSize(FAKE_HEIGHT);
    	
    	int topXF = (this.width - FAKE_WIDTH) / 2;
        int topYF = (this.height - FAKE_HEIGHT) / 2;

        for(int i = 0; i < 3; ++i) {
        	int tabIndex = currentPage == -1 ? 0 : currentPage * 6;
        	int y = topYF + 7 + i * 29;
        	
        	if(this.filterList.size() > tabIndex + i) {
        		this.addButton(new GuiHorizontalTab(150 + i, topXF - (30 - 1), y, GuiHorizontalTab.Side.LEFT, filterList.get(tabIndex + i), this) {
            		@Override
        			public void onClick(double mouseX, double mouseY) {
                    	unSelectBut(this);
            		}
            	});
        	}
        	
        	if(this.filterList.size() > tabIndex + i + 3) {
             	this.addButton(new GuiHorizontalTab(153 + i, topXF + FAKE_WIDTH - 1, y, GuiHorizontalTab.Side.RIGHT, filterList.get(tabIndex + i + 3), this) {
            		@Override
        			public void onClick(double mouseX, double mouseY) {
                    	unSelectBut(this);
            		}
            	});
            }
        }

        if(this.filterList.size() > 6) {
        	this.addButton(new GuiButton(148, topXF + FAKE_WIDTH + 3,  topYF - 15, 20, 20, "<") {
        		@Override
    			public void onClick(double mouseX, double mouseY) {
        			if(currentPage > 0) {
            			--currentPage;
            			initGui();
            		}
        		}
        	});
        	this.addButton(new GuiButton(149, topXF + FAKE_WIDTH + 26, topYF - 15, 20, 20, ">") {
        		@Override
    			public void onClick(double mouseX, double mouseY) {
        			if(currentPage < maxPages - 1) {
            			++currentPage;
            			initGui();
            		}
        		}
        	});
        }

        if(filter != null)
        	filter.initGui(this);
        else {}
        	//this.buttonList.add(new GuiButtonCancel(this, -1, k + 210, l + 70, 112, 220)); //cancel
        
        int topY = (this.height - this.ySize) / 2;
        
        if(filter != null) {
        	if(filter.getFilterInfo(this) != null)
        		this.addButton(new GuiButtonSmall(156, topXF + 5, topY + 4, 13, 12, "?") {
            		@Override
        			public void onClick(double mouseX, double mouseY) {
            			
            		}
            	});
        
        	if(filter.hasUpdateButton(this))
        		this.addButton(new GuiButtonSmall(157, topXF + 20, topY + 4, 13, 12, "v") {
            		@Override
        			public void onClick(double mouseX, double mouseY) {
            			filter.updateButtonClicked(GuiFilter.this);
            		}
            	});
        }
	
        for(int i = 0; i < this.buttons.size(); ++i) {
    		if(this.buttons.get(i) instanceof GuiHorizontalTab) {
        		GuiHorizontalTab tab = (GuiHorizontalTab)(GuiButton)this.buttons.get(i);
        		if(tab.filter == filter) {
        			int index = this.filterList.indexOf(filter);
        			this.getContainerFilter().setSelected(index);
        			PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketSelectedFilter(index));

        			tab.isSelected = true;
        		}
    		}
        }
        
    	this.children.addAll(this.textfields);
	}

	@Override
    public void tick() {
    	if(filter != null)
    		filter.updateScreen(this);
    	
    	for(GuiTextField textField : this.textfields)
    		textField.tick();
    }
	
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
    	this.mc.keyboardListener.enableRepeatEvents(false);
    }
    
    /**
    @return 
     * @Override
	public void actionPerformed(GuiButton button) {
        if(filter != null)
        	filter.actionPerformed(this, button);
    	
    	if (button.enabled) {
            switch (button.id) {
            	case -1:
            		ClientHelper.getClient().player.closeScreen();
            		break;
            
            	case 148:
            		if(currentPage > 0) {
            			--currentPage;
            			this.initGui();
            		}
            		break;
            	case 149:
            		if(currentPage < maxPages - 1) {
            			++currentPage;
            			this.initGui();
            		}
            		break;
                case 150:
                case 151:
                case 152:
                case 153:
                case 154:
                case 155:
                	if(button instanceof GuiHorizontalTab)
                		unSelectBut((GuiHorizontalTab)button);
                	break;
                case 157:
                	filter.updateButtonClicked(this);
                default:
            }
        }
    }
    
    @Override
    protected void keyTyped(char cha, int charIndex) {
        if(filter != null)
        	filter.keyTyped(this, cha, charIndex);
        
        if(filter == null || filter.doClosingKeysWork(this, cha, charIndex))
        	if (charIndex == Keyboard.KEY_ESCAPE)
        		ClientHelper.getClient().player.closeScreen();
        
        for(GuiTextField textField : this.textfields)
        	textField.textboxKeyTyped(cha, charIndex);
    }**/
    
    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClicked(mouseX, mouseY, mouseButton)) {
        	return true;
        }
        
        if(filter != null)
        	filter.mouseClicked(this, mouseX, mouseY, mouseButton);
       
        for(GuiTextField textField : this.textfields) {
        	if(textField.mouseClicked(mouseX, mouseY, mouseButton)) {
        		return true;
        	}
        }
        
        return false;
    }
    
    public ContainerFilter getContainerFilter() {
    	return ((ContainerFilter)this.inventorySlots);
    }
    
    public void unSelectBut(GuiHorizontalTab button) {
    	filter = button.filter;
    	this.initGui();
    	for(int i = 0; i < this.buttons.size(); ++i) {
    		GuiButton listBt = (GuiButton)this.buttons.get(i);
    		if(listBt.id == button.id) {
    			if(listBt instanceof GuiHorizontalTab)
    				((GuiHorizontalTab)listBt).isSelected = true;
    		}
    	}
    }
    
    ///////////////////////////////  IGuiFilter overridden methods  ///////////////////////////////
    
    // Size of the main menu that tabs positions are based off
    @Override
    public int xFakeSize() {
		return FAKE_WIDTH;
	}
    
	@Override
	public int getScreenWidth() {
		return this.width;
	}

	@Override
	public int getScreenHeight() {
		return this.height;
	}
	
	@Override
	public int getGuiY() {
		return this.guiTop;
	}

	@Override
	public int getGuiX() {
		return this.guiLeft;
	}
	
    @Override
	public void setYSize(int newYSize) {
		this.ySize = newYSize;
        this.guiTop = (this.height - this.ySize) / 2;
	}

	@Override
	public TargetType getTargetType() {
		return this.mode;
	}
    
	@Override
	public BlockPos getBlockPos() {
		return this.pos;
	}

	@Override
	public int getEntityId() {
		return this.entityId;
	}
	
	@Override
	public Entity getEntity() {
		return FakeWorldManager.getEntity(this.getWorld(), this.getEntityId());
		//return this.getWorld().getEntityByID(this.getEntityId());
	}
	
	@Override
	public World getWorld() {
		return this.player.world;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}
    
	@Override
	public void drawHoveringTooltip(List<String> text, int mouseX, int mouseY) {
		this.drawHoveringText(text, mouseX, mouseY, ClientHelper.getClient().fontRenderer);
	}

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
	public FontRenderer getFont() {
		return this.fontRenderer;
	}

	@Override
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6) {
		this.drawTexturedModalRect(par1, par2, par3, par4, par5, par6);
	}

	@Override
	public IFilterContainer getFilterContainer() {
		return (IFilterContainer)this.inventorySlots;
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
