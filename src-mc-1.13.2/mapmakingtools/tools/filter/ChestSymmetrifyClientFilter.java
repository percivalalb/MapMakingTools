package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.filter.packet.PacketChestSymmetrify;
import mapmakingtools.tools.filter.packet.PacketCreeperProperties;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class ChestSymmetrifyClientFilter extends FilterClient {

	public GuiButtonData btn_covert;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.chestsymmetrify.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/chest_symmetrify.png";
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityChest)
			return true;
		return false;
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Symmetrify") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketChestSymmetrify(gui.getBlockPos()));
        		ClientHelper.getClient().player.closeScreen();
    		}
    	};
		
        gui.addButtonToGui(this.btn_covert);
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.chestsymmetrify.info"));
	}
}
