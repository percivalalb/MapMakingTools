package mapmakingtools;

import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.Map;

import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.item.ItemEdit;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;
import net.minecraftforge.common.ForgeVersion;

import com.google.common.collect.BiMap;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author ProPercivalalb
 */
public class ModItems {

	private static String WOODEN_AXE = "minecraft:wooden_axe";
	public static Item editItem;
	
	public static void inti() {
		replace(WOODEN_AXE, editItem = new ItemEdit());
	}
	
	public static void replace(String name, Item thing) {
		if(ForgeVersion.getBuildVersion() >= 1056) {
			FMLControlledNamespacedRegistry<Item> itemRegistry = ReflectionHelper.invokeMethod(GameData.class, "getItemRegistry", FMLControlledNamespacedRegistry.class, null, new Class[0], new Object[0]);
			GameData data = ReflectionHelper.invokeMethod(GameData.class, "getMain", GameData.class, null, new Class[0], new Object[0]);
			BitSet availabilityMap = ReflectionHelper.getField(GameData.class, BitSet.class, data, "availabilityMap");
			ObjectIntIdentityMap map = ReflectionHelper.getField(RegistryNamespaced.class, ObjectIntIdentityMap.class, itemRegistry, 0);
		    Map registryObjects = ReflectionHelper.getField(RegistrySimple.class, Map.class, itemRegistry, 1);
		    int id = Item.itemRegistry.getIDForObject(Items.wooden_axe);
		    registryObjects.put(name, thing);
		    map.func_148746_a(thing, id);
		}
		else {
			BiMap namedIds = (BiMap)ReflectionHelper.getField(FMLControlledNamespacedRegistry.class, BiMap.class, GameData.itemRegistry, "namedIds");
		    ObjectIntIdentityMap map = (ObjectIntIdentityMap)ReflectionHelper.getField(RegistryNamespaced.class, ObjectIntIdentityMap.class, GameData.itemRegistry, 0);
		    Map registryObjects = (Map)ReflectionHelper.getField(RegistrySimple.class, Map.class, GameData.itemRegistry, 1);
		    int id = GameData.itemRegistry.getId(name);

		    namedIds.forcePut(name, Integer.valueOf(id));
		    registryObjects.remove(name);
		    registryObjects.put(name, thing);
		    map.func_148746_a(thing, id);
		}
	    
        FMLLog.info("[MapMakingTools] Replaced %s with a custom verson for use of wrench", name);
    }
}
