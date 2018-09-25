package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketCreeperProperties;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
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
public class CreeperPropertiesClientFilter extends FilterMobSpawnerBase {

	public GuiTextField txt_radius;
	public GuiTextField txt_fuse;
	public GuiButton btn_ok;
	public GuiButton btn_cancel;
	
	public static String fuseText = "30";
	public static String radiusText = "3";
	
	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityCreeper; 
	}
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.creeperproperties.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/explosion.png";
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_ok = new GuiButton(0, topX + 140, topY + 66, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 66, 60, 20, "Cancel");
        this.txt_radius = new GuiTextField(0, gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_radius.setMaxStringLength(7);
        this.txt_radius.setText(radiusText);
        this.txt_fuse = new GuiTextField(1, gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_fuse.setMaxStringLength(7);
        this.txt_fuse.setText(fuseText);
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getTextBoxList().add(this.txt_radius);
        gui.getTextBoxList().add(this.txt_fuse);
        
        if(SpawnerUtil.isSpawner(gui)) {
        	this.addPotentialSpawnButtons(gui, topX, topY);
        	
        	MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
        	this.txt_fuse.setText("" + SpawnerUtil.getCreeperFuse(spawnerLogic, potentialSpawnIndex));
			this.txt_radius.setText("" + SpawnerUtil.getExplosionRadius(spawnerLogic, potentialSpawnIndex));
        }
        else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = gui.getEntity();
			if(entity instanceof EntityCreeper) {
				EntityCreeper creeper = (EntityCreeper)gui.getEntity();
				//this.txt_fuse.setText(Spawner);
				//this.txt_radius.setText(Spawner);
			}
		}
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            if(button.id == 0) {
                PacketDispatcher.sendToServer(new PacketCreeperProperties(this.txt_fuse.getText(), this.txt_radius.getText(), potentialSpawnIndex));
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
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.creeperproperties.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IFilterGui gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        gui.getFont().drawString("Fuse Time", topX + 20, topY + 25, 4210752);
        gui.getFont().drawString("Explosion Radius", topX + 120, topY + 25, 4210752);
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		fuseText = this.txt_fuse.getText();
		radiusText = this.txt_radius.getText();
		this.btn_ok.enabled = Numbers.areIntegers(fuseText, radiusText);
	}
	
	@Override
	public boolean showErrorIcon(IFilterGui gui) {
		if(SpawnerUtil.isSpawner(gui)) {
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			
			List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawnerLogic);
			if(minecarts.size() <= potentialSpawnIndex) return true;
			WeightedSpawnerEntity randomMinecart = minecarts.get(potentialSpawnIndex);
			String mobId = SpawnerUtil.getMinecartType(randomMinecart).toString();
			if(mobId.equals("minecraft:creeper"))
				return false;
		
			return true; 
		}
		
		return false;
	}
	
	@Override
	public String getErrorMessage(IFilterGui gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.creeperproperties.error");
	}
}
