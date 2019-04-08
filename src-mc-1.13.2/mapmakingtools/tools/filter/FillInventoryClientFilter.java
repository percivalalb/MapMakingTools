package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class FillInventoryClientFilter extends FilterClient {

	public GuiButton btnOk;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.fillinventory.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/fill_inventory.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, pos);
		if(tileEntity != null && tileEntity instanceof IInventory)
			return true;
		return super.isApplicable(player, world, pos);
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    	    	PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketFillInventory(gui.getBlockPos()));
        		ClientHelper.getClient().player.closeScreen();
    		}
    	};
        gui.addButtonToGui(this.btnOk);
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.fillinventory.info"));
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_ONE_SLOT);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
