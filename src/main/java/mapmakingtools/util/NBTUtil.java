package mapmakingtools.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.function.Function;

public class NBTUtil {

    /**
     * Returns the stack's tag or if it does not have a tag
     * or current tag is empty it creates new object
     */
    @Deprecated // Use ItemStack.getOrCreateTag
    public static CompoundTag getOrCreateTag(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        return stack.getTag();
    }

    @Deprecated // Use ItemStack.getOrCreateChildTag
    public static CompoundTag getOrCreateSubCompound(ItemStack stack, String key) {
        if (hasTag(stack, key, Tag.TAG_COMPOUND))
            return stack.getTag().getCompound(key);
        else {
            CompoundTag tagList = new CompoundTag();
            stack.addTagElement(key, tagList);
            return tagList;
        }
    }

    /**
     * Returns the stack's tag or if it does not have a tag
     * or current tag is empty it creates new object
     */
    public static ListTag getOrCreateSubList(ItemStack stack, String key, int tagId) {
        if (hasTag(stack, key, Tag.TAG_LIST))
            return stack.getTag().getList(key, tagId);
        else {
            ListTag tagList = new ListTag();
            stack.addTagElement(key, tagList);
            return tagList;
        }
    }

    /**
     * Removes the stack's tag if it is empty, returns a boolean if
     * any action was performed
     */
    public static boolean removeTagIfEmpty(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().isEmpty()) {
            stack.setTag(null);
            return true;
        }

        return false;

    }

    // Only adds if it is not already contained
    public static boolean addToSet(ListTag nbt, Object value, int type) {
        boolean contains = false;
        Function<Object, Tag> f = toHolder(type);
        Tag holder = f.apply(value);
        for (int i = 0; i < nbt.size(); i++) {
            if (nbt.get(i).equals(holder)) {
                contains = true;
                break;
            }
        }

        if (!contains) {
            nbt.add(holder);
            return true;
        }

        return false;
    }

    public static Function<Object, Tag> toHolder(int type) {
        switch (type) {
        case Tag.TAG_STRING:
            return (v) -> StringTag.valueOf(v.toString());
        default: throw new RuntimeException();
        }
    }

    @Deprecated // Use ItemStack.removeChildTag, Note also removes empty tags afterwards
    public static void removeSubList(ItemStack stack, String key) {
        removeTag(stack, key, Tag.TAG_LIST);
    }

    public static void removeTagFromSubList(ItemStack stack, String key, int tagId, int tagIndex) {
        if (hasTag(stack, key, Tag.TAG_LIST)) {
            ListTag tagList = stack.getTag().getList(key, tagId);
            tagList.remove(tagIndex);

            if (tagList.isEmpty())
                removeSubList(stack, key);
        }
    }





    public static boolean contains(ListTag tagList, String value) {
        if (tagList.getElementType() != Tag.TAG_STRING) return false;

        for (int i = 0; i < tagList.size(); i++)
            if (tagList.getString(i).equals(value))
                return true;

        return false;
    }

    @Deprecated // Use ItemStack.removeChildTag, Note also removes empty tags afterwards
    public static void removeSubCompound(ItemStack stack, String key) {
        removeTag(stack, key, Tag.TAG_COMPOUND);
    }

    public static void removeTagFromSubCompound(ItemStack stack, String key, int tagId, String tagKey) {
        if (hasTag(stack, key, Tag.TAG_COMPOUND)) {
            CompoundTag subCompound = stack.getTag().getCompound(key);

            if (subCompound.contains(tagKey, tagId))
                subCompound.remove(tagKey);

            if (subCompound.isEmpty())
                removeSubCompound(stack, key);
        }
    }

    public static boolean hasTagInSubCompound(ItemStack stack, String key, String key2, int tagId) {
        return hasTag(stack, key, Tag.TAG_COMPOUND) && stack.getTag().getCompound(key).contains(key2, tagId);
    }

    public static byte getByteInSubCompound(ItemStack stack, String key, String key2) {
        return stack.getTag().getCompound(key).getByte(key2);
    }

    public static int getIntInSubCompound(ItemStack stack, String key, String key2) {
        return stack.getTag().getCompound(key).getInt(key2);
    }

    public static ListTag getListInSubCompound(ItemStack stack, String key, String key2, int tagId) {
        return stack.getTag().getCompound(key).getList(key2, tagId);
    }

    public static int getInt(ItemStack stack, String key) {
        if (!stack.hasTag() || stack.getTag().isEmpty()) {
            return 0;
        }

        return stack.getTag().getInt(key);
    }




    @Deprecated // Use ItemStack.removeChildTag, Note also removes empty tags afterwards
    public static void removeTag(ItemStack stack, String key, int tagId) {
        if (hasTag(stack, key, tagId))
            stack.getTag().remove(key);
    }

    public static boolean hasTag(ItemStack stack, String key, int tagId) {
        return stack.hasTag() && stack.getTag().contains(key, tagId);
    }

    public static boolean hasTag(ItemStack stack, String key) {
        return stack.hasTag() && stack.getTag().contains(key);
    }
}
