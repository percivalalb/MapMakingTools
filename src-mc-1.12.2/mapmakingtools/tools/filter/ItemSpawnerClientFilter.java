package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.FilterMobSpawnerBase;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketItemSpawner;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class ItemSpawnerClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.changeitem.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/change_item.png";
	}

	@Override
	public void initGui(final IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
        this.addMinecartButtons(gui, topX, topY);
	}

	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.changeitem.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 104) / 2;
	    gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		super.mouseClicked(gui, xMouse, yMouse, mouseButton);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return true;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
		if(minecarts.size() <= 0) return true;
		WeightedSpawnerEntity randomMinecart = minecarts.get(minecartIndex);
		ResourceLocation mobId = SpawnerUtil.getMinecartType(randomMinecart);
		if(mobId.toString().equals("minecraft:item"))
			return false;
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.changeitem.error");
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketItemSpawner(gui.getBlockPos(), minecartIndex));
            		ClientHelper.getClient().player.closeScreen();
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_ONE_SLOT);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
