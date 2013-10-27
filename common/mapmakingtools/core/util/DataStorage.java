package mapmakingtools.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mapmakingtools.core.helper.ArrayListHelper;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.PlayerHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketBuildLeftClick;
import mapmakingtools.network.packet.PacketBuildRightClick;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class DataStorage {
	
	/** If the minY or maxY are {@link #INDEX_UNSELECTED} then the player has not completed there selected positions **/
	public static int INDEX_UNSELECTED = -1;
	private static Map<String, ArrayList<Integer>> selected_Pos = new Hashtable<String, ArrayList<Integer>>();
	//Saves the undos and redos of each player in this map
	private static Map<String, ArrayList<ArrayList<CachedBlockPlacement>>> cachedUndo = createCache();
	private static Map<String, ArrayList<ArrayList<CachedBlockPlacement>>> cachedRedo = createCache();
	private static Map<String, ArrayList<CachedBlockPlacement>> cachedCopy = new Hashtable<String, ArrayList<CachedBlockPlacement>>();
	private static Map<String, Integer> copyRotation = new Hashtable<String, Integer>();
	private static Map<String, Integer> copyFlip = new Hashtable<String, Integer>();
	
	public static void addCopy(ArrayList<CachedBlockPlacement> list, EntityPlayer player) {
		cachedCopy.put(PlayerHelper.usernameLowerCase(player), list);
		copyRotation.put(PlayerHelper.usernameLowerCase(player), INDEX_UNSELECTED);
	}
	
	public static boolean hasCopy(EntityPlayer player) {
		ArrayList<CachedBlockPlacement> list = cachedCopy.get(PlayerHelper.usernameLowerCase(player));
		return !ArrayListHelper.isEmpty(list);
	}
	
	public static boolean flip(EntityPlayer player, ArrayList<CachedBlockPlacement> list, int flipMode, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		Iterator<CachedBlockPlacement> ite = list.iterator();
		ArrayList<CachedBlockPlacement> newList = new ArrayList<CachedBlockPlacement>();
		int blocks = 0;
		while(ite.hasNext()) {
			CachedBlockPlacement undo = ite.next();
			newList.add(undo.setCachedBlockReletiveToFlipped(player, flipMode, minX, minY, minZ, maxX, maxY, maxZ));
			++blocks;
		}
		player.addChatMessage(blocks + " block(s) have been flipped.");
		DataStorage.addUndo(newList, player);
		return true;
	}
	
	public static boolean paste(EntityPlayer player) {
		ArrayList<CachedBlockPlacement> list = cachedCopy.get(PlayerHelper.usernameLowerCase(player));
		if(ArrayListHelper.isEmpty(list)) {
			player.addChatMessage(EnumChatFormatting.RED + "There is nothing to paste.");
			return false;
		}
		Iterator<CachedBlockPlacement> ite = list.iterator();
		ArrayList<CachedBlockPlacement> newList = new ArrayList<CachedBlockPlacement>();
		int blocks = 0;
		while(ite.hasNext()) {
			CachedBlockPlacement undo = ite.next();
			newList.add(undo.setCachedBlockReletiveToRotated(player));
			++blocks;
		}
		player.addChatMessage(blocks + " block(s) have been pasted.");
		DataStorage.addUndo(newList, player);
		return true;
	}
	
	public static boolean hasRotation(EntityPlayer player) {
		if(!copyRotation.containsKey(PlayerHelper.usernameLowerCase(player))) {
			return false;
		}
		return INDEX_UNSELECTED != copyRotation.get(PlayerHelper.usernameLowerCase(player));
	}
	
	public static void setRotation(EntityPlayer player, int rotation) {
		if(rotation % 90 != 0) {
			throw new CommandException("commands.build.rotationNot90", new Object[0]);
		}
		copyRotation.put(PlayerHelper.usernameLowerCase(player), rotation);
	}
	
	public static int getRotation(EntityPlayer player) {
		if(!hasRotation(player)) {
			return 0;
		}
		return copyRotation.get(PlayerHelper.usernameLowerCase(player));
	}
	
	public static void addUndo(ArrayList<CachedBlockPlacement> list, EntityPlayer player) {
		ArrayList<ArrayList<CachedBlockPlacement>> undos = getCache(cachedUndo, player);
		undos.add(list);
	}
	
	public static boolean undo(EntityPlayer player) {
		ArrayList<ArrayList<CachedBlockPlacement>> undos = getCache(cachedUndo, player);
		if(ArrayListHelper.isEmpty(undos)) {
			throw new CommandException("commands.build.nothingToUndo", new Object[0]);
		}
		ArrayList<CachedBlockPlacement> list = undos.get(undos.size() - 1);
		Iterator<CachedBlockPlacement> ite = list.iterator();
		ArrayList<CachedBlockPlacement> newList = new ArrayList<CachedBlockPlacement>();
		int blocks = 0;
		while(ite.hasNext()) {
			CachedBlockPlacement undo = ite.next();
			newList.add(undo.setCachedBlock());
			++blocks;
		}
		player.addChatMessage(blocks + " block(s) have been undone.");
		DataStorage.addRedo(newList, player);
		ArrayListHelper.removeLast(undos);
		return true;
	}
	
	public static void addRedo(ArrayList<CachedBlockPlacement> list, EntityPlayer player) {
		ArrayList<ArrayList<CachedBlockPlacement>> undos = getCache(cachedRedo, player);
		undos.add(list);
	}
	
	public static boolean redo(EntityPlayer player) {
		ArrayList<ArrayList<CachedBlockPlacement>> redos = getCache(cachedRedo, player);
		if(ArrayListHelper.isEmpty(redos)) {
			throw new CommandException("commands.build.nothingToRedo", new Object[0]);
		}
		ArrayList<CachedBlockPlacement> list = redos.get(redos.size() - 1);
		Iterator<CachedBlockPlacement> ite = list.iterator();
		ArrayList<CachedBlockPlacement> newList = new ArrayList<CachedBlockPlacement>();
		int blocks = 0;
		while(ite.hasNext()) {
			CachedBlockPlacement undo = ite.next();
			newList.add(undo.setCachedBlock());
			++blocks;
		}
		player.addChatMessage(blocks + " block(s) have been redone.");
		DataStorage.addUndo(newList, player);
		ArrayListHelper.removeLast(redos);
		return true;
	}
	
	public static Map<String, ArrayList<Integer>> getPlayerPos() {
		return selected_Pos;
	}
	
	public static boolean hasSelectedPostions(EntityPlayer player) {
		if(selected_Pos.containsKey(PlayerHelper.usernameLowerCase(player))) {
			ArrayList<Integer> list = selected_Pos.get(PlayerHelper.usernameLowerCase(player));
			if(list.get(1) != INDEX_UNSELECTED && list.get(3) != INDEX_UNSELECTED) return true;
		}
		return false;
	}
	
	public static int[] getSelectedPosFromPlayer(EntityPlayer player) {
		if(!selected_Pos.containsKey(PlayerHelper.usernameLowerCase(player))) {
			ArrayList list = new ArrayList();
			for(int var1 = 0; var1 < 6; ++var1) list.add(INDEX_UNSELECTED);
			selected_Pos.put(PlayerHelper.usernameLowerCase(player), list);
		}
		ArrayList<Integer> list = selected_Pos.get(PlayerHelper.usernameLowerCase(player));
		int minX = list.get(0);
		int minY = list.get(1);
		int minZ = list.get(2);
		int maxX = list.get(3);
		int maxY = list.get(4);
		int maxZ = list.get(5);
		return new int[] {minX, minY, minZ, maxX, maxY, maxZ};
	}
	
	public static void setPlayerRightClick(EntityPlayer player, int x, int y, int z) {
		if(!selected_Pos.containsKey(PlayerHelper.usernameLowerCase(player))) {
			ArrayList list = new ArrayList();
			for(int var1 = 0; var1 < 6; ++var1) list.add(INDEX_UNSELECTED);
			selected_Pos.put(PlayerHelper.usernameLowerCase(player), list);
		}
		ArrayList<Integer> list = selected_Pos.get(PlayerHelper.usernameLowerCase(player));
		list.set(3, x);
		list.set(4, y);
		list.set(5, z);
		if(!player.worldObj.isRemote) {
			PacketTypeHandler.populatePacketAndSendToClient(new PacketBuildRightClick(x, y, z), (EntityPlayerMP)player);
		}
	}
	
	public static void setPlayerLeftClick(EntityPlayer player, int x, int y, int z) {
		if(!selected_Pos.containsKey(PlayerHelper.usernameLowerCase(player))) {
			ArrayList list = new ArrayList();
			for(int var1 = 0; var1 < 6; ++var1) list.add(INDEX_UNSELECTED);
			selected_Pos.put(PlayerHelper.usernameLowerCase(player), list);
		}
		ArrayList<Integer> list = selected_Pos.get(PlayerHelper.usernameLowerCase(player));
		list.set(0, x);
		list.set(1, y);
		list.set(2, z);
		LogHelper.logDebug("Set values left click");
		if(!player.worldObj.isRemote) {
			PacketTypeHandler.populatePacketAndSendToClient(new PacketBuildLeftClick(x, y, z), (EntityPlayerMP)player);
			LogHelper.logDebug("Send Packet left click");
		}
	}
	
	//Minor Functions
	private static Hashtable<String, ArrayList<ArrayList<CachedBlockPlacement>>> createCache() {
		return new Hashtable<String, ArrayList<ArrayList<CachedBlockPlacement>>>();
	}
	public static ArrayList<ArrayList<CachedBlockPlacement>> getCache(Map<String, ArrayList<ArrayList<CachedBlockPlacement>>> map, EntityPlayer player) {
		ArrayList<ArrayList<CachedBlockPlacement>> list = map.get(PlayerHelper.usernameLowerCase(player));
		if(list == null) {map.put(PlayerHelper.usernameLowerCase(player), new ArrayList<ArrayList<CachedBlockPlacement>>());}
		return list = map.get(PlayerHelper.usernameLowerCase(player));
	}
	
	public static void clearAll() {
		cachedUndo.clear();
		cachedRedo.clear();
		cachedCopy.clear();
		selected_Pos.clear();
	}
}
