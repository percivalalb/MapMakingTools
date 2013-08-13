package mapmakingtools.core.helper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ProPercivalalb
 */
public class ImageHelper {

	public static List<ColourCache> blockToImg = new ArrayList<ColourCache>();
	
	public static int[] closestMaterial(int red, int green, int blue, int alpha) {
		int closest = 255 * 255 * 3;
		int[] best = new int[] {35, 0}; //White wool
		for(ColourCache cache : blockToImg) {
			int newRed = red - cache.red;
			int newGreen = green - cache.green;
			int newBlue = blue - cache.blue;
			int dist = newRed*newRed + newGreen*newGreen + newBlue*newBlue;
			if(dist < closest) {
				closest = dist;
				best = new int[] {cache.blockId, cache.blockMeta};
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

		LogHelper.logInfo("rgb: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
		return rgb;
	}
	
	public static void putBlockColour(int blockId, int blockMeta, int red, int green, int blue) {
		ColourCache cache = new ColourCache(blockId, blockMeta, red, green, blue);
		if(!blockToImg.contains(cache)) {
			blockToImg.add(cache);
		}
	}
	
	static {
		putBlockColour(1,   0,  125, 125, 125);
		putBlockColour(3,   0,  134,  96,  67);
		putBlockColour(5,   0,  156, 127,  78);
		putBlockColour(5,   1,  103,  77,  46);
		putBlockColour(5,   2,  195, 179, 123);
		putBlockColour(5,   3,  154, 110,  77);
		putBlockColour(22,  0,   29,  71, 165);
		putBlockColour(24,  0,  229, 221, 168);
		putBlockColour(25,  0,  100,  67,  50);
		putBlockColour(35,  0,  221, 221, 221);
		putBlockColour(35,  1,  219, 125,  62);
		putBlockColour(35,  2,  179,  80, 188);
		putBlockColour(35,  3,  107, 138, 201);
		putBlockColour(35,  4,  177, 166,  39);
		putBlockColour(35,  5,   65, 174,  56);
		putBlockColour(35,  6,  208, 132, 153);
		putBlockColour(35,  7,   64,  64,  64);
		putBlockColour(35,  8,  154, 161, 161);
		putBlockColour(35,  9,   46, 110, 137);
		putBlockColour(35, 10,  126,  61, 181);
		putBlockColour(35, 11,   46,  56, 141);
		putBlockColour(35, 12,   79,  50,  31);
		putBlockColour(35, 13,   53,  70,  27);
		putBlockColour(35, 14,  150,  52,  48);
		putBlockColour(35, 15,   25,  22,  22);
		putBlockColour(41,  0,  249, 236,  78);
		putBlockColour(42,  0,  219, 219, 219);
		putBlockColour(45,  0,  146,  99,  86);
		putBlockColour(49,  0,   20,  18,  29);
		putBlockColour(57,  0,   97, 219, 213);
		putBlockColour(80,  0,  239, 251, 251);
		putBlockColour(82,  0,  158, 164, 176);
		putBlockColour(87,  0,  111,  54,  52);
		putBlockColour(88,  0,   84,  64,  51);
		putBlockColour(98,  0,  122, 122, 122);
		putBlockColour(103, 0,  141, 145,  36);
		putBlockColour(112, 0,   44,  22,  46);
		putBlockColour(121, 0,  221, 223, 165);
		putBlockColour(133, 0,   81, 217, 117);
	}
}
