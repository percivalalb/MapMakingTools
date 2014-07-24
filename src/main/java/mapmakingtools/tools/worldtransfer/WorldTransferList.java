package mapmakingtools.tools.worldtransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;

/**
 * @author ProPercivalalb
 */
public class WorldTransferList {

	public static final File saveFile = new File("mapmakingtools_worldtransfer.dat");
	private static final Hashtable<String, ArrayList<CachedBlock>> map = new Hashtable<String, ArrayList<CachedBlock>>();
	
	public static void add(String name, boolean override, ArrayList<CachedBlock> list) {
		if(override || !map.containsKey(name))
			map.put(name, list);
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
	
	public static ArrayList<CachedBlock> getAreaFromName(String name) {
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
				
				NBTTagList areaList = new NBTTagList();
				
				ArrayList<CachedBlock> blockList = map.get(name);
				for(CachedBlock cachedBlock : blockList)
					areaList.appendTag(cachedBlock.writeToNBT(new NBTTagCompound(), false));
				
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
			
			if (!saveFile.exists()) {
				FileOutputStream outputStream = new FileOutputStream(saveFile);
			    CompressedStreamTools.writeCompressed(new NBTTagCompound(), outputStream);
			    outputStream.close();
			}
			FileInputStream inputStream = new FileInputStream(saveFile);
			NBTTagCompound data = CompressedStreamTools.readCompressed(inputStream);
			
			NBTTagList list = (NBTTagList)data.getTagList("selection", 10);
			for(int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				NBTTagList areaList = (NBTTagList)tag.getTagList("area", 10);
				
				ArrayList<CachedBlock> blockList = new ArrayList<CachedBlock>();
				for(int j = 0; j < areaList.tagCount(); ++j)
					blockList.add(new CachedBlock(areaList.getCompoundTagAt(j), false));
				
				map.put(tag.getString("name"), blockList);
			}
			
			for(String name : map.keySet()) {
				LogHelper.info("  Name: \"%s\", Blocks: %d", name, map.get(name).size());
			}
			
			inputStream.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
