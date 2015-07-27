package mapmakingtools.tools.item.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class PotionNBT {

	public static final String POTION_TAG = "CustomPotionEffects";
	public static final String POTION_ID = "Id";
	public static final String POTION_AMPLIFIER = "Amplifier";
	public static final String POTION_DURATION = "Duration";
	public static final String POTION_AMBIENT = "Ambient";
	public static final String POTION_PARTICLES = "ShowParticles";
	
	public static void checkHasTag(ItemStack item) {
		if(!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		if(!item.getTagCompound().hasKey(POTION_TAG, 9))
			item.getTagCompound().setTag(POTION_TAG, new NBTTagList());
	}
	
	public static void setPotionEffects(ItemStack item, int id, int level, int duration, boolean ambient, boolean showParticles) {
		checkHasTag(item); 
		NBTTagList potionList = new NBTTagList();
		NBTTagCompound potion = new NBTTagCompound();
		potion.setByte(POTION_ID, (byte)id);
		potion.setByte(POTION_AMPLIFIER, (byte)(level - 1));
		potion.setInteger(POTION_DURATION, duration);
		potion.setBoolean(POTION_AMBIENT, ambient);
		potion.setBoolean(POTION_PARTICLES, showParticles);
		potionList.appendTag(potion);
		item.getTagCompound().setTag(POTION_TAG, potionList);
	}
	
	public static void addPotionEffects(ItemStack item, int id, int level, int duration, boolean ambient, boolean showParticles) {
		checkHasTag(item);  
		NBTTagList potionList = (NBTTagList)item.getTagCompound().getTag(POTION_TAG);
		NBTTagCompound potion = new NBTTagCompound();
		potion.setByte(POTION_ID, (byte)id);
		potion.setByte(POTION_AMPLIFIER, (byte)(level - 1));
		potion.setInteger(POTION_DURATION, duration);
		potion.setBoolean(POTION_AMBIENT, ambient);
		potion.setBoolean(POTION_PARTICLES, showParticles);
		potionList.appendTag(potion);
	}
}
