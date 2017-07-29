package mapmakingtools.tools.filter;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonTextColour;
import mapmakingtools.client.gui.textfield.GuiTextFieldNonInteractable;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketCommandBlockAlias;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandBlockAliasClientFilter extends FilterClient {

	private GuiTextFieldNonInteractable fld_alias;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private GuiButtonTextColour btnColourLine1;
    private GuiButton btnInsert;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.commandblockalias.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/command_block_name.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, pos);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, pos);
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.fld_alias = new GuiTextFieldNonInteractable(0, gui.getFont(), topX + 20, topY +  45, 200, 20);
        this.fld_alias.setMaxStringLength(32);
        this.btn_ok = new GuiButton(0, topX + 140, topY + 70, 60, 20, "OK");
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 70, 60, 20, "Cancel");
        this.btnColourLine1 = new GuiButtonTextColour(2, topX + 25, topY + 22, 20, 20);
        this.btnInsert = new GuiButton(4, topX + 50, topY + 22, 40, 20, "Insert");
        gui.getTextBoxList().add(this.fld_alias);
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getButtonList().add(this.btnColourLine1);
        gui.getButtonList().add(this.btnInsert);
        TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(tile instanceof TileEntityCommandBlock) {
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			String text = commandBlock.getCommandBlockLogic().getName();
			if(text.endsWith("\u00a7r") && text.length() >= 2)
				text = text.substring(0, text.length() - 2);
			this.fld_alias.setText(text);
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if (button.enabled) {
			if(button instanceof GuiButtonTextColour) {
				if(this.fld_alias.isFocused())
					fld_alias.missMouseClick = true;
               	((GuiButtonTextColour)button).leftClick();
            }
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketCommandBlockAlias(gui.getBlockPos(), fld_alias.getText() + "\u00a7r"));
                	ClientHelper.getClient().player.closeScreen();
                	break;
                case 4:
                	if(this.fld_alias.isFocused()) {
                		String text = fld_alias.getText();
                		this.fld_alias.setText(text + this.btnColourLine1.getCurrentColour(this.btnColourLine1.textColourIndex).getColour());
                		this.fld_alias.missMouseClick = true;
                	}
                	break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
                	//gui.selectedButton = guibutton;
                    // TODO this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    if(guibutton instanceof GuiButtonTextColour) {
                    	((GuiButtonTextColour)guibutton).rightClick();
                    	if(this.fld_alias.isFocused())
                    		fld_alias.missMouseClick = true;
                    }
                }
            }
		}
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.commandblockalias.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt instanceof GuiButtonTextColour) {
    			GuiButtonTextColour tab = (GuiButtonTextColour)listBt;
        		if(tab.mousePressed(ClientHelper.getClient(), xMouse, yMouse)) {
        			List<String> list = new ArrayList<String>();
        			list.add(tab.getCurrentColour(tab.textColourIndex).getName());
        			list.add((tab.textColourIndex + 1) + "/" + GuiButtonTextColour.TextColour.values().length);
        			gui.drawHoveringText2(list, xMouse, yMouse);
        		}
    		}
    	}
	}
}
