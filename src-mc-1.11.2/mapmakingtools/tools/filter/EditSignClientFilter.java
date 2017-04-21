package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonTextColour;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.client.gui.textfield.GuiTextFieldNonInteractable;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketSignEdit;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class EditSignClientFilter extends IFilterClient {

	private GuiButtonTextColour btnColourLine1;
	private GuiTextFieldNonInteractable txtLine1;
	private GuiTextFieldNonInteractable txtLine2;
	private GuiTextFieldNonInteractable txtLine3;
	private GuiTextFieldNonInteractable txtLine4;
	private GuiButton btnInsert;
	private GuiButton btnOk;
	    
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.signedit.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/sign_edit.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, pos);
		if(tileEntity != null && tileEntity instanceof TileEntitySign)
			return true;
		return super.isApplicable(player, world, pos);
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
	    this.btnColourLine1 = new GuiButtonTextColour(0, topX + 25, topY + 22, 20, 20);
	    this.btnInsert = new GuiButton(1, topX + 15, topY + 46, 40, 20, "Insert");
	    this.btnOk = new GuiSmallButton(2, topX + (gui.xFakeSize() / 2) - (40 / 2), topY + 80, 40, 16, "Set");
	    this.txtLine1 = new GuiTextFieldNonInteractable(0, gui.getFont(), topX + 70, topY + 22, 100, 12);
	    this.txtLine1.setMaxStringLength(15);
	    this.txtLine2 = new GuiTextFieldNonInteractable(1, gui.getFont(), topX + 70, topY + 37, 100, 12);
	    this.txtLine2.setMaxStringLength(15);
	    this.txtLine3 = new GuiTextFieldNonInteractable(2, gui.getFont(), topX + 70, topY + 52, 100, 12);
	    this.txtLine3.setMaxStringLength(15);
	    this.txtLine4 = new GuiTextFieldNonInteractable(3,gui.getFont(), topX + 70, topY + 67, 100, 12);
	    this.txtLine4.setMaxStringLength(15);
	    gui.getButtonList().add(this.btnColourLine1);
	    gui.getButtonList().add(this.btnInsert);
	    gui.getButtonList().add(this.btnOk);
	    gui.getTextBoxList().add(this.txtLine1);
	    gui.getTextBoxList().add(this.txtLine2);
	    gui.getTextBoxList().add(this.txtLine3);
	    gui.getTextBoxList().add(this.txtLine4);
	    TileEntity tileEntity = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(tileEntity != null && tileEntity instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)tileEntity;
			this.txtLine1.setText(sign.signText[0].getUnformattedText());
			this.txtLine2.setText(sign.signText[1].getUnformattedText());
			this.txtLine3.setText(sign.signText[2].getUnformattedText());
			this.txtLine4.setText(sign.signText[3].getUnformattedText());
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
			if(button instanceof GuiButtonTextColour) {
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
               	((GuiButtonTextColour)button).leftClick();
            }
            switch (button.id) {
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
                	PacketDispatcher.sendToServer(new PacketSignEdit(gui.getBlockPos(), new IChatComponent[] {new ChatComponentText(this.txtLine1.getText()), new ChatComponentText(this.txtLine2.getText()), new ChatComponentText(this.txtLine3.getText()), new ChatComponentText(this.txtLine4.getText())}));
                	ClientHelper.mc.thePlayer.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.signedit.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
}
