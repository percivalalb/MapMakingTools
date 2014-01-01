package mapmakingtools.filters;

import java.util.Arrays;
import java.util.List;

import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiButtonTextColour;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.core.helper.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class FilterMobMaxHealth implements IFilter {

	public static Icon icon;
	private Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.mobHealth");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:mobHealth");
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
		if(entity != null && entity instanceof EntityLiving)
			return true;
		return false;
	}

    private GuiButton btnOk;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btnOk = new GuiSmallButton(2, k + (gui.xSize() / 2) - (40 / 2), l + 80, 40, 16, "Set");
        gui.getButtonList().add(this.btnOk);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		}
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
		int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - gui.ySize()) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int xMouse, int yMouse) {
		GL11.glTranslatef((float)-gui.guiLeft(), (float)-gui.guiTop(), 0.0F);
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt instanceof GuiButtonTextColour) {
    			GuiButtonTextColour tab = (GuiButtonTextColour)listBt;
        		if(tab.mousePressed(mc, xMouse, yMouse)) {
        			List<String> list = Arrays.asList(tab.getCurrentColour(tab.textColourIndex).getName(), (tab.textColourIndex + 1) + "/");
        			gui.drawHoveringText(list, xMouse, yMouse, this.mc.fontRenderer);
        		}
    		}
    	}
		GL11.glTranslatef((float)gui.guiLeft(), (float)gui.guiTop(), 0.0F);
	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {

	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int xMouse, int yMouse, int mouseButton) {		
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.mousePressed(this.mc, xMouse, yMouse)) {
                	//gui.selectedButton = guibutton;
                   // this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    
                }
            }
        }
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {

        if (var2 == Keyboard.KEY_RETURN) {
            //gui.actionPerformed(btnColourLine1);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	break;
                	//PacketTypeHandler.populatePacketAndSendToServer(new PacketConvertToDispenser(gui.x, gui.y, gui.z));
                	//ClientHelper.mc.displayGuiScreen(null);
                   // ClientHelper.mc.setIngameFocus();
                case 1:
                	
                	break;
                case 2:
                    //PacketSignEdit packet = new PacketSignEdit(gui.x, gui.y, gui.z, new String[] {this.txtLine1.getText(), this.txtLine2.getText(), this.txtLine3.getText(), this.txtLine4.getText()});
                    //PacketTypeHandler.populatePacketAndSendToServer(packet);
                    //packet.execute(null, gui.entityPlayer);
                	ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                	break;
            }
        }
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		return false;
	}
}
