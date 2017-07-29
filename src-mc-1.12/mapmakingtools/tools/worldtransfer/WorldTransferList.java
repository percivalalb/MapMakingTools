package mapmakingtools.tools.worldtransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import mapmakingtools.MapMakingTools;
import mapmakingtools.tools.BlockCache;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;

/**
 * @author ProPercivalalb
 */
public class WorldTransferList {

	public static final File saveFile = new File("mmt_savedata.dat");
	private static final Hashtable<String, ArrayList<BlockCache>> map = new Hashtable<String, ArrayList<BlockCache>>();
	public static final Hashtable<String, List<BlockPos>> mapPos = new Hashtable<String, List<BlockPos>>();
	
	
	public static void put(String name, boolean firstSection, boolean lastSection, ArrayList<BlockCache> list) {
		if(firstSection) {
			map.put(name, list);
		}
		else {
			map.get(name).addAll(list);
		}
	}
	
	public static Set<String> getNameList() {
		return map.keySet();
	}
	
	public static boolean hasName(String name) {
		return map.containsKey(name);
	}
	
	public static int getSize() {
		return map.size();
	}
	
	public static String getName(int i) {
		return map.keySet().toArray(new String[] {})[i];
	}
	
	public static void delete(int i) {
		map.remove(map.keySet().toArray(new String[] {})[i]);
	}
	
	public static ArrayList<BlockCache> getAreaFromName(String name) {
		return map.get(name);
	}
	
	public static void saveToFile() {
		try {
			
			if (!saveFile.exists()) {
				FileOutputStream outputStream = new FileOutputStream(saveFile);
				CompressedStreamTools.writeCompressed(new NBTTagCompound(), outputStream);
			    outputStream.close();
			}
	
			FileOutputStream outputStream = new FileOutputStream(saveFile);
			NBTTagCompound data = new NBTTagCompound();
	
			//Write Data
			NBTTagList list = new NBTTagList();
			for(String name : map.keySet()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("name", name);
				
				
				NBTTagList blockPosList = new NBTTagList();
				List<BlockPos> posList = mapPos.get(name);
				for(int j = 0; j < posList.size(); ++j)
					blockPosList.appendTag(new NBTTagLong(posList.get(j).toLong()));
				tag.setTag("blockpos", blockPosList);
				
				
				NBTTagList areaList = new NBTTagList();
				ArrayList<BlockCache> blockList = map.get(name);
				for(int j = 0; j < blockList.size(); ++j) {
					NBTTagCompound compound = new NBTTagCompound();
					blockList.get(j).writeToNBT(compound);
					areaList.appendTag(compound);
				}
				tag.setTag("area", areaList);
				
				list.appendTag(tag);
			}
			
			data.setTag("selection", list);
			
	        CompressedStreamTools.writeCompressed(data, outputStream);
			outputStream.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void readFromFile() {
		try {
			MapMakingTools.LOGGER.info("" + saveFile.getAbsolutePath());
			if(!saveFile.exists()) {
				FileOutputStream outputStream = new FileOutputStream(saveFile);
			    CompressedStreamTools.writeCompressed(new NBTTagCompound(), outputStream);
			    outputStream.close();
			}
			FileInputStream inputStream = new FileInputStream(saveFile);
			NBTTagCompound data = CompressedStreamTools.readCompressed(inputStream);
			
			NBTTagList list = (NBTTagList)data.getTagList("selection", 10);
			for(int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				NBTTagList blockPosList = (NBTTagList)tag.getTagList("blockpos", 4);
				
				ArrayList<BlockPos> posList = new ArrayList<BlockPos>();
				for(int j = 0; j < blockPosList.tagCount(); ++j)
					posList.add(BlockPos.fromLong(((NBTTagLong)blockPosList.get(j)).getLong()));
				
				NBTTagList areaList = (NBTTagList)tag.getTagList("area", 10);
				
				ArrayList<BlockCache> blockList = new ArrayList<BlockCache>();
				for(int j = 0; j < areaList.tagCount(); ++j)
					blockList.add(BlockCache.readFromNBT(areaList.getCompoundTagAt(j)));

				if(posList.size() >= 2) {
					String name = tag.getString("name");
					map.put(name, blockList);
					mapPos.put(name, posList);
				}
			}
			
			for(String name : map.keySet()) {
				MapMakingTools.LOGGER.info("  Name: \"%s\", Blocks: %d", name, map.get(name).size());
			}
			
			inputStream.close();
		}
		catch(Exception e) {
			saveFile.delete();
			e.printStackTrace();
		}
	}
}
