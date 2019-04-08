package mapmakingtools.tools.filter;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonColourBlock;
import mapmakingtools.client.gui.textfield.GuiTextFieldNonInteractable;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.filter.packet.PacketCommandBlockAlias;
import mapmakingtools.util.CommandBlockUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class CommandBlockAliasClientFilter extends FilterClient {

	private GuiTextFieldNonInteractable fld_alias;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private GuiButtonColourBlock btnColourLine1;
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
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityMinecartCommandBlock; 
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.fld_alias = new GuiTextFieldNonInteractable(0, gui.getFont(), topX + 20, topY +  45, 200, 20);
        this.fld_alias.setMaxStringLength(32);
        this.btn_ok = new GuiButton(0, topX + 140, topY + 70, 60, 20, "OK") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketCommandBlockAlias(fld_alias.getText() + "\u00a7r"));
            	ClientHelper.getClient().player.closeScreen();
    		}
    	};
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 70, 60, 20, "Cancel") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			ClientHelper.getClient().player.closeScreen();
    		}
    	};
        this.btnColourLine1 = new GuiButtonColourBlock(2, topX + 25, topY + 22, 20, 20);
        this.btnInsert = new GuiButton(4, topX + 50, topY + 22, 40, 20, "Insert") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			if(fld_alias.isFocused()) {
            		String text = fld_alias.getText();
            		fld_alias.setText(text + btnColourLine1.getCurrentColour(btnColourLine1.textColourIndex).getColour());
            		fld_alias.missMouseClick = true;
            	}
    		}
    	};
        gui.addTextFieldToGui(this.fld_alias);
        gui.addButtonToGui(this.btn_ok);
        gui.addButtonToGui(this.btn_cancel);
        gui.addButtonToGui(this.btnColourLine1);
        gui.addButtonToGui(this.btnInsert);

        if(CommandBlockUtil.isCommand(gui)) {
        	CommandBlockBaseLogic logic = CommandBlockUtil.getCommandLogic(gui);
        	
			ITextComponent name = CommandBlockUtil.getName(logic);
			//TODO if(name.endsWith("\u00a7r") && name.length() >= 2)
			//TODO 	name = name.substring(0, name.length() - 2);
			this.fld_alias.setText(name.getUnformattedComponentText());
		}
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		if (button.enabled) {
			if(button instanceof GuiButtonColourBlock) {
				if(this.fld_alias.isFocused())
					fld_alias.missMouseClick = true;
               	((GuiButtonColourBlock)button).leftClick();
            }
        }
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 1) {
            for (int l = 0; l < gui.getButtonList().size(); ++l) {
                GuiButton guibutton = (GuiButton)gui.getButtonList().get(l);

                if (guibutton.isMouseOver()) {
                	//gui.selectedButton = guibutton;
                    // TODO this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    if(guibutton instanceof GuiButtonColourBlock) {
                    	((GuiButtonColourBlock)guibutton).rightClick();
                    	if(this.fld_alias.isFocused())
                    		fld_alias.missMouseClick = true;
                    }
                }
            }
		}
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.commandblockalias.info"));
	}
	
	@Override
	public void drawToolTips(IFilterGui gui, int xMouse, int yMouse) {
		for(int var1 = 0; var1 < gui.getButtonList().size(); ++var1) {
    		GuiButton listBt = (GuiButton)gui.getButtonList().get(var1);
    		if(listBt instanceof GuiButtonColourBlock) {
    			GuiButtonColourBlock tab = (GuiButtonColourBlock)listBt;
        		if(tab.isMouseOver()) {
        			List<String> list = new ArrayList<String>();
        			list.add(tab.getCurrentColour(tab.textColourIndex).getName());
        			list.add((tab.textColourIndex + 1) + "/" + GuiButtonColourBlock.TextColour.values().length);
        			gui.drawHoveringTooltip(list, xMouse, yMouse);
        		}
    		}
    	}
	}
}
