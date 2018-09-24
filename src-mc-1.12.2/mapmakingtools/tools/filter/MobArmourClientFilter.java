package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.FilterMobSpawnerBase;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketFetchMobArmour;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class MobArmourClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	
	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityLiving; 
	}
	
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
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btnOk = new GuiButton(0, topX + 12, topY + 93, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
        if(gui.getTargetType() == TargetType.BLOCK) {
	        this.addPotentialSpawnButtons(gui, topX, topY);
	        this.onPotentialSpawnChange(gui);
        }
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
	    int topY = gui.getGuiY();
        
        gui.getFont().drawString("Mob Armour", topX + 40, topY + 30, 4210752);
        gui.getFont().drawString("Player Armour", topX + 129, topY + 30, 4210752);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if(button.enabled) {
            if(button.id == 0) {
                PacketDispatcher.sendToServer(new PacketMobArmor(FilterMobSpawnerBase.potentialSpawnIndex));
            	ClientHelper.getClient().player.closeScreen();
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
        if(gui.getTargetType() == TargetType.BLOCK)
        	this.removePotentialSpawnButtons(gui, xMouse, yMouse, mouseButton, (gui.getScreenWidth() - gui.xFakeSize()) / 2, gui.getGuiY());
	}
	
	@Override
	public void onPotentialSpawnChange(IGuiFilter gui) {
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
			PacketDispatcher.sendToServer(new PacketFetchMobArmour(FilterMobSpawnerBase.potentialSpawnIndex));
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) { 
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			if(!(tile instanceof TileEntityMobSpawner))
				return true;
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
			if(minecarts.size() <= potentialSpawnIndex) return true;
			WeightedSpawnerEntity randomMinecart = minecarts.get(potentialSpawnIndex);
			String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
			if(mobId.equals("minecraft:zombie") || mobId.equals("minecraft:zombie_pigman") || mobId.equals("minecraft:skeleton")) {
				return false;
			}
			
			return true; 
		}
		
		return false;
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.mobArmor.error");
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_MOB_ARMOUR);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 151);
		return true;
	}
}
