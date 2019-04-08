package mapmakingtools.tools;

/**
 * @author ProPercivalalb
 */
public class RotationLoader {

	/**
	private static Hashtable<UniqueIdentifier, Hashtable<String, Hashtable<Integer, Integer>>> map = new Hashtable<UniqueIdentifier, Hashtable<String, Hashtable<Integer, Integer>>>();
	
	public static void loadFromTextFile(String filePath) {
		try {
			InputStream is = MapMakingTools.class.getResourceAsStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			UniqueIdentifier blockIdentifier = null;
			Hashtable<String, Hashtable<Integer, Integer>> modeData = new Hashtable<String, Hashtable<Integer, Integer>>();
			
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	if(line.isEmpty() || line.startsWith("#"))
					continue;
				
				String[] split = line.split(" ~~~ ");
				if(split.length < 2)
					continue;
				
				String type = split[0];
				
				
				if(type.equals("block"))
					blockIdentifier = new UniqueIdentifier(new ResourceLocation(split[1]).toString());
				
				if(type.equals("data")) {
					String movement = split[1];
					
					if(!modeData.containsKey(movement))
						modeData.put(movement, new Hashtable<Integer, Integer>());
					
					Hashtable<Integer, Integer> mapping = modeData.get(movement);
					
					int start = NumberParse.getInt(split[2]);
					int end = NumberParse.getInt(split[3]);
					mapping.put(start, end);
				}
		    }
		    
		    if(blockIdentifier != null) {
				map.put(blockIdentifier, modeData);
				MapMakingTools.LOGGER.info("Loaded movement data for %s", blockIdentifier);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void clear() {
		map.clear();
	}
	
	public static boolean onRotation(World world, BlockPos pos, UniqueIdentifier blockIdentifier, Block block, int meta, MovementType movementType) {
		if(!map.containsKey(blockIdentifier))
			return false;
		
		Hashtable<String, Hashtable<Integer, Integer>> modeData = map.get(blockIdentifier);
		
		if(!modeData.containsKey(movementType.getMarker()))
			return false;
		
		Hashtable<Integer, Integer> mapping = modeData.get(movementType.getMarker());
		
		if(!mapping.containsKey(meta))
			return false;
		
		int newMeta = mapping.get(meta);
		world.setBlockState(pos, block.getStateFromMeta(newMeta), 2);
		
		return true;
	}**/
}
