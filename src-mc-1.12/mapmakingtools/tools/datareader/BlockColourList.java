package mapmakingtools.tools.datareader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import mapmakingtools.helper.Numbers;

/**
 * @author ProPercivalalb
 */
public class BlockColourList {

	public static List<ColourCache> blockToImg = new ArrayList<ColourCache>();
	
	public static Object[] closestMaterial(int red, int green, int blue) {
		int closest = 255 * 255 * 3;
		Object[] best = new Object[] {"minecraft:wool", 0}; //White wool
		for(ColourCache cache : blockToImg) {
			int newRed = red - cache.red;
			int newGreen = green - cache.green;
			int newBlue = blue - cache.blue;
			int dist = newRed * newRed + newGreen * newGreen + newBlue * newBlue;
			if(dist < closest) {
				closest = dist;
				best = new Object[] {cache.block, cache.meta};
			}
		}
		return best;
	}
	
	public static int[] getPixelData(BufferedImage img, int x, int y) {
		int argb = img.getRGB(x, y);

		int rgb[] = new int[] {
		    (argb >> 16) & 0xff, //red
		    (argb >>  8) & 0xff, //green
		    (argb      ) & 0xff  //blue
		};

		return rgb;
	}
	
	public static void putBlockColour(String block, int blockMeta, int red, int green, int blue) {
		ColourCache cache = new ColourCache(block, blockMeta, red, green, blue);

		if(!blockToImg.contains(cache))
			blockToImg.add(cache);
	}
	
	public static void readDataFromFile() {
		Stream<String> lines = DataReader.loadResource("/assets/mapmakingtools/data/colourblockmap.txt");
		Stream<String[]> parts = lines.map(line -> line.split(" ~~~ ")).filter(arr -> arr.length == 5).filter(arr -> Numbers.areIntegers(arr[1], arr[2], arr[3], arr[4]));
		parts.forEach(arr -> putBlockColour(arr[0], Numbers.parse(arr[1]), Numbers.parse(arr[2]), Numbers.parse(arr[3]), Numbers.parse(arr[4])));
	}
	
	public static class ColourCache {
		
		public String block;
		public int meta, red, green, blue;
		
		public ColourCache(String block, int blockMeta, int red, int green, int blue) {
			this.block = block;
			this.meta = blockMeta;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}

}
