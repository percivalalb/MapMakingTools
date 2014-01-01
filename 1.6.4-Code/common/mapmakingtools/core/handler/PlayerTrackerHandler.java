package mapmakingtools.core.handler;

import mapmakingtools.core.util.DataStorage;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * @author ProPercivalalb
 */
public class PlayerTrackerHandler implements IPlayerTracker {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		DataStorage.setPlayerLeftClick(player, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED);
		DataStorage.setPlayerRightClick(player, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED);
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		
	}

}
