package mapmakingtools.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.enums.MovementType;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.NumberParse;

/**
 * @author ProPercivalalb
 */
public class RotationLoader {

	private static Hashtable<Block, Hashtable<String, Hashtable<Integer, Integer>>> map = new Hashtable<Block, Hashtable<String, Hashtable<Integer, Integer>>>();
	
	public static void loadFiles() {
		try {
			map.clear();
			
			URI uri = MapMakingTools.class.getResource("/assets/mapmakingtools/data/rotation/").toURI();
			File folder = new File(uri);
			for(File file : folder.listFiles()) {
				if(file.isDirectory() || !file.getName().endsWith(".txt"))
					continue;
				
				BufferedReader paramReader = new BufferedReader(new FileReader(file)); 
				String line = "";
				
				Block block = null;
				Hashtable<String, Hashtable<Integer, Integer>> modeData = new Hashtable<String, Hashtable<Integer, Integer>>();
				
				
				while((line = paramReader.readLine()) != null) {
					if(line.isEmpty() || line.startsWith("#"))
						continue;
					
					String[] split = line.split(" ~~~ ");
					if(split.length < 2)
						continue;
					
					String type = split[0];
					
					
					if(type.equals("block"))
						block = Block.getBlockFromName(split[1]);
					
					if(type.equals("data")) {
						String movement = split[1];
						
						if(!modeData.containsKey(movement))
							modeData.put(movement, new Hashtable<Integer, Integer>());
						
						Hashtable<Integer, Integer> mapping = modeData.get(movement);
						
						int start = NumberParse.getInteger(split[2]);
						int end = NumberParse.getInteger(split[3]);
						mapping.put(start, end);
					}						
				}
				
				if(block != null) {
					map.put(block, modeData);
					LogHelper.info("Loaded movement data for %s", Block.blockRegistry.getNameForObject(block));
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean onRotation(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, MovementType movementType) {
		if(!map.containsKey(block))
			return false;
		
		Hashtable<String, Hashtable<Integer, Integer>> modeData = map.get(block);
		
		if(!modeData.containsKey(movementType.getMarker()))
			return false;
		
		Hashtable<Integer, Integer> mapping = modeData.get(movementType.getMarker());
		
		if(!mapping.containsKey(meta))
			return false;
		
		int newMeta = mapping.get(meta);
		world.setBlock(x, y, z, block, newMeta, 2);
		
		return true;
	}
}
