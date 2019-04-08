package mapmakingtools.tools.item.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {

	public static final int ID_BYTE = 1;
	public static final int ID_INTEGER = 3;
	public static final int ID_DOUBLE = 6;
	public static final int ID_STRING = 8;
	public static final int ID_LIST = 9;
	public static final int ID_COMPOUND = 10;
	public static final int ID_NUMBER = 99;
	
	public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
		if(!stack.hasTag())
            stack.setTag(new NBTTagCompound());
		
		return stack.getTag();
	}
	
	/** Checks if base NBTTagCompound has any tags if not it removes it */
	public static boolean hasEmptyTagCompound(ItemStack stack, boolean takeAction) {
		if(!stack.hasTag() || stack.getTag().isEmpty()) {
			if(takeAction) stack.setTag(null);
			return true;
		}
		
		return false;
	}
	
	public static NBTTagList getOrCreateSubList(ItemStack stack, String key, int tagId) {
		if(hasTag(stack, key, ID_LIST))
            return stack.getTag().getList(key, tagId);
        else {
        	NBTTagList tagList = new NBTTagList();
            stack.setTagInfo(key, tagList);
            return tagList;
        }
	}
	
	public static void removeSubList(ItemStack stack, String key) {
		remove(stack, key, ID_LIST);
    }
	
	public static void removeFromSubList(ItemStack stack, String key, int tagId, int tagIndex) {
		if(hasTag(stack, key, ID_LIST)) {
	        NBTTagList tagList = stack.getTag().getList(key, tagId);
	        tagList.remove(tagIndex);
	        
	        if(tagList.isEmpty())
	        	removeSubList(stack, key);
		}
    }
	
	public static void removeKeyFromSubList(ItemStack stack, String key) {
		//TODO
    }
	
	
	
	
	
	
	public static boolean contains(NBTTagList tagList, String value) {
		if(tagList.getTagType() != ID_STRING) return false;
		
		for(int i = 0; i < tagList.size(); i++)
			if(tagList.getString(i).equals(value))
				return true;
		
		return false;
	}
	
	public static NBTTagCompound getOrCreateChildTag(ItemStack stack, String key) {
		if(hasTag(stack, key, ID_COMPOUND))
            return stack.getTag().getCompound(key);
        else {
        	NBTTagCompound tagList = new NBTTagCompound();
            stack.setTagInfo(key, tagList);
            return tagList;
        }
	}
	
	public static void removeSubCompound(ItemStack stack, String key) {
		remove(stack, key, ID_COMPOUND);
    }
	
	public static void removeFromSubCompound(ItemStack stack, String key, int tagId, String tagKey) {
		if(hasTag(stack, key, ID_COMPOUND)) {
			NBTTagCompound subCompound = stack.getTag().getCompound(key);
			
			if(subCompound.contains(tagKey, tagId))
				subCompound.remove(tagKey);
	        
	        if(subCompound.isEmpty())
	        	removeSubCompound(stack, key);
		}
    }
	
	public static boolean hasTagInSubCompound(ItemStack stack, String key, String key2, int tagId) {
		return hasTag(stack, key, ID_COMPOUND) && stack.getTag().getCompound(key).contains(key2, tagId);
    }
	
	public static byte getByteInSubCompound(ItemStack stack, String key, String key2) {
		return stack.getTag().getCompound(key).getByte(key2);
    }
	
	public static NBTTagList getListInSubCompound(ItemStack stack, String key, String key2, int tagId) {
		return stack.getTag().getCompound(key).getList(key2, tagId);
    }
	
	
	
	public static void remove(ItemStack stack, String key, int tagId) {
		if(hasTag(stack, key, tagId))
            stack.getTag().remove(key);
    }
	
	public static boolean hasTag(ItemStack stack, String key, int tagId) {
		return stack.hasTag() && stack.getTag().contains(key, tagId);
    }
}
