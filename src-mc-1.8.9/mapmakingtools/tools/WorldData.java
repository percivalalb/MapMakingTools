package mapmakingtools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class WorldData {

	private static final HashMap<String, PlayerData> PLAYER_POINTS = new HashMap<String, PlayerData>();
	
	public static PlayerData getPlayerData(EntityPlayer player) {
		if(player == null)
			return null;
		
		String uuid = player.getUniqueID().toString();
		
		if(!PLAYER_POINTS.containsKey(uuid))
			PLAYER_POINTS.put(uuid, new PlayerData(player.getUniqueID()));
		
		return PLAYER_POINTS.get(uuid);
	}

	public static void save(File worldDirectory) {
		try {
			File file = new File(worldDirectory, "mapmakingtools.dat");
			
			if (!file.exists()) {
				FileOutputStream outputStream = new FileOutputStream(file);
				CompressedStreamTools.writeCompressed(new NBTTagCompound(), outputStream);
			    outputStream.close();
			}
	
			FileOutputStream outputStream = new FileOutputStream(file);
			NBTTagCompound data = new NBTTagCompound();
	
			//Write Data
			NBTTagList list = new NBTTagList();
			for(String uuid : PLAYER_POINTS.keySet())
				list.appendTag(PLAYER_POINTS.get(uuid).writeToNBT(new NBTTagCompound()));

			
			data.setTag("playerPoints", list);
			
			for(IFilterServer filter : FilterManager.getServerMap()) {
				if(Strings.isNullOrEmpty(filter.getSaveId()))
					continue;
				String key = "filter:" + filter.getSaveId();
				data.setTag(key, filter.writeToNBT(new NBTTagCompound()));
			}
			
	        CompressedStreamTools.writeCompressed(data, outputStream);
			outputStream.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	public static void read(File worldDirectory) {
		try {
			File file = new File(worldDirectory, "mapmakingtools.dat");
			
			if(!file.exists()) {
				FileOutputStream outputStream = new FileOutputStream(file);
			    CompressedStreamTools.writeCompressed(new NBTTagCompound(), outputStream);
			    outputStream.close();
			}
			FileInputStream inputStream = new FileInputStream(file);
			NBTTagCompound data = CompressedStreamTools.readCompressed(inputStream);
			    
			//Read Data
			NBTTagList list = (NBTTagList)data.getTagList("playerPoints", 10);
			for(int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				PLAYER_POINTS.put(tag.getString("uuid"), new PlayerData(tag));
			}
			
			for(IFilterServer filter : FilterManager.getServerMap()) {
				if(Strings.isNullOrEmpty(filter.getSaveId()))
					continue;
				String key = "filter:" + filter.getSaveId();
				if(data.hasKey(key))
					filter.readFromNBT(data.getCompoundTag(key));
			}
			
			inputStream.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
