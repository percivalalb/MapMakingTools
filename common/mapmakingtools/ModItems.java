package mapmakingtools;

import java.util.BitSet;
import java.util.Map;

import com.google.common.collect.BiMap;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.item.ItemEdit;
import net.minecraft.item.Item;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;

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
		BiMap namedIds = ReflectionHelper.getField(FMLControlledNamespacedRegistry.class, BiMap.class, GameData.itemRegistry, "namedIds");
	    ObjectIntIdentityMap map = ReflectionHelper.getField(RegistryNamespaced.class, ObjectIntIdentityMap.class, GameData.itemRegistry, 0);
	    Map registryObjects = ReflectionHelper.getField(RegistrySimple.class, Map.class, GameData.itemRegistry, 1);
	    int id = GameData.itemRegistry.getId(WOODEN_AXE);
	    
        namedIds.forcePut(name, id);
        registryObjects.remove(name);
        GameData.itemRegistry.putObject(name, thing);
        map.func_148746_a(thing, id);
        
        FMLLog.info("[MapMakingTools] Replaced %s with a custom verson for use of wrench", name);
    }
}
