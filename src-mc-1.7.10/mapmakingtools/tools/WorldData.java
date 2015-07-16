package mapmakingtools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;

import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.manager.FilterManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.base.Strings;

/**
 * @author ProPercivalalb
 */
public class WorldData {

	private static final Hashtable<String, PlayerData> PLAYER_POINTS = new Hashtable<String, PlayerData>();
	
	public static PlayerData getPlayerData(EntityPlayer player) {
		if(player == null)
			return null;
		
		String username = player.getCommandSenderName();
		
		if(!PLAYER_POINTS.keySet().contains(username))
			PLAYER_POINTS.put(username, new PlayerData(player.getCommandSenderName()));
		
		return PLAYER_POINTS.get(username);
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
			for(String username : PLAYER_POINTS.keySet()) {
				NBTTagCompound tag = PLAYER_POINTS.get(username).writeToNBT(new NBTTagCompound());
				list.appendTag(tag);
			}
			
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
			
			if (!file.exists()) {
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
				PLAYER_POINTS.put(tag.getString("username"), new PlayerData().readFromNBT(tag));
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
