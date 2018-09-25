package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

/**
 * @author ProPercivalalb
 */
public class BabyMonsterClientFilter extends FilterMobSpawnerBase {

	public GuiButtonData btn_covert;
	
	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityZombie; 
	}
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.babymonster.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/baby_monster.png";
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Turn into Baby");
        gui.getButtonList().add(this.btn_covert);
        
        if(SpawnerUtil.isSpawner(gui)) {
        	this.addPotentialSpawnButtons(gui, topX, topY);
        	
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			if(SpawnerUtil.isBabyMonster(spawnerLogic, potentialSpawnIndex)) {
				this.btn_covert.displayString = "Turn into Adult";
				this.btn_covert.setData(1);
			}
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = gui.getEntity();
        	if(entity instanceof EntityZombie) {
        		EntityZombie zombie = (EntityZombie)entity;
        		if(zombie.isChild()) {
	        		this.btn_covert.displayString = "Turn into Adult";
					this.btn_covert.setData(1);
        		}
        	}
		}
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            if(button.id == 0) {
                PacketDispatcher.sendToServer(new PacketBabyMonster(this.btn_covert.getData() == 0, potentialSpawnIndex));
            	ClientHelper.getClient().player.closeScreen();
            }
        }
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, int xMouse, int yMouse, int mouseButton) {
		if(SpawnerUtil.isSpawner(gui))
        	this.removePotentialSpawnButtons(gui, xMouse, yMouse, mouseButton, (gui.getScreenWidth() - gui.xFakeSize()) / 2, gui.getGuiY());
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.babymonster.info"));
	}

	@Override
	public boolean showErrorIcon(IFilterGui gui) {
		if(SpawnerUtil.isSpawner(gui)) {
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
			if(minecarts.size() <= potentialSpawnIndex) return true;
			WeightedSpawnerEntity randomMinecart = minecarts.get(potentialSpawnIndex);
			String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
			if(mobId.equals("minecraft:zombie") || mobId.equals("minecraft:zombie_pigman"))
				return false;
			
			return true; 
		}
		return false;
	}
	
	@Override
	public String getErrorMessage(IFilterGui gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.babymonster.error");
	}
}
