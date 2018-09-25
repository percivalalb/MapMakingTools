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
	
	public static NBTTagList getOrCreatePotionList(ItemStack item) {
		return NBTUtil.getOrCreateSubList(item, POTION_TAG, NBTUtil.ID_COMPOUND);
	}
	
	public static void setPotionEffects(ItemStack item, int id, int level, int duration, boolean ambient, boolean showParticles) {
		getOrCreatePotionList(item);
		
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
		NBTTagList potionList = getOrCreatePotionList(item);
		
		NBTTagCompound potion = new NBTTagCompound();
		potion.setByte(POTION_ID, (byte)id);
		potion.setByte(POTION_AMPLIFIER, (byte)(level - 1));
		potion.setInteger(POTION_DURATION, duration);
		potion.setBoolean(POTION_AMBIENT, ambient);
		potion.setBoolean(POTION_PARTICLES, showParticles);
		potionList.appendTag(potion);
	}
}
