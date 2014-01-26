package mapmakingtools.api;

import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterClientSpawner extends IFilterClient {

	public int minecartIndex = -1;
	
	public void addMinecartButtons(IGuiFilter gui, int topX, int topY) {
		TileEntity tile = gui.getWorld().func_147438_o(gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		for(WeightedRandomMinecart randomMinecart : SpawnerUtil.getRandomMinecarts(spawner.func_145881_a())) {
			gui.getButtonList().add(new GuiSmallButton(156, topX + 18, topY + 4, 13, 12, "?"));
		}
	}
	
}
