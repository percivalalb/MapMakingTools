package mapmakingtools.filters;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiButtonCancel;
import mapmakingtools.client.gui.GuiButtonTextColour;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.client.gui.GuiTabSelect;
import mapmakingtools.client.gui.GuiButtonTextColour.TextColour;
import mapmakingtools.client.gui.GuiTextFieldNonInteractable;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketSignEdit;

/**
 * @author ProPercivalalb
 */
public class FilterEditSign implements IFilter {

	public static Icon icon;
	private Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.signEdit");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:signEdit");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntitySign) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiButtonTextColour btnColourLine1;
    private GuiTextFieldNonInteractable txtLine1;
    private GuiTextFieldNonInteractable txtLine2;
    private GuiTextFieldNonInteractable txtLine3;
    private GuiTextFieldNonInteractable txtLine4;
    private GuiButton btnInsert;
    private GuiButton btnOk;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btnColourLine1 = new GuiButtonTextColour(0, k + 25, l + 22, 20, 20);
        this.btnInsert = new GuiButton(1, k + 15, l + 46, 40, 20, "Insert");
        this.btnOk = new GuiSmallButton(2, k + (gui.xSize() / 2) - (40 / 2), l + 80, 40, 16, "Set");
        this.txtLine1 = new GuiTextFieldNonInteractable(gui.getFont(), k + 70, l + 22, 100, 12);
        this.txtLine1.setMaxStringLength(15);
        this.txtLine2 = new GuiTextFieldNonInteractable(gui.getFont(), k + 70, l + 37, 100, 12);
        this.txtLine2.setMaxStringLength(15);
        this.txtLine3 = new GuiTextFieldNonInteractable(gui.getFont(), k + 70, l + 52, 100, 12);
        this.txtLine3.setMaxStringLength(15);
        this.txtLine4 = new GuiTextFieldNonInteractable(gui.getFont(), k + 70, l + 67, 100, 12);
        this.txtLine4.setMaxStringLength(15);
        gui.getButtonList().add(this.btnColourLine1);
        gui.getButtonList().add(this.btnInsert);
        gui.getButtonList().add(this.btnOk);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)tile;
			this.txtLine1.setText(sign.signText[0]);
			this.txtLine2.setText(sign.signText[1]);
			this.txtLine3.setText(sign.signText[2]);
			this.txtLine4.setText(sign.signText[3]);
		}
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
		int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - gui.ySize()) / 2;
	    gui.getFont().drawString(this.txtLine1.getText().length() + "/" + this.txtLine1.getMaxStringLength(), k + 175, l + 24, 4210752);
	    gui.getFont().drawString(this.txtLine2.getText().length() + "/" + this.txtLine2.getMaxStringLength(), k + 175, l + 39, 4210752);
	    gui.getFont().drawString(this.txtLine3.getText().length() + "/" + this.txtLine3.getMaxStringLength(), k + 175, l + 54, 4210752);
	    gui.getFont().drawString(this.txtLine4.getText().length() + "/" + this.txtLine4.getMaxStringLength(), k + 175, l + 69, 4210752);
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        this.txtLine1.drawTextBox();
        this.txtLine2.drawTextBox();
        this.txtLine3.drawTextBox();
        this.txtLine4.drawTextBox();
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
		this.txtLine1.updateCursorCounter();
		this.txtLine2.updateCursorCounter();
		this.txtLine3.updateCursorCounter();
		this.txtLine4.updateCursorCounter();
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
                	if(this.txtLine1.isFocused()) {
                		txtLine1.missMouseClick = true;
                	}
                	else if(this.txtLine2.isFocused()) {
                		txtLine2.missMouseClick = true;
                	}
                	else if(this.txtLine3.isFocused()) {
                		txtLine3.missMouseClick = true;
                	}
                	else if(this.txtLine4.isFocused()) {
                		txtLine4.missMouseClick = true;
                	}
                }
            }
        }
		this.txtLine1.mouseClicked(xMouse, yMouse, mouseButton);
		this.txtLine2.mouseClicked(xMouse, yMouse, mouseButton);
		this.txtLine3.mouseClicked(xMouse, yMouse, mouseButton);
		this.txtLine4.mouseClicked(xMouse, yMouse, mouseButton);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
		this.txtLine1.textboxKeyTyped(var1, var2);
		this.txtLine2.textboxKeyTyped(var1, var2);
		this.txtLine3.textboxKeyTyped(var1, var2);
		this.txtLine4.textboxKeyTyped(var1, var2);
		
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btnOk);
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
				if(this.txtLine1.isFocused()) {
            		txtLine1.missMouseClick = true;
            	}
            	else if(this.txtLine2.isFocused()) {
            		txtLine2.missMouseClick = true;
            	}
            	else if(this.txtLine3.isFocused()) {
            		txtLine3.missMouseClick = true;
            	}
            	else if(this.txtLine4.isFocused()) {
            		txtLine4.missMouseClick = true;
            	}
               	((GuiButtonTextColour)var1).leftClick();
            }
            switch (var1.id) {
                case 0:
                	break;
                	//PacketTypeHandler.populatePacketAndSendToServer(new PacketConvertToDispenser(gui.x, gui.y, gui.z));
                	//ClientHelper.mc.displayGuiScreen(null);
                   // ClientHelper.mc.setIngameFocus();
                case 1:
                	if(this.txtLine1.isFocused()) {
                		String text = txtLine1.getText();
                		txtLine1.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine1.missMouseClick = true;
                	}
                	else if(this.txtLine2.isFocused()) {
                		String text = txtLine2.getText();
                		txtLine2.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine2.missMouseClick = true;
                	}
                	else if(this.txtLine3.isFocused()) {
                		String text = txtLine3.getText();
                		txtLine3.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine3.missMouseClick = true;
                	}
                	else if(this.txtLine4.isFocused()) {
                		String text = txtLine4.getText();
                		txtLine4.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		txtLine4.missMouseClick = true;
                	}
                	break;
                case 2:
                    PacketSignEdit packet = new PacketSignEdit(gui.x, gui.y, gui.z, new String[] {this.txtLine1.getText(), this.txtLine2.getText(), this.txtLine3.getText(), this.txtLine4.getText()});
                    PacketTypeHandler.populatePacketAndSendToServer(packet);
                    packet.execute(null, gui.entityPlayer);
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
