package mapmakingtools.core.helper;

import com.google.common.base.Strings;

/**
 * @author ProPercivalalb
 */
public class QuickBuildHelper {
	
	/**
	 * Checks whether the player has typed in the command correctly.
	 * @param ids Will look like this... "blockId:blockMeta" or "blockId"
	 * @return Whether these are integers
	 */
	public static boolean isValidIds(String ids) {
		if(Strings.isNullOrEmpty(ids) || ids.endsWith(":")) return false;
		String[] splitString = ids.split(":");
		String blockId = splitString[0];
		String blockMeta = "0";
		if(splitString.length == 2) {
			blockMeta = splitString[1];
		}
		boolean validId = false;
		boolean validMeta = false;
		try {
			new Integer(blockId);
			validId = true;
		}
		catch(Exception e) {
			validId = false;
		}
		try {
			new Integer(blockMeta);
			validMeta = true;
		}
		catch(Exception e) {
			validMeta = false;
		}
		return validId && validMeta;
	}
	
	public static int[] convertIdString(String ids) {
		if(Strings.isNullOrEmpty(ids) || ids.endsWith(":")) return new int[] {0, 0};
		String[] splitString = ids.split(":");
		String blockId = splitString[0];
		String blockMeta = "0";
		if(splitString.length == 2) {
			blockMeta = splitString[1];
		}
		int id = 0;
		int meta = 0;
		try {
			id = new Integer(blockId);
		}
		catch(Exception e) {}
		try {
			meta = new Integer(blockMeta);
		}
		catch(Exception e) {}
		return new int[] {id, meta};
	}
	
	public static int getTypeFromRotation(int rotation) {
		if(rotation % 90 != 0) {
			rotation = 0;
		}
		return rotation / 90;
	}
}
