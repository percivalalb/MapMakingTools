package mapmakingtools.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.button.GuiHorizontalTab;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketSelectedFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class GuiFilter extends GuiContainer implements IGuiFilter {

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
	
	private List<GuiTextField> textboxList = new ArrayList<>();
	
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
        	PacketDispatcher.sendToServer(new PacketSelectedFilter(index));
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    	this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int xMouse, int yMouse) {
		int topXF = (this.width - FAKE_WIDTH) / 2;
        int topY = (this.height - this.ySize) / 2;
        
		if(filter == null || !filter.drawBackground(this)) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_SMALL);
			this.drawTexturedModalRect(topXF, topY, 0, 0, FAKE_WIDTH, FAKE_HEIGHT);
		}
		
		if(filter != null) {
			filter.drawGuiContainerBackgroundLayer(this, partialTicks, xMouse, yMouse);
			this.fontRenderer.drawString(filter.getFilterName(), topXF - this.fontRenderer.getStringWidth(filter.getFilterName()) / 2 + FAKE_WIDTH / 2, topY + 6, 1);
		}
		else {
			GlStateManager.pushMatrix();
			double scale = 1.7D;
			GlStateManager.scale(scale, scale, scale);
			this.fontRenderer.drawString("Minecraft Filters", (int)((topXF + 10) / scale), (int)((topY + 15) / scale), 0);
			GlStateManager.scale(0.588D, 0.588D, 0.588D);
			GlStateManager.popMatrix();
		}
		
		for(GuiTextField textField : this.textboxList) 
			textField.drawTextBox();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		int topXF = (this.width - FAKE_WIDTH) / 2;
        int topY = (this.height - this.ySize) / 2;
        
		if(filter != null)
			filter.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		
		GlStateManager.translate((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
		for(GuiButton button : this.buttonList) {
			
    		if(button instanceof GuiHorizontalTab) {
        		GuiHorizontalTab tabButton = (GuiHorizontalTab)button;
        		if(tabButton.isMouseAbove(xMouse, yMouse)) {
        			
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
    		else if(button instanceof GuiButtonSmall && button.id == 156) {
    			GuiButtonSmall smallButton = (GuiButtonSmall)button;
        		if(smallButton.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = filter.getFilterInfo(this);
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		//if(listBt instanceof GuiButtonCancel) {
    		//	GuiButtonCancel tab = (GuiButtonCancel)listBt;
        	//	if(tab.isMouseAbove(xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList(I18n.translateToLocal("gui.cancel"));
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 148) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Prev. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 149) {
    		//	if(listBt.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Next. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    	}
		if(filter != null)
			filter.drawToolTips(this, xMouse, yMouse);
		
		GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
    }

	@Override
    public void initGui() {
    	super.initGui();
    	Keyboard.enableRepeatEvents(true);
    	this.textboxList.clear();
    	this.buttonList.clear();
    	this.labelList.clear();
    	
    	this.setYSize(FAKE_HEIGHT);
    	
    	int topXF = (this.width - FAKE_WIDTH) / 2;
        int topYF = (this.height - FAKE_HEIGHT) / 2;

        for(int i = 0; i < 3; ++i) {
        	int tabIndex = currentPage == -1 ? 0 : currentPage * 6;
        	int y = topYF + 7 + i * 29;
        	
        	if(this.filterList.size() > tabIndex + i) {
        		this.buttonList.add(new GuiHorizontalTab(150 + i, topXF - (30 - 1), y, GuiHorizontalTab.Side.LEFT, filterList.get(tabIndex + i), this));
        	}
        	
        	if(this.filterList.size() > tabIndex + i + 3) {
             	this.buttonList.add(new GuiHorizontalTab(153 + i, topXF + FAKE_WIDTH - 1, y, GuiHorizontalTab.Side.RIGHT, filterList.get(tabIndex + i + 3), this));
            }
        }

        if(this.filterList.size() > 6) {
        	this.buttonList.add(new GuiButton(148, topXF + FAKE_WIDTH + 3,  topYF - 15, 20, 20, "<"));
        	this.buttonList.add(new GuiButton(149, topXF + FAKE_WIDTH + 26, topYF - 15, 20, 20, ">"));
        }

        if(filter != null)
        	filter.initGui(this);
        else {}
        	//this.buttonList.add(new GuiButtonCancel(this, -1, k + 210, l + 70, 112, 220)); //cancel
        
        int topY = (this.height - this.ySize) / 2;
        
        if(filter != null) {
        	if(filter.getFilterInfo(this) != null)
        		this.buttonList.add(new GuiButtonSmall(156, topXF + 5, topY + 4, 13, 12, "?"));
        
        	if(filter.hasUpdateButton(this))
        		this.buttonList.add(new GuiButtonSmall(157, topXF + 20, topY + 4, 8, 8, "" + (char)8595));
        }
	
        for(int i = 0; i < this.buttonList.size(); ++i) {
    		if(this.buttonList.get(i) instanceof GuiHorizontalTab) {
        		GuiHorizontalTab tab = (GuiHorizontalTab)(GuiButton)this.buttonList.get(i);
        		if(tab.filter == filter) {
        			int index = this.filterList.indexOf(filter);
        			this.getContainerFilter().setSelected(index);
        			PacketDispatcher.sendToServer(new PacketSelectedFilter(index));

        			tab.isSelected = true;
        		}
    		}
        }	
	}

	@Override
    public void updateScreen() {
    	if(filter != null)
    		filter.updateScreen(this);
    	
    	for(GuiTextField textField : this.textboxList)
    		textField.updateCursorCounter();
    }
	
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
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
        
        for(GuiTextField textField : this.textboxList)
        	textField.textboxKeyTyped(cha, charIndex);
    }
    
    @Override
    protected void mouseClicked(int xMouse, int yMouse, int mouseButton) throws IOException {
        super.mouseClicked(xMouse, yMouse, mouseButton);
        
        if(filter != null)
        	filter.mouseClicked(this, xMouse, yMouse, mouseButton);
       
        for(GuiTextField textField : this.textboxList)
        	textField.mouseClicked(xMouse, yMouse, mouseButton);
    }
    
    public ContainerFilter getContainerFilter() {
    	return ((ContainerFilter)this.inventorySlots);
    }
    
    public void unSelectBut(GuiHorizontalTab button) {
    	filter = button.filter;
    	this.initGui();
    	for(int i = 0; i < this.buttonList.size(); ++i) {
    		GuiButton listBt = (GuiButton)this.buttonList.get(i);
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
	public BlockPos getBlockPos() {
		return this.pos;
	}

	@Override
	public int getEntityId() {
		return this.entityId;
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
	public FontRenderer getFont() {
		return this.fontRenderer;
	}

	@Override
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6) {
		this.drawTexturedModalRect(par1, par2, par3, par4, par5, par6);
	}

	@Override
	public IContainerFilter getFilterContainer() {
		return (IContainerFilter)this.inventorySlots;
	}
	
	@Override
	public Entity getEntity() {
		return this.getWorld().getEntityByID(this.getEntityId());
	}
}
