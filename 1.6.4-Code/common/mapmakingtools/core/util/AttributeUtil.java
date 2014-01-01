package mapmakingtools.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import mapmakingtools.core.helper.ItemStackHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public class AttributeUtil {

	public static void addDefaultAttributesAsNBT(ItemStack stack) {
		if(stack == null)
			return;
		if(stack.hasTagCompound() && stack.stackTagCompound.hasKey("AttributeModifiers"))
			return;
		Multimap multimap = stack.getItem().getItemAttributeModifiers();
		if (multimap.isEmpty())
			return;
		
		ItemStackHelper.makeSureItemHasTagCompound(stack);
		NBTTagList attributeList = new NBTTagList();
		
		Iterator iterator = multimap.entries().iterator();
	    while (iterator.hasNext()) {
	    	Entry entry = (Entry)iterator.next();
	        AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
	        NBTTagCompound tag = writeAttriuteToNBT(attributeModifier);
	        tag.setString("AttributeName", (String)entry.getKey());
	        attributeList.appendTag(tag);
	    }
		
		stack.stackTagCompound.setTag("AttributeModifiers", attributeList);
	}
	
	
	private static NBTTagCompound writeAttriuteToNBT(AttributeModifier par0AttributeModifier) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
	    nbttagcompound.setString("Name", par0AttributeModifier.getName());
	    nbttagcompound.setDouble("Amount", par0AttributeModifier.getAmount());
	    nbttagcompound.setInteger("Operation", par0AttributeModifier.getOperation());
	    nbttagcompound.setLong("UUIDMost", par0AttributeModifier.getID().getMostSignificantBits());
	    nbttagcompound.setLong("UUIDLeast", par0AttributeModifier.getID().getLeastSignificantBits());
	    return nbttagcompound;
	}
	
	public static void setDamageAttribute(ItemStack stack, double damage) {
		if(stack == null)
			return;
		
		addDefaultAttributesAsNBT(stack);
		ItemStackHelper.makeSureItemHasTagCompound(stack);
		
		NBTTagCompound damageTag = findAttributeTag(stack, SharedMonsterAttributes.attackDamage);
		damageTag.setDouble("Amount", damage);
		
		NBTTagCompound followTag = findAttributeTag(stack, SharedMonsterAttributes.followRange);
		followTag.setDouble("Amount", damage);
	}
	
	public static NBTTagCompound findAttributeTag(ItemStack stack, Attribute attribute) {
		
		if(stack.hasTagCompound() && stack.stackTagCompound.hasKey("AttributeModifiers")) {
			NBTTagList tagList = (NBTTagList)stack.stackTagCompound.getTag("AttributeModifiers");
			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tag = (NBTTagCompound)tagList.tagAt(i);
				if(tag.hasKey("AttributeName") && tag.getString("AttributeName").equals(attribute.getAttributeUnlocalizedName())) {
					return tag;
				}
			}
		}
		
		return new NBTTagCompound();
	}
	
}
