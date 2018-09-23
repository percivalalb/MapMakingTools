package mapmakingtools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.base.Strings;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.interfaces.FilterServer;
import mapmakingtools.api.manager.FilterManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class WorldData {

	private static final HashMap<UUID, PlayerData> PLAYER_POINTS = new HashMap<UUID, PlayerData>();
	
	public static PlayerData getPlayerData(EntityPlayer player) {
		return getPlayerData(player.getUniqueID());
	}
	
	public static PlayerData getPlayerData(UUID uuid) {
		if(uuid == null)
			return null;
		
		if(!PLAYER_POINTS.containsKey(uuid)) {
			MapMakingTools.LOGGER.info("Create player profile for {}", uuid);
			PLAYER_POINTS.put(uuid, new PlayerData(uuid));
		}
		
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
			for(UUID uuid : PLAYER_POINTS.keySet())
				list.appendTag(PLAYER_POINTS.get(uuid).writeToNBT(new NBTTagCompound()));

			
			data.setTag("playerPoints", list);
			
			for(FilterServer filter : FilterManager.getServerMap()) {
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
				PLAYER_POINTS.put(tag.getUniqueId("uuid"), new PlayerData(tag));
			}
			
			for(FilterServer filter : FilterManager.getServerMap()) {
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
