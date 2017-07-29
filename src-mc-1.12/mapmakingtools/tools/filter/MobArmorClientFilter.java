package mapmakingtools.tools.filter;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.FilterMobSpawnerBase;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketMobArmorUpdate;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;

/**
 * @author ProPercivalalb
 */
public class MobArmorClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobArmor.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/mob_armor.png";
	}
	
	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(151);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
        this.btnOk = new GuiButton(0, topX + 12, topY + 93, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
        this.addMinecartButtons(gui, topX, topY);
        this.onMinecartIndexChange(gui);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 151) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
        
        gui.getFont().drawString("Mob Armor", topX + 40, topY + 30, 4210752);
        gui.getFont().drawString("Player Armor", topX + 129, topY + 30, 4210752);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketMobArmor(gui.getBlockPos(), FilterMobSpawnerBase.minecartIndex));
            		ClientHelper.getClient().player.closeScreen();
                    break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public void onMinecartIndexChange(IGuiFilter gui) {
		if(this.showErrorIcon(gui))
			this.btnOk.enabled = false;
		else
			this.btnOk.enabled = true;
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.mobArmor.info"));
	}
	
	@Override
	public boolean hasUpdateButton(IGuiFilter gui) {
		return true;
	}
	
	@Override
	public void updateButtonClicked(IGuiFilter gui) {
		if(!showErrorIcon(gui))
			PacketDispatcher.sendToServer(new PacketMobArmorUpdate(gui.getBlockPos(), FilterMobSpawnerBase.minecartIndex));
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
		String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
		if(mobId.equals("minecraft:zombie") || mobId.equals("minecraft:zombie_pigman") || mobId.equals("minecraft:skeleton")) {
			return false;
		}
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.mobArmor.error");
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceReference.SCREEN_MOB_ARMOUR);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 151);
		return true;
	}
}
