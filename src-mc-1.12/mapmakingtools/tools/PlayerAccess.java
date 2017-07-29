package mapmakingtools.tools;

import mapmakingtools.lib.Constants;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PlayerAccess {

	public static boolean canEdit(EntityPlayer player) {
		if(player == null)
			return false;
		
		boolean isCreativeMode = player.capabilities.isCreativeMode || !Constants.HAS_TO_BE_CREATIVE;
		
		return isCreativeMode;
	}
	
	public static boolean canSeeBlockIdHelper(EntityPlayer player) {
		return canEdit(player) && Constants.SHOULD_SHOW_BLOCK_ID_HELPER;
	}
}
