package mapmakingtools.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class ItemStackHelper {

	/**
	 * Converts an object to and ItemStack. Check to see whether it is 
	 * not null before using the returned object.
	 * @param obj A data type that can be converted to and ItemStack
	 * @return An ItemStack created from the given object.
	 */
	 public static ItemStack convertObjectToItemStack(Object obj) {
		 if (obj instanceof Item)
	         return new ItemStack((Item)obj);
	     else if (obj instanceof Block)
	         return new ItemStack((Block)obj);
	     else if (obj instanceof ItemStack)
	         return ((ItemStack)obj).copy();
	     else
	    	 return null;
	 }
	 
	 /**
	  * Checks whether the item has and tag compound if not it creates a new 
	  * one for that itemstack.
	  * @param item The item needed to be checked for a #NBTTagCompound
	  * @return Returns whether it has created a new #NBTTagCompound
	  */
	 public static boolean makeSureItemhasTag(ItemStack item) {
		 if(!item.hasTag()) {
			 item.setTag(new NBTTagCompound());
			 return true;
		 }
		 return false;
	 }
	 
	 public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
		 if (stack1 == null || stack2 == null)
			 return false;
		 if (!stack1.isItemEqual(stack2))
			 return false;
		 if (!ItemStack.areItemStackTagsEqual(stack1, stack2))
			 return false;
		 return true;

	}

	public static boolean hasTag(ItemStack itemStack, String keyName) {
		if (itemStack.getTag() != null)
			return itemStack.getTag().contains(keyName);
		return false;
	}

	public static void remove(ItemStack itemStack, String keyName) {
		if (itemStack.getTag() != null) {
			itemStack.getTag().remove(keyName);
		}
	}

	 //String
	 public static String getString(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putString(itemStack, keyName, "");
	     }
	     return itemStack.getTag().getString(keyName);
	 }

	 public static void putString(ItemStack itemStack, String keyName, String keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putString(keyName, keyValue);
	 }

	 //Boolean
	 public static boolean getBoolean(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putBoolean(itemStack, keyName, false);
	     }
	     return itemStack.getTag().getBoolean(keyName);
	 }

	 public static void putBoolean(ItemStack itemStack, String keyName, boolean keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putBoolean(keyName, keyValue);
	 }

	 //Byte
	 public static byte getByte(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putByte(itemStack, keyName, (byte) 0);
	     }
	     return itemStack.getTag().getByte(keyName);
	 }

	 public static void putByte(ItemStack itemStack, String keyName, byte keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putByte(keyName, keyValue);
	 }

	 //Short
	 public static short getShort(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putShort(itemStack, keyName, (short) 0);
	     }
	     return itemStack.getTag().getShort(keyName);
	 }

	 public static void putShort(ItemStack itemStack, String keyName, short keyValue) {
	    makeSureItemhasTag(itemStack);
	    itemStack.getTag().putShort(keyName, keyValue);
	 }

	 //Integer
	 public static int getInt(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putInt(itemStack, keyName, 0);
	     }
	     return itemStack.getTag().getInt(keyName);
	 }

	 public static void putInt(ItemStack itemStack, String keyName, int keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putInt(keyName, keyValue);
	 }

	 //Long
	 public static long getLong(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putLong(itemStack, keyName, 0);
	     }
	     return itemStack.getTag().getLong(keyName);
	 }

	 public static void putLong(ItemStack itemStack, String keyName, long keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putLong(keyName, keyValue);
	 }

	 //Float
	 public static float getFloat(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putFloat(itemStack, keyName, 0);
	     }
	     return itemStack.getTag().getFloat(keyName);
	 }

	 public static void putFloat(ItemStack itemStack, String keyName, float keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putFloat(keyName, keyValue);
	 }

	 //Double
	 public static double getDouble(ItemStack itemStack, String keyName) {
		 makeSureItemhasTag(itemStack);
	     if (!itemStack.getTag().contains(keyName)) {
	    	 putDouble(itemStack, keyName, 0);
	     }
	     return itemStack.getTag().getDouble(keyName);
	 }

	 public static void putDouble(ItemStack itemStack, String keyName, double keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().putDouble(keyName, keyValue);
	 }
	 
	 //Tag
	 public static INBTBase getTag(ItemStack itemStack, String keyName) {
		 if (!itemStack.getTag().contains(keyName)) {
			 return null;
		 }
		 return itemStack.getTag().get(keyName);
	 }
	 
	 public static void setTag(ItemStack itemStack, String keyName, INBTBase keyValue) {
		 makeSureItemhasTag(itemStack);
	     itemStack.getTag().put(keyName, keyValue);
	 }

	 //Other more specific NBT data
	 public static void setPotionEffects(ItemStack itemStack, int id, int level, int duration, boolean ambient) {
		 makeSureItemhasTag(itemStack);
		 /**NBTTagList var4 = (NBTTagList)itemStack.getTag().getTag(NBTData.POTION_TAG);
		 NBTTagCompound var5 = new NBTTagCompound();
		 var5.putByte(NBTData.POTION_ID, (byte)id);
		 var5.putByte(NBTData.POTION_AMPLIFIER, (byte)(level - 1));
		 var5.putInt(NBTData.POTION_DURATION, duration * 20);
		 var5.putBoolean(NBTData.POTION_AMBIENT, ambient);
		 var4.add(var5);**/
	 }
}
