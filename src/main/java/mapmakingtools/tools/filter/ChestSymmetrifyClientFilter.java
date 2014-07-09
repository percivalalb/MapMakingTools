package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IFilterClientSpawner;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.tools.filter.packet.PacketChestSymmetrify;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class ChestSymmetrifyClientFilter extends IFilterClient {

	public GuiButtonData btn_covert;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.chestsymmetrify.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:chestSymmetrify";
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityChest)
			return true;
		return false;
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Symmetrify");
		
        gui.getButtonList().add(this.btn_covert);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketChestSymmetrify(gui.getX(), gui.getY(), gui.getZ()));
            		ClientHelper.mc.setIngameFocus();
                	break;
            }
        }
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.chestsymmetrify.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
}
