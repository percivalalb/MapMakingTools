package mapmakingtools.filters;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiButtonTextColour;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiTextFieldNonInteractable;
import mapmakingtools.client.gui.GuiButtonTextColour.TextColour;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketCommandBlockAlias;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;

/**
 * @author ProPercivalalb
 */
public class FilterCommandBlockAlias implements IFilter {

	public static Icon icon;
	private Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.commandBlockName");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:commandBlockName");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityCommandBlock) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiTextFieldNonInteractable fld_alias;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private GuiButtonTextColour btnColourLine1;
    private GuiButton btnInsert;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.fld_alias = new GuiTextFieldNonInteractable(mc.fontRenderer, k + 20, l +  45, 200, 20);
        this.fld_alias.setMaxStringLength(32);
        this.btn_ok = new GuiButton(0, k + 140, l + 70, 60, 20, "OK");
        this.btn_cancel = new GuiButton(1, k + 40, l + 70, 60, 20, "Cancel");
        this.btnColourLine1 = new GuiButtonTextColour(2, k + 25, l + 22, 20, 20);
        this.btnInsert = new GuiButton(4, k + 50, l + 22, 40, 20, "Insert");
        gui.getTextFieldList().add(this.fld_alias);
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getButtonList().add(this.btnColourLine1);
        gui.getButtonList().add(this.btnInsert);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityCommandBlock) {
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			String text = commandBlock.getCommandSenderName();
			if(text.endsWith(MapMakingTools.sectionSign + "r"))
				text = text.substring(0, text.length() - 2);
			this.fld_alias.setText(text);
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
        			List<String> list = Arrays.asList(tab.getCurrentColour(tab.textColourIndex).getName(), (tab.textColourIndex + 1) + "/" + TextColour.values().length);
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
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    if(guibutton instanceof GuiButtonTextColour) {
                    	((GuiButtonTextColour)guibutton).rightClick();
                    }
                	if(this.fld_alias.isFocused()) {
                		fld_alias.missMouseClick = true;
                	}
                }
            }
		}
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {

        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
			if(var1 instanceof GuiButtonTextColour) {
				if(this.fld_alias.isFocused()) {
					fld_alias.missMouseClick = true;
            	}
               	((GuiButtonTextColour)var1).leftClick();
            }
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketCommandBlockAlias(gui.x, gui.y, gui.z, fld_alias.getText() + MapMakingTools.sectionSign + "r"));
                case 1:
                	ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                	break;
                case 4:
                	if(this.fld_alias.isFocused()) {
                		String text = fld_alias.getText();
                		fld_alias.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		fld_alias.missMouseClick = true;
                	}
                	break;
            }
        }
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		return false;
	}
}
