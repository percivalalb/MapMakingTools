package mapmakingtools.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {

	public static final int ID_BOOLEAN = 1;
	public static final int ID_INTEGER = 3;
	public static final int ID_STRING = 8;
	public static final int ID_LIST = 9;
	public static final int ID_COMPOUND = 10;
	public static final int ID_NUMBER = 99;
	
	public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
		if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
		
		return stack.getTagCompound();
	}
	
	/** Checks if base NBTTagCompound has any tags if not it removes it */
	public static boolean hasEmptyTagCompound(ItemStack stack, boolean takeAction) {
		if(!stack.hasTagCompound() || stack.getTagCompound().hasNoTags()) {
			if(takeAction) stack.setTagCompound(null);
			return true;
		}
		
		return false;
	}
	
	public static NBTTagList getOrCreateSubList(ItemStack stack, String key, int tagId) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_LIST))
            return stack.getTagCompound().getTagList(key, tagId);
        else {
        	NBTTagList tagList = new NBTTagList();
            stack.setTagInfo(key, tagList);
            return tagList;
        }
	}
	
	public static void removeSubList(ItemStack stack, String key) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_LIST))
            stack.getTagCompound().removeTag(key);
    }
	
	public static void removeTagFromSubList(ItemStack stack, String key, int tagId, int tagIndex) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_LIST)) {
	        NBTTagList tagList = stack.getTagCompound().getTagList(key, tagId);
	        tagList.removeTag(tagIndex);
	        
	        if(tagList.hasNoTags())
	        	removeSubList(stack, key);
		}
    }
	
	public static void removeKeyFromSubList(ItemStack stack, String key) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_LIST))
            stack.getTagCompound().removeTag(key);
    }
	
	public static boolean contains(NBTTagList tagList, String value) {
		if(tagList.getTagType() != ID_STRING) return false;
		
		for(int i = 0; i < tagList.tagCount(); i++)
			if(tagList.getStringTagAt(i).equals(value))
				return true;
		
		return false;
	}
	
	public static void removeSubCompound(ItemStack stack, String key) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_COMPOUND))
            stack.getTagCompound().removeTag(key);
    }
	
	public static void removeTagFromSubCompound(ItemStack stack, String key, int tagId, String tagKey) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(key, ID_COMPOUND)) {
			NBTTagCompound subCompound = stack.getTagCompound().getCompoundTag(key);
			
			if(subCompound.hasKey(tagKey, tagId))
				subCompound.removeTag(tagKey);
	        
	        if(subCompound.hasNoTags())
	        	removeSubCompound(stack, key);
		}
    }
}
