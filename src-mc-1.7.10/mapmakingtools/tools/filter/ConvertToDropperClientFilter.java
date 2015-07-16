package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.tools.filter.packet.PacketConvertToDropper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import mapmakingtools.tools.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ConvertToDropperClientFilter extends IFilterClient {

	public GuiButtonData btn_covert;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.converttodropper.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/convert.png";
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
		LogHelper.info(pos.toString());
		if(tile != null && tile.getClass() == TileEntityDispenser.class)
			return true;
		return false;
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Convert to Dropper");
		
        gui.getButtonList().add(this.btn_covert);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketConvertToDropper(gui.getBlockPos()));
            		ClientHelper.mc.setIngameFocus();
                	break;
            }
        }
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.converttodropper.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
}
