package doggytalents.api.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import doggytalents.ModBlocks;
import doggytalents.api.inferface.DefaultDogBedIcon;
import doggytalents.api.inferface.IDogBedIcon;
import doggytalents.helper.LogHelper;

/**
 * @author ProPercivalalb
 */
public class DogBedRegistry {

	public final static DogBedRegistry CASINGS = new DogBedRegistry("casing");
	public final static DogBedRegistry BEDDINGS = new DogBedRegistry("bedding");
	
	private final List<String> keys = new ArrayList<String>();
	private final Map<String, String> lookupnames = new HashMap<String, String>();
	private final Map<String, IDogBedIcon> icons = new HashMap<String, IDogBedIcon>();
	private final Map<String, ItemStack> craftingItems = new HashMap<String, ItemStack>();
	private final String key;
	
	public DogBedRegistry(String key) {
		this.key = key;
	}
	
	public boolean isValidId(String id) {
		return this.keys.contains(id);
	}
	
	public void registerMaterial(String blockId) { this.registerMaterial(blockId, 0); }
	public void registerMaterial(Block block) { this.registerMaterial(block, 0); }
	
	public void registerMaterial(String blockId, int meta) {
		if(Block.blockRegistry.containsKey(blockId))
			LogHelper.warning("The block id %s does not exist for a material", blockId);
		else {
			Block block = Block.getBlockFromName(blockId);
			String lookupname = String.format("dogbed.%s.%s.%d", this.key, blockId, meta);
			ItemStack stack = new ItemStack(block, 1, meta);
			this.registerMaterial(blockId + "." + meta, lookupname, new DefaultDogBedIcon(block, meta), stack);
		}
	}
	
	public void registerMaterial(Block block, int meta) {
		String blockId = Block.blockRegistry.getNameForObject(block);
		String lookupname = String.format("dogbed.%s.%s.%d", this.key, blockId, meta);
		ItemStack stack = new ItemStack(block, 1, meta);
		this.registerMaterial(blockId + "." + meta, lookupname, new DefaultDogBedIcon(block, meta), stack);
	}
	
	public void registerMaterial(String key, String lookupname, IDogBedIcon dogBedIcon, ItemStack craftingItem) {
		if(this.isValidId(key))
			LogHelper.warning("Tried to register a dog bed material with the id %s more that once", key); 
		else {
			this.keys.add(key);
			this.lookupnames.put(key, lookupname);
			this.icons.put(key, dogBedIcon);
			this.craftingItems.put(key, craftingItem);
			
			if("casing".equals(this.key))
				for(String beddingId : DogBedRegistry.BEDDINGS.getKeys())
					GameRegistry.addRecipe(createItemStack(key, beddingId), new Object[] {"CBC", "CBC", "CCC", 'C', craftingItem, 'B', DogBedRegistry.BEDDINGS.getCraftingItem(beddingId)});
			else if("bedding".equals(this.key))
				for(String casingId : DogBedRegistry.CASINGS.getKeys())
					GameRegistry.addRecipe(createItemStack(casingId, key), new Object[] {"CBC", "CBC", "CCC", 'C', DogBedRegistry.CASINGS.getCraftingItem(casingId), 'B', craftingItem});
		
			LogHelper.info("Register dog bed %s under the key %s", this.key, key);
		}
	}
	
	public List<String> getKeys() {
		return this.keys;
	}
	
	public IIcon getIcon(String id, int side) {
		if(!this.isValidId(id))
			return null;
		return this.icons.get(id).getIcon(side);
	}
	
	public String getLookUpValue(String id) {
		if(!this.isValidId(id))
			return null;
		return this.lookupnames.get(id);
	}
	
	public ItemStack getCraftingItem(String id) {
		if(!this.isValidId(id))
			return null;
		return this.craftingItems.get(id);
	}
	
	public static ItemStack createItemStack(String casingId, String beddingId) {
		ItemStack stack = new ItemStack(ModBlocks.dogBed, 1, 0);
		stack.stackTagCompound = new NBTTagCompound();
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("casingId", casingId);
		tag.setString("beddingId", beddingId);
		stack.stackTagCompound.setTag("doggytalents", tag);
		return stack;
	}
}
