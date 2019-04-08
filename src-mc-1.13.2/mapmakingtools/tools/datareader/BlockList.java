package mapmakingtools.tools.datareader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.helper.Numbers;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

/**
 * @author ProPercivalalb
 */
public class BlockList {
	
	public static List<ItemStack> stacksDisplay = new ArrayList<ItemStack>();
	
	public static Iterator<ItemStack> getIterator() {
		return stacksDisplay.iterator();
	}
	
	public static List<ItemStack> getList() {
		return stacksDisplay;
	}
	
	public static int getListSize() {
		return stacksDisplay.size();
	}
	
	public static void addStack(String block) {
		ItemStack res = new ItemStack(IRegistry.BLOCK.get(new ResourceLocation(block)), 1);
		stacksDisplay.add(res);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/blocks.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 2 && Numbers.isInteger(arr[1]));
		//TODO parts.forEach(arr -> addStack(arr[0]));
	}
}
