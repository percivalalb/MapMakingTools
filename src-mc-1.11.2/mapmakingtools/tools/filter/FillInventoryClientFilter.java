package mapmakingtools.tools.filter;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

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
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if(button.enabled) {
            switch(button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketFillInventory(gui.getBlockPos()));
            		ClientHelper.getClient().player.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.fillinventory.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceReference.SCREEN_ONE_SLOT);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
