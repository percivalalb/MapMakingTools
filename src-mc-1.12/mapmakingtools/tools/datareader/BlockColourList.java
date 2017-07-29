package mapmakingtools.tools.datareader;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.NumberParse;

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
				best = new Object[] {cache.block, cache.blockMeta};
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
		try {
			BufferedReader paramReader = new BufferedReader(new InputStreamReader(MapMakingTools.class.getResourceAsStream("/assets/mapmakingtools/data/colourblockmap.txt"))); 
			String line = "";
			while((line = paramReader.readLine()) != null) {
				
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] dataParts = line.split(" ~~~ ");
				if(dataParts.length != 5)
					continue;
					
				if(!NumberParse.areIntegers(dataParts[1], dataParts[2], dataParts[3], dataParts[4]))
					continue;
					
				String block = dataParts[0];
				int meta = NumberParse.getInteger(dataParts[1]);
				int red = NumberParse.getInteger(dataParts[2]);
				int green = NumberParse.getInteger(dataParts[3]);
				int blue = NumberParse.getInteger(dataParts[4]);
				
				putBlockColour(block, meta, red, green, blue);
			}
	    }
		catch(Exception e) {
			e.printStackTrace();
	    }
	}
	
	public static class ColourCache {
		
		public String block;
		public int blockMeta, red, green, blue;
		
		public ColourCache(String block, int blockMeta, int red, int green, int blue) {
			this.block = block;
			this.blockMeta = blockMeta;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}

}
