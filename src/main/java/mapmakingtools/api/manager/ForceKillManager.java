package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mapmakingtools.api.interfaces.IForceKill;
import mapmakingtools.helper.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author ProPercivalalb
 */
public class ForceKillManager {
	
	private static final Map<String, IForceKill> map = new Hashtable<String, IForceKill>();
	private static final List<String> nameList = new ArrayList<String>();
	
	public static void killGiven(String name, Entity entity, EntityPlayerMP player) {
		if(!isRealName(name) || (entity instanceof EntityPlayer))
			return;
		IForceKill kill = map.get(name);
		if(kill.onCommand(entity)) {
			entity.setDead();
			entity.worldObj.removeEntity(entity);
			//PacketTypeHandler.populatePacketAndSendToClient(new PacketRemoveEntityFromWorld(entity.entityId), player);
		}
	}
	
	/**
	 * Checks if there is a corresponding {@link IForcekill.class} 
	 * @param name The name of the instance
	 * @return Whether it is a real name
	 */
	public static boolean isRealName(String name) {
		return map.keySet().contains(name);
	}
	
	/**
	 * Checks to make sure no other has the same name and adds to list.
	 * @param forceKill The instance you want to register
	 */
	public static void registerHandler(String name, IForceKill forceKill) {
		if(nameList.contains(name)) {
			LogHelper.warning("You can't register and Force Kill class with the same name as another!");
			return;
		}
		map.put(name, forceKill);
		nameList.add(name);
	}
	
	/**
	 * Used for tab completion in the command menu
	 * @return A String array that contains all the names the #IForceKill.class
	 */
	public static String[] getNameList() {
		Collections.sort(nameList);
		return nameList.toArray(new String[] {});
	}

}
