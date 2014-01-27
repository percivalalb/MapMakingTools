package mapmakingtools.api;

import java.util.List;

import mapmakingtools.client.gui.GuiMinecartIndexButton;
import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterClientSpawner extends IFilterClient {

	public static int minecartIndex = 0;
	public int minecartsCount = 1;
	
	public void addMinecartButtons(IGuiFilter gui, int topX, int topY) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
		
		if(minecarts == null)
			return;

		this.minecartsCount = minecarts.size();
		
		int i = 0;
		for(WeightedRandomMinecart randomMinecart : minecarts) {
			GuiMinecartIndexButton button = new GuiMinecartIndexButton(200 + i, topX + 18 * i + 4, topY - 13, 13, 12, "" + i);
			if(i != minecartIndex)
				button.field_146124_l = false;
			gui.getButtonList().add(button);
			++i;
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if(button.field_146127_k >= 200 && button.field_146127_k <= 200 + minecartsCount) {
			minecartIndex = button.field_146127_k - 200;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.field_146127_k >= 200 && tempButton.field_146127_k <= 200 + minecartsCount) {
					if(tempButton.field_146127_k - 200 != minecartIndex)
						tempButton.field_146124_l = false;
					else
						tempButton.field_146124_l = true;
				}
			}
		}
	}
}
