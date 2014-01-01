package mapmakingtools;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.ItemData;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.item.ItemEdit;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class ModItems {

	public static void inti() {
		overrideItem(Item.axeWood, "mapmakingtools.item.ItemEdit", new Class[]{});
	}
	
	public static void overrideItem(Item oldItem, String itemClassPath, Class<?>[] classParam, Object... param) {
		Map<Integer, ItemData> idMap = ReflectionHelper.getField(GameData.class, Map.class, null, 0);
		Item.itemsList[oldItem.itemID] = null;
		idMap.remove(oldItem.itemID);
		Constructor<Item> itemCtor;
        Item newItem;
        try {
            Class itemClass = Class.forName(itemClassPath);
            itemCtor = itemClass.getConstructor(classParam);
            newItem = itemCtor.newInstance(param);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
		
		ModContainer mc = null;
        if (mc == null) {
            mc = Loader.instance().getMinecraftModContainer();
            if (Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
                FMLLog.severe("It appears something has tried to allocate an Item outside of the initialization phase of Minecraft, this could be very bad for your network connectivity.");
            }
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ModId", mc.getModId());
        tag.setString("ItemType", oldItem.getClass().getName());
        tag.setInteger("ItemId", oldItem.itemID);
        tag.setInteger("ordinal", 1);
        
        
        ItemData itemData = new ItemData(tag);
        idMap.put(oldItem.itemID, itemData);
        LogHelper.logInfo("Overrided wooden axe for 'Quick Build' system in Map Making Tools");
        
	}
}
