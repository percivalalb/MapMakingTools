package mapmakingtools.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.MobSpawnerType;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketMobType;

/**
 * @author ProPercivalalb
 */
public class FilterMobType implements IFilter {

	private static ScaledResolution scaling = null;
    private int scrollY = 0;
    private int scrollHeight = 0;
    private int listHeight = 0;
    private int selected = -1;
    private boolean isScrolling = false;
    private List<String> mobTypes = new ArrayList<String>();
    
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.mobSpawnerType");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:mobType");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(135);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 135) / 2;
        this.mobTypes.clear();
        MobSpawnerType.addToList(mobTypes);
        Collections.sort(mobTypes);
        if(mobTypes.size() > 0) {
            this.listHeight = (14 * ((mobTypes.size() + (mobTypes.size() % 2 == 0 ? 0 : 1))) / 2 - 108);
            this.scrollHeight = (int)(108.0D / (double)(this.listHeight + 108) * 108.0D);

            if (this.scrollHeight <= 0 || this.scrollHeight >= 108) {
                this.scrollHeight = 108;
            }
        }
    	TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			for(int count = 0; count < mobTypes.size(); ++count) {
				if(mobTypes.get(count).equals(SpawnerHelper.getMobId(tile))) {
					this.selected = count;
					break;
				}
			}
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int xMouse, int yMouse) {
		int width = gui.width - gui.xSize() >> 1;
	    int height = gui.height - 135 >> 1;
	    this.scaling = new ScaledResolution(ClientHelper.mc.gameSettings, ClientHelper.mc.displayWidth, ClientHelper.mc.displayHeight);
	    this.clip(width, height);
        ClientHelper.mc.func_110434_K().func_110577_a(ResourceReference.screenScroll);
        int var9;
        for(var9 = 0; var9 < mobTypes.size(); ++var9) {
            int var10 = width + 10 + ((var9 & 1) != 0 ? 103 : 0);
            int var11 = height + 14 * (var9 / 2) + 21 - this.scrollY;
            gui.drawTexturedModalRect(var10, var11, 0 + (this.selected != var9 ? 0 : 8), 135, 8, 9);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.scrollHeight != 108) {
            this.drawScrollBar(gui);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)width, (float)height, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.mouseClicked(gui);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();

        if (this.scrollHeight != 108) {
            xMouse -= width;
            yMouse -= height;

            if (Mouse.isButtonDown(0)) {
                if (xMouse >= 216 && xMouse < 233 && yMouse >= 20 && yMouse < 128) {
                    this.isScrolling = true;
                }
            }
            else {
                this.isScrolling = false;
            }

            if (this.isScrolling) {
                this.scrollY = (yMouse - 20) * this.listHeight / (108 - (this.scrollHeight >> 1));

                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }

                if (this.scrollY > this.listHeight) {
                    this.scrollY = this.listHeight;
                }
            }

            var9 = Mouse.getDWheel();

            if (var9 < 0) {
                this.scrollY += 14;

                if (this.scrollY > this.listHeight) {
                    this.scrollY = this.listHeight;
                }
            }
            else if (var9 > 0) {
                this.scrollY -= 14;

                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }
            }
        }
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 135) / 2;
        gui.getFont().drawString(getFilterName(), k + 6, l + 6, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	private void drawScrollBar(GuiFilterMenu gui) {
		int var1 = (gui.width - gui.xSize() >> 1) + 227;
		int var2 = (gui.height - 135 >> 1) + 19 + this.scrollY * (108 - this.scrollHeight) / this.listHeight;
	    gui.drawTexturedModalRect(var1 - 10, var2, 0, 144, 15, 1);
	    int var3;

	    for(var3 = var2 + 1; var3 < var2 + this.scrollHeight - 1; ++var3) {
	        gui.drawTexturedModalRect(var1 - 10, var3, 0, 145, 15, 1);
	    }

	    gui.drawTexturedModalRect(var1 - 10, var3, 0, 146, 15, 1);
	}  
	
	private void clip(int var1, int var2) {
        int var3 = (var1 + 10) * scaling.getScaleFactor();
        int var4 = (var2 + 9) * scaling.getScaleFactor();
        int var5 = 215 * scaling.getScaleFactor();
        int var6 = 108 * scaling.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(var3, var4, var5, var6);
    }
	
	public boolean mouseInRadioButton(int var1, int var2, int var3) {
	    int var4 = 10 + ((var3 & 1) != 0 ? 103 : 0);
	    int var5 = 14 * (var3 / 2) + 21 - this.scrollY;
	    return var1 >= var4 - 1 && var1 < var4 + 9 && var2 >= var5 - 1 && var2 < var5 + 10;
	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
       
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int xMouse, int yMouse, int mouseButton) {
		 int var4 = gui.width - gui.xSize() >> 1;
	        int var5 = gui.height - 135 >> 1;
	        xMouse -= var4;
	        yMouse -= var5;

	        if (mouseButton == 0 && xMouse >= 10 && xMouse < 165 && yMouse >= 20 && yMouse < 128) {
	            for (int var6 = 0; var6 < mobTypes.size(); ++var6) {
	                if (this.mouseInRadioButton(xMouse, yMouse, var6)) {
	                    this.selected = var6;
	                    PacketTypeHandler.populatePacketAndSendToServer(new PacketMobType(gui.x, gui.y, gui.z, mobTypes.get(selected)));
	                    ClientHelper.mc.displayGuiScreen(null);
	                    ClientHelper.mc.setIngameFocus();
	                    break;
	                }
	            }
	        }
	}
	
	public void mouseClicked(GuiFilterMenu gui) {
		int width = gui.width - gui.xSize() >> 1;
	    int height = gui.height - 135 >> 1;
	    this.scaling = new ScaledResolution(ClientHelper.mc.gameSettings, ClientHelper.mc.displayWidth, ClientHelper.mc.displayHeight);
	    this.clip(width, height);

        for (int var3 = 0; var3 < mobTypes.size(); ++var3) {
            int var4 = ((var3 & 1) != 0 ? 103 : 0) + 20;
            int var5 = 14 * (var3 / 2) + 21 - this.scrollY;
            String var6 = "entity." + (String)mobTypes.get(var3) + ".name";
            String var7 = StatCollector.translateToLocal(var6);

            if (var7 == var6) {
                var7 = (String)mobTypes.get(var3);
            }
            
            ClientHelper.mc.fontRenderer.drawString(var7, var4, var5, 16777215);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {

        if (var2 == Keyboard.KEY_RETURN) {
           // gui.actionPerformed(btn_covert);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		scaling = new ScaledResolution(ClientHelper.mc.gameSettings, ClientHelper.mc.displayWidth, ClientHelper.mc.displayHeight);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.func_110434_K().func_110577_a(ResourceReference.screenScroll);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 135) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 135);
		return true;
	}

	@Override
	public boolean isMouseOverSlot(GuiFilterMenu gui, Slot slot, int mouseX,
			int mouseY) {
		// TODO Auto-generated method stub
		return false;
	}
}
