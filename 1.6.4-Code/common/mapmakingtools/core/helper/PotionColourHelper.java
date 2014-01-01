package mapmakingtools.core.helper;

/**
 * @author ProPercivalalb
 */
public enum PotionColourHelper {

	GREEN("green", 8196, 16388),
	ORANGE("orange", 8227, 16419),
	RED("red", 8261, 16453),
	LIGHT_BLUE("lightBlue", 8194, 16386),
	DARK_BLUE("darkBlue", 2830, 16422),
	LIGHT_PURPLE("lightPurple", 8201, 16393),
	DARK_PURPLE("darkPurple", 8268, 16460),
	PINK("pink", 8225, 16385),
	CYAN("cyan", 8238, 16430),
	UNKNOWN("unknown", 8194, 16386);
	
	private int bottleId;
	private int splashId;
	private String name;
	
	PotionColourHelper(String colourName, int bottle, int splash) {
		this.bottleId = bottle;
		this.splashId = splash;
		this.name = colourName;
	}
	
	public int bottle() {
		return this.bottleId;
	}
	
	public int splash() {
		return this.splashId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static String[] getPotionColours() {
		String[] colours = new String[PotionColourHelper.values().length];
		int count = 0;
		for(PotionColourHelper colour : PotionColourHelper.values()) {
			colours[count] = colour.getName();
			++count;
		}
		return colours;
	}
	
	public static int getSplashIdFromString(String name) {
		for(PotionColourHelper colour : PotionColourHelper.values()) {
			if(name.equalsIgnoreCase(colour.getName())) {
				return colour.splash();
			}
		}
		return LIGHT_BLUE.splash();
	}
	
	public static int getBottleIdFromString(String name) {
		for(PotionColourHelper colour : PotionColourHelper.values()) {
			if(name.equalsIgnoreCase(colour.getName())) {
				return colour.bottle();
			}
		}
		return LIGHT_BLUE.bottle();
	}
}
