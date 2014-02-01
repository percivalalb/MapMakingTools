package mapmakingtools.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.FilterManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IGuiFilter;
import mapmakingtools.api.TargetType;
import mapmakingtools.container.ContainerFilter;
import mapmakingtools.container.IPhantomSlot;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.packet.PacketSelectedFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class GuiFilter extends GuiContainer implements IGuiFilter {

	//Extra data the class holds
	public EntityPlayer player;
    public int x, y, z;
    public int entityId;
	public TargetType mode;
	
	private List<IFilterClient> filterList;
	private static IFilterClient filterCurrent;
	private static int currentPage = 1;
	private int maxPages = 1;
	
	private List textboxList = new ArrayList();
	
	private GuiFilter(List<IFilterClient> filters, EntityPlayer player) {
		super(new ContainerFilter(FilterManager.getServerFiltersFromList(filters), player));
		this.field_146999_f = 302;
        this.field_147000_g = 100;
        this.filterList = filters;
        this.player = player;
        this.maxPages = (this.filterList.size() + (filterList.size() % 6)) / 6;
        
        //If the last selected filter is not in this new gui reset it
        if(!this.filterList.contains(filterCurrent))
        	filterCurrent = null;
        else {
        	int index = this.filterList.indexOf(filterCurrent);
        	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketSelectedFilter(index));
			this.getContainerFilter().setSelected(index);
        }
	}
	
	public GuiFilter(List<IFilterClient> filters, EntityPlayer player, int x, int y, int z) {
		this(filters, player);
		this.x = x;
	    this.y = y;
	    this.z = z;
	    this.mode = TargetType.BLOCK;
	    this.getContainerFilter().setBlockCoords(x, y, z);
	}
	    
	public GuiFilter(List<IFilterClient> filters, EntityPlayer player, int entityId) {
		this(filters, player);
	    this.entityId = entityId;
	    this.mode = TargetType.ENTITY;
	    this.getContainerFilter().setEntityId(entityId);
	}

	@Override
	protected void func_146976_a(float partialTicks, int xMouse, int yMouse) {
		int topX = (this.field_146294_l - this.xFakeSize()) / 2;
        int topY = (this.field_146295_m - this.yFakeSize()) / 2;
        
		if(filterCurrent == null || !filterCurrent.drawBackground(this)) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenSmall);
			this.drawTexturedModalRect(topX, topY, 0, 0, this.xFakeSize(), this.yFakeSize());
		}
		if(filterCurrent != null)
			filterCurrent.drawGuiContainerBackgroundLayer(this, partialTicks, xMouse, yMouse);
		else {
			GL11.glPushMatrix();
			double scale = 1.7D;
			GL11.glScaled(scale, scale, scale);
			this.field_146289_q.drawString("Minecraft Filters", (int)((topX + 10) / scale), (int)((topY + 15) / scale), 0);
			GL11.glScaled(0.588D, 0.588D, 0.588D);
			GL11.glPopMatrix();
		}
		
		for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).func_146194_f();
	}
	
	@Override
	protected void func_146979_b(int xMouse, int yMouse) {
		int topX = (this.field_146294_l - this.xFakeSize()) / 2;
        int topY = (this.field_146295_m - this.yFakeSize()) / 2;
        
		for(int i = 0; i < this.field_147002_h.inventorySlots.size(); ++i) {
			if(this.field_147002_h.inventorySlots.get(i) instanceof IPhantomSlot) {
				Slot slot = (Slot)this.field_147002_h.inventorySlots.get(i);
				IPhantomSlot phantomSlot = (IPhantomSlot)this.field_147002_h.inventorySlots.get(i);
				if(phantomSlot.isUnlimited() && slot.getStack() != null && slot.getStack().stackSize == 1) {
					String s1 = "" + (char)8734;
			        GL11.glDisable(GL11.GL_LIGHTING);
			        GL11.glDisable(GL11.GL_DEPTH_TEST);
			        GL11.glDisable(GL11.GL_BLEND);
			        this.field_146289_q.drawStringWithShadow(s1, slot.xDisplayPosition + 19 - 2 - this.field_146289_q.getStringWidth(s1), slot.yDisplayPosition + 6 + 3, 16777215);
			        GL11.glEnable(GL11.GL_LIGHTING);
			        GL11.glEnable(GL11.GL_DEPTH_TEST);
			        GL11.glEnable(GL11.GL_BLEND);
				}
			}
		}
        
		if(filterCurrent != null)
			filterCurrent.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		
		GL11.glTranslatef((float)-this.field_147003_i, (float)-this.field_147009_r, 0.0F);
		for(int var1 = 0; var1 < this.field_146292_n.size(); ++var1) {
    		GuiButton listBt = (GuiButton)this.field_146292_n.get(var1);
    		if(listBt instanceof GuiTabSelect) {
        		GuiTabSelect button = (GuiTabSelect)listBt;
        		if(button.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = new ArrayList<String>();
        			list.add(button.filter.getFilterName());
        			if(button.filter.showErrorIcon(this)) {
        				String errorMessage = button.filter.getErrorMessage(this);
        				if(errorMessage != null)
        					list.add(errorMessage);
        			}
        			this.drawHoveringText(list, xMouse, yMouse);
        		}
    		}
    		//else if(listBt instanceof GuiSmallButton) {
    		//	GuiSmallButton button = (GuiSmallButton)listBt;
        	//	if(button.isMouseAbove(xMouse, yMouse)) {
        	//		List<String> list = this.filterCurrent.getFilterInfo();
        	//		this.drawHoveringText(list, xMouse, yMouse);
        	//	}
    		//}
    		//if(listBt instanceof GuiButtonCancel) {
    		//	GuiButtonCancel tab = (GuiButtonCancel)listBt;
        	//	if(tab.isMouseAbove(xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList(StatCollector.translateToLocal("gui.cancel"));
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 148) {
    		//	if(listBt.mousePressed(ClientHelper.mc, xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Prev. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    		//if(listBt.id == 149) {
    		//	if(listBt.mousePressed(ClientHelper.mc, xMouse, yMouse)) {
        	//		List<String> list = Arrays.asList("Next. Page");
        	//		this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        	//	}
    		//}
    	}
		if(filterCurrent != null)
			filterCurrent.drawToolTips(this, xMouse, yMouse);
		GL11.glTranslatef((float)this.field_147003_i, (float)this.field_147009_r, 0.0F);
    }

	@Override
    public void initGui() {
    	super.initGui();
    	Keyboard.enableRepeatEvents(true);
    	this.textboxList.clear();
    	this.field_146292_n.clear();
    	this.field_146293_o.clear();
    	int topX = (this.field_146294_l - this.xFakeSize()) / 2;
        int topY = (this.field_146295_m - this.yFakeSize()) / 2;

        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 6) {
            this.field_146292_n.add(new GuiTabSelect(150, topX - 29, topY + 9, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 6), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 5) {
        	this.field_146292_n.add(new GuiTabSelect(151, topX - 29, topY + 36, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 5), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 4) {
        	this.field_146292_n.add(new GuiTabSelect(152, topX - 29, topY + 63, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 4), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 3) {
        	this.field_146292_n.add(new GuiTabSelect(153, topX + xFakeSize() - 1, topY + 9, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 3), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 2) {
        	this.field_146292_n.add(new GuiTabSelect(154, topX + xFakeSize() - 1, topY + 36, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 2), this));
        }
        if(this.filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 1) {
        	this.field_146292_n.add(new GuiTabSelect(155, topX + xFakeSize() - 1, topY + 63, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 1), this));
        }
        
        if(this.filterList.size() > 6) {
        	this.field_146292_n.add(new GuiButton(148, topX + xFakeSize() + 3,  topY - 15, 20, 20, "<"));
        	this.field_146292_n.add(new GuiButton(149, topX + xFakeSize() + 26, topY - 15, 20, 20, ">"));
        }
        
        if(filterCurrent != null)
        	filterCurrent.initGui(this);
        else {}
        	//this.field_146292_n.add(new GuiButtonCancel(this, -1, k + 210, l + 70, 112, 220)); //cancel
        
        int realtopY = (this.field_146295_m - this.field_147000_g) / 2;
        
        if(filterCurrent != null && filterCurrent.getFilterInfo(this) != null)
        	this.field_146292_n.add(new GuiSmallButton(156, topX + 5, realtopY + 4, 13, 12, "?"));
        
        if(filterCurrent != null && filterCurrent.hasUpdateButton(this))
        	this.field_146292_n.add(new GuiSmallButton(157, topX + 5, realtopY + 4, 8, 8, "" + (char)8595));
        
        for(int i = 0; i < this.field_146292_n.size(); ++i) {
    		if(this.field_146292_n.get(i) instanceof GuiTabSelect) {
        		GuiTabSelect tab = (GuiTabSelect)(GuiButton)this.field_146292_n.get(i);
        		if(tab.filter == filterCurrent) {
        			int index = this.filterList.indexOf(filterCurrent);
        			MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketSelectedFilter(index));
        			this.getContainerFilter().setSelected(index);
        			tab.isSelected = true;
        		}
    		}
        }	
	}

	@Override
    public void updateScreen() {
    	if(filterCurrent != null)
    		filterCurrent.updateScreen(this);
    	
    	for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).func_146178_a();
    }
	
    @Override
    public void func_146281_b() {
    	super.func_146281_b();
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
	public void func_146284_a(GuiButton button) {
        if(filterCurrent != null)
        	filterCurrent.actionPerformed(this, button);
    	
    	if (button.field_146124_l) {
            switch (button.field_146127_k) {
            	case -1:
            		ClientHelper.mc.func_147108_a((GuiScreen)null);
            		ClientHelper.mc.setIngameFocus();
            		break;
            
            	case 148:
            		if(this.currentPage > 1) {
            			--currentPage;
            			this.initGui();
            		}
            		break;
            	case 149:
            		if(this.currentPage <= maxPages) {
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
                	if(button instanceof GuiTabSelect)
                		unSelectBut((GuiTabSelect)button);
                	break;
                case 157:
                	filterCurrent.updateButtonClicked(this);
                default:
            }
        }
    }
    
    @Override
    protected void keyTyped(char cha, int charIndex) {
        if(filterCurrent != null)
        	filterCurrent.keyTyped(this, cha, charIndex);
        
        if(filterCurrent == null || filterCurrent.doClosingKeysWork(this, cha, charIndex)){
        	if (charIndex == Keyboard.KEY_ESCAPE) {
        		ClientHelper.mc.func_147108_a((GuiScreen)null);
        		ClientHelper.mc.setIngameFocus();
        	}
        }
        for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)this.textboxList.get(i)).func_146201_a(cha, charIndex);
    }
    
    @Override
    protected void mouseClicked(int xMouse, int yMouse, int mouseButton) {
        super.mouseClicked(xMouse, yMouse, mouseButton);
        
        if(filterCurrent != null)
        	filterCurrent.mouseClicked(this, xMouse, yMouse, mouseButton);
       
        for(int i = 0; i < this.textboxList.size(); ++i)
        	((GuiTextField)textboxList.get(i)).func_146192_a(xMouse, yMouse, mouseButton);
    }
    
    public ContainerFilter getContainerFilter() {
    	return ((ContainerFilter)this.field_147002_h);
    }
    
    public void unSelectBut(GuiTabSelect button) {
    	filterCurrent = button.filter;
    	this.initGui();
    	for(int i = 0; i < this.field_146292_n.size(); ++i) {
    		GuiButton listBt = (GuiButton)this.field_146292_n.get(i);
    		if(listBt.field_146127_k == button.field_146127_k) {
    			if(listBt instanceof GuiTabSelect)
    				((GuiTabSelect)listBt).isSelected = true;
    		}
    	}
    }
    
    protected void drawHoveringText(List text, int mouseX, int mouseY, FontRenderer font) {
        if (!text.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = text.iterator();

            while (iterator.hasNext()) {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                    k = l;
            }

            int j2 = mouseX + 12;
            int k2 = mouseY - 12;
            int i1 = 8;

            if (text.size() > 1)
                i1 += 2 + (text.size() - 1) * 10;

            if (j2 + k > this.field_146294_l)
                j2 -= 28 + k;

            if (k2 + i1 + 6 > this.field_146295_m)
                k2 = this.field_146295_m - i1 - 6;

            this.zLevel = 300.0F;
            field_146296_j.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < text.size(); ++i2) {
                String s1 = (String)text.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                    k2 += 2;

                k2 += 10;
            }

            this.zLevel = 0.0F;
            field_146296_j.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
    
    
    ///////////////////////////////  IGuiFilter overridden methods  ///////////////////////////////
    
    @Override
    public int xFakeSize() {
		return 240;
	}
	
    @Override
	public int yFakeSize() {
		return 100;
	}
    
	@Override
	public int getWidth() {
		return this.field_146294_l;
	}

	@Override
	public int getHeight() {
		return this.field_146295_m;
	}
	
    @Override
	public void setYSize(int newYSize) {
		this.field_147000_g = newYSize;
        this.field_147003_i = (this.field_146294_l - this.field_146999_f) / 2;
        this.field_147009_r = (this.field_146295_m - this.field_147000_g) / 2;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getEntityId() {
		return this.entityId;
	}
	
	@Override
	public World getWorld() {
		return this.player.worldObj;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}
    
	@Override
	public void drawHoveringText(List text, int mouseX, int mouseY) {
		this.drawHoveringText(text, mouseX, mouseY, ClientHelper.mc.fontRenderer);
	}

	@Override
	public List getLabelList() {
		return this.field_146293_o;
	}

	@Override
	public List getButtonList() {
		return this.field_146292_n;
	}

	@Override
	public List getTextBoxList() {
		return this.textboxList;
	}
	
	@Override
	public FontRenderer getFont() {
		return this.field_146289_q;
	}

	@Override
	public void drawTexturedModalRectangle(int par1, int par2, int par3, int par4, int par5, int par6) {
		this.drawTexturedModalRect(par1, par2, par3, par4, par5, par6);
	}
}
