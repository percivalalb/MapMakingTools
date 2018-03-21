package mapmakingtools.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
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
	 public static boolean makeSureItemHasTagCompound(ItemStack item) {
		 if(!item.hasTagCompound()) {
			 item.setTagCompound(new NBTTagCompound());
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
	 
	 public static void setDamage(ItemStack item, int damage) {
		 item.setItemDamage(damage);
	 }
	 
	 public static boolean hasTag(ItemStack itemStack, String keyName) {
	        if (itemStack.getTagCompound() != null)
	            return itemStack.getTagCompound().hasKey(keyName);
	        return false;
	 }

	 public static void removeTag(ItemStack itemStack, String keyName) {
		 if (itemStack.getTagCompound() != null) {
			 itemStack.getTagCompound().removeTag(keyName);
	     }
	 }

	 //String
	 public static String getString(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setString(itemStack, keyName, "");
	     }
	     return itemStack.getTagCompound().getString(keyName);
	 }

	 public static void setString(ItemStack itemStack, String keyName, String keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setString(keyName, keyValue);
	 }

	 //Boolean
	 public static boolean getBoolean(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setBoolean(itemStack, keyName, false);
	     }
	     return itemStack.getTagCompound().getBoolean(keyName);
	 }

	 public static void setBoolean(ItemStack itemStack, String keyName, boolean keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setBoolean(keyName, keyValue);
	 }

	 //Byte
	 public static byte getByte(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setByte(itemStack, keyName, (byte) 0);
	     }
	     return itemStack.getTagCompound().getByte(keyName);
	 }

	 public static void setByte(ItemStack itemStack, String keyName, byte keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setByte(keyName, keyValue);
	 }

	 //Short
	 public static short getShort(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setShort(itemStack, keyName, (short) 0);
	     }
	     return itemStack.getTagCompound().getShort(keyName);
	 }

	 public static void setShort(ItemStack itemStack, String keyName, short keyValue) {
	    makeSureItemHasTagCompound(itemStack);
	    itemStack.getTagCompound().setShort(keyName, keyValue);
	 }

	 //Integer
	 public static int getInt(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setInteger(itemStack, keyName, 0);
	     }
	     return itemStack.getTagCompound().getInteger(keyName);
	 }

	 public static void setInteger(ItemStack itemStack, String keyName, int keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setInteger(keyName, keyValue);
	 }

	 //Long
	 public static long getLong(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setLong(itemStack, keyName, 0);
	     }
	     return itemStack.getTagCompound().getLong(keyName);
	 }

	 public static void setLong(ItemStack itemStack, String keyName, long keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setLong(keyName, keyValue);
	 }

	 //Float
	 public static float getFloat(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setFloat(itemStack, keyName, 0);
	     }
	     return itemStack.getTagCompound().getFloat(keyName);
	 }

	 public static void setFloat(ItemStack itemStack, String keyName, float keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setFloat(keyName, keyValue);
	 }

	 //Double
	 public static double getDouble(ItemStack itemStack, String keyName) {
		 makeSureItemHasTagCompound(itemStack);
	     if (!itemStack.getTagCompound().hasKey(keyName)) {
	    	 setDouble(itemStack, keyName, 0);
	     }
	     return itemStack.getTagCompound().getDouble(keyName);
	 }

	 public static void setDouble(ItemStack itemStack, String keyName, double keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setDouble(keyName, keyValue);
	 }
	 
	 //Tag
	 public static NBTBase getTag(ItemStack itemStack, String keyName) {
		 if (!itemStack.getTagCompound().hasKey(keyName)) {
			 return null;
		 }
		 return itemStack.getTagCompound().getTag(keyName);
	 }
	 
	 public static void setTag(ItemStack itemStack, String keyName, NBTBase keyValue) {
		 makeSureItemHasTagCompound(itemStack);
	     itemStack.getTagCompound().setTag(keyName, keyValue);
	 }

	 //Other more specific NBT data
	 public static void setPotionEffects(ItemStack itemStack, int id, int level, int duration, boolean ambient) {
		 makeSureItemHasTagCompound(itemStack);
		 /**NBTTagList var4 = (NBTTagList)itemStack.getTagCompound().getTag(NBTData.POTION_TAG);
		 NBTTagCompound var5 = new NBTTagCompound();
		 var5.setByte(NBTData.POTION_ID, (byte)id);
		 var5.setByte(NBTData.POTION_AMPLIFIER, (byte)(level - 1));
		 var5.setInteger(NBTData.POTION_DURATION, duration * 20);
		 var5.setBoolean(NBTData.POTION_AMBIENT, ambient);
		 var4.appendTag(var5);**/
	 }
}
