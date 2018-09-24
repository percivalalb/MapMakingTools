package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.FilterMobSpawnerBase;
import mapmakingtools.api.interfaces.IGuiFilter;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

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
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Turn into Baby");
        
        if(gui.getTargetType() == TargetType.BLOCK) {
	        TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				if(SpawnerUtil.isBabyMonster(spawner.getSpawnerBaseLogic(), this.minecartIndex)) {
					this.btn_covert.displayString = "Turn into Adult";
					this.btn_covert.setData(1);
				}
			}
	        this.addMinecartButtons(gui, topX, topY);
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
				
        gui.getButtonList().add(this.btn_covert);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketBabyMonster(gui.getBlockPos(), this.btn_covert.getData() == 0, this.minecartIndex));
            		ClientHelper.getClient().player.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        if(gui.getTargetType() == TargetType.BLOCK)
        	this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.babymonster.info"));
	}

	@Override
	public boolean showErrorIcon(IGuiFilter gui) {
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			if(!(tile instanceof TileEntityMobSpawner))
				return true;
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
			if(minecarts.size() <= minecartIndex) return true;
			WeightedSpawnerEntity randomMinecart = minecarts.get(minecartIndex);
			String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
			if(mobId.equals("minecraft:zombie") || mobId.equals("minecraft:zombie_pigman"))
				return false;
			
			return true; 
		}
		return false;
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.babymonster.error");
	}
}
