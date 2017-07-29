package mapmakingtools.tools.datareader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.Numbers;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

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
	
	public static void addStack(String block, int blockMeta) {
		ItemStack res = new ItemStack(Block.getBlockFromName(block), 1, blockMeta);
		stacksDisplay.add(res);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/blocks.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 2 && Numbers.isInteger(arr[1]));
		parts.forEach(arr -> addStack(arr[0], Numbers.parse(arr[1])));
	}
}
