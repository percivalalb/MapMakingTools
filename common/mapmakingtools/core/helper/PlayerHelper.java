package mapmakingtools.core.helper;

import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PlayerHelper {

	/**
	 * @param player The player to check
	 * @return True if the player is sneaking
	 */
	public static boolean isSneaking(EntityPlayer player) {
		return player.isSneaking();
	}
	
	public static String usernameLowerCase(EntityPlayer player) {
		return player == null ? "ERROR" : player.username.toLowerCase();
	}
	
	public static void addChatMessage(EntityPlayer player, String message) {
		if(player == null || message == null) return;
		player.addChatMessage(message);
	}
}
