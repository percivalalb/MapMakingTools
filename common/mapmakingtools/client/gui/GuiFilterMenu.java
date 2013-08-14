package mapmakingtools.client.gui;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mapmakingtools.api.FilterRegistry;
import mapmakingtools.api.IFilter;
import mapmakingtools.api.IServerFilter;
import mapmakingtools.client.util.ButtonType;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.core.util.WrenchTasks.Mode;
import mapmakingtools.inventory.ContainerDummy;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketFilterPageMenu;
import mapmakingtools.network.packet.PacketSkullModify;
import mapmakingtools.network.packet.PacketWrenchTask;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.StringUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiFilterMenu extends GuiContainer {
   
	/** Extra data the class holds **/
	public EntityPlayer entityPlayer;
    public int x, y, z;
    public int entityId;
	public Mode mode;
	public enum Mode { ENTITY, BLOCK; }
    
	/** The list that the current screen could be **/
    private List<IFilter> filterList;
    private static IFilter current;
    /** The page varibles **/
    public static int currentPage = 1;
    int maxPages = 1;

    public GuiFilterMenu(EntityPlayer player, int x, int y, int z, List<IFilter> filters, List<IServerFilter> serverFilters) {
       	this(player, filters, serverFilters);
        this.x = x;
        this.y = y;
        this.z = z;
        this.mode = Mode.BLOCK;
        ((ContainerFilter)inventorySlots).setCor(x, y, z);
    }
    
    public GuiFilterMenu(EntityPlayer player, int entityId, List<IFilter> filters, List<IServerFilter> serverFilters) {
       	this(player, filters, serverFilters);
        this.entityId = entityId;
        this.mode = Mode.ENTITY;
        ((ContainerFilter)inventorySlots).setEntityId(entityId);
    }
    
    private GuiFilterMenu(EntityPlayer player, List<IFilter> filters, List<IServerFilter> serverFilters) {
       	super(new ContainerFilter(player, serverFilters));
    	this.entityPlayer = player;
        this.xSize = 302;
        this.ySize = 100;
        this.filterList = filters;
        this.maxPages = (filterList.size() + (deducuce(filterList.size() % 6))) / 6;
        LogHelper.logDebug("" + deducuce(filterList.size() % 6));
        LogHelper.logDebug("Max Pages: " + maxPages);
        if(currentPage > maxPages) {
        	currentPage = 1;
        }
        if(!filterList.contains(current)) {
        	current = null;
        }
        for(int count = 0; count < filterList.size(); ++count) {
			if(current == filterList.get(count)) {
				PacketTypeHandler.populatePacketAndSendToServer(new PacketFilterPageMenu(count));
				((ContainerFilter)inventorySlots).setSelected(count);
			}
		}
    }
    
    public int deducuce(int par1) {
    	switch(par1) {
    	case 0: return 6;
    	case 1: return 5;
    	case 2: return 4;
    	case 3: return 3;
    	case 4: return 2;
    	case 5: return 1;
    	default: return 0;
    	}
    }
    
    public ContainerFilter getContainer() {
    	return ((ContainerFilter)inventorySlots);
    }

    @Override
    public void updateScreen() {
    	if(current != null) {
    		current.updateScreen(this);
    	}
    }

    @Override
    public void initGui() {
    	super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int k = (this.width - this.xSize()) / 2;
        int l = (this.height - this.ySize()) / 2;

        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 6) {
            this.buttonList.add(new GuiTabSelect(150, k - 29, l + 9, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 6)));
        }
        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 5) {
        	this.buttonList.add(new GuiTabSelect(151, k - 29, l + 36, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 5)));
        }
        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 4) {
        	this.buttonList.add(new GuiTabSelect(152, k - 29, l + 63, ButtonType.LEFT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 4)));
        }
        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 3) {
        	this.buttonList.add(new GuiTabSelect(153, k + xSize() - 1, l + 9, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 3)));
        }
        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 2) {
        	this.buttonList.add(new GuiTabSelect(154, k + xSize() - 1, l + 36, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 2)));
        }
        if(filterList.size() > (currentPage == 0 ? 1 : currentPage) * 6 - 1) {
        	this.buttonList.add(new GuiTabSelect(155, k + xSize() - 1, l + 63, ButtonType.RIGHT, filterList.get((currentPage == 0 ? 1 : currentPage) * 6 - 1)));
        }
        if(filterList.size() > 6) {
        	this.buttonList.add(new GuiButton(148, k + xSize() + 3,  l - 15, 20, 20, "<"));
        	this.buttonList.add(new GuiButton(149, k + xSize() + 26, l - 15, 20, 20, ">"));
        }
        for(int var1 = 0; var1 < buttonList.size(); ++var1) {
    		GuiButton listBt = (GuiButton)buttonList.get(var1);
    		if(listBt instanceof GuiTabSelect) {
        		GuiTabSelect tab = (GuiTabSelect)listBt;
        		if(tab.filter == current) {
        			for(int count = 0; count < filterList.size(); ++count) {
        				if(current == filterList.get(count)) {
        					PacketTypeHandler.populatePacketAndSendToServer(new PacketFilterPageMenu(count));
        					((ContainerFilter)inventorySlots).setSelected(count);
        				}
        			}
        			tab.isSelected = true;
        		}
        		
    		}
        }
        
        if(current != null) {
    		current.initGui(this);
    	}
        else {
        	this.buttonList.add(new GuiButtonCancel(this, -1, k + 210, l + 70, 112, 220)); //cancel
        }
        
    }
    
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        if(mode == Mode.BLOCK) {
            PacketTypeHandler.populatePacketAndSendToServer(new PacketWrenchTask(x, y, z));
        }
        else {
        	LogHelper.logDebug("Send wrench task entity remove");
            PacketTypeHandler.populatePacketAndSendToServer(new PacketWrenchTask(entityId));
        }
    	//TODO onGuiClosed event
    	//if(current != null) {
    	//	current.onGuiClosed(this);
    	//}
    }

    @Override
	public void actionPerformed(GuiButton var1) {
        if(current != null) {
        	current.actionPerformed(this, var1);
        }
    	
    	if (var1.enabled) {
            switch (var1.id) {
            	case -1:
            		this.mc.displayGuiScreen((GuiScreen)null);
                    this.mc.setIngameFocus();
            		break;
            
            	case 148:
            		if(this.currentPage > 1) {
            			--currentPage;
            			this.initGui();
            		}
            		break;
            	case 149:
            		if(this.currentPage < maxPages) {
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
                	if(var1 instanceof GuiTabSelect) {
                		GuiTabSelect button = (GuiTabSelect)var1;
                		unSelectAllExpect(button);
                	}
                	break;
                default:
            }
        }
    	LogHelper.logDebug("Page Number: " + currentPage);
    }
    
    public void unSelectAllExpect(GuiTabSelect button) {
    	this.current = button.filter;
    	this.initGui();
    	for(int var1 = 0; var1 < buttonList.size(); ++var1) {
    		GuiButton listBt = (GuiButton)buttonList.get(var1);
    		if(listBt.id == button.id) {
    			if(listBt instanceof GuiTabSelect) {
    				((GuiTabSelect)listBt).isSelected = true;
    			}
    		}
    	}
    }

    @Override
    protected void keyTyped(char var1, int var2) {
        if(current != null) {
			current.keyTyped(this, var1, var2);
		}
        else {
        	if (var2 == Keyboard.KEY_ESCAPE) {
        		this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        	}
        }
    }

    @Override
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        if(current != null) {
			current.mouseClicked(this, var1, var2, var3);
		}
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float particleTicks, int xMouse, int yMouse) {	
		int k = (this.width - this.xSize()) / 2;
	    int l = (this.height - this.ySize()) / 2;
		if(current == null || current != null && !current.drawBackground(this)) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.func_110434_K().func_110577_a(ResourceReference.screenSmall);
			this.drawTexturedModalRect(k, l, 0, 0, this.xSize(), this.ySize());
		}
		if(current != null) {
			current.drawGuiContainerBackgroundLayer(this, particleTicks, xMouse, yMouse);
		}
		else {
			GL11.glPushMatrix();
			double scale = 1.7D;
			GL11.glScaled(scale, scale, scale);
			this.fontRenderer.drawString("Minecraft Filters", (int)((k + 10) / scale), (int)((l + 15) / scale), 0);
			GL11.glPopMatrix();
		}
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		int k = (this.width - this.xSize()) / 2;
	    int l = (this.height - this.ySize()) / 2;
		if(current != null) {
			current.drawGuiContainerForegroundLayer(this, xMouse, yMouse);
		}
		GL11.glTranslatef((float)-this.guiLeft, (float)-this.guiTop, 0.0F);
		for(int var1 = 0; var1 < buttonList.size(); ++var1) {
    		GuiButton listBt = (GuiButton)buttonList.get(var1);
    		if(listBt instanceof GuiTabSelect) {
        		GuiTabSelect tab = (GuiTabSelect)listBt;
        		if(tab.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = Arrays.asList(tab.filter.getFilterName());
        			this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    		if(listBt instanceof GuiButtonCancel) {
    			GuiButtonCancel tab = (GuiButtonCancel)listBt;
        		if(tab.isMouseAbove(xMouse, yMouse)) {
        			List<String> list = Arrays.asList(StatCollector.translateToLocal("gui.cancel"));
        			this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    		if(listBt.id == 148) {
    			if(listBt.mousePressed(mc, xMouse, yMouse)) {
        			List<String> list = Arrays.asList("Prev. Page");
        			this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    		if(listBt.id == 149) {
    			if(listBt.mousePressed(mc, xMouse, yMouse)) {
        			List<String> list = Arrays.asList("Next. Page");
        			this.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    	}
		GL11.glTranslatef((float)this.guiLeft, (float)this.guiTop, 0.0F);
    }
	
	public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
        if (!par1List.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > this.width)
            {
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
	
	public FontRenderer getFont() {
		return this.fontRenderer;
	}
	
	public int xSize() {
		return 240;
	}
	
	public int ySize() {
		return 100;
		//return this.ySize;
	}
	
	public List getButtonList() {
		return this.buttonList;
	}
	
	public void setYSize(int par1) {
		this.ySize = par1;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
	}
}