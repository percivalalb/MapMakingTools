package mapmakingtools.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class WrenchTasks {

	/** A list of all the tasks in the worlds Blocks and entities **/
	private static List<WrenchTasks> wrenchTaskBlock = new ArrayList<WrenchTasks>(); 
	private static List<WrenchTasks> wrenchTaskEntity = new ArrayList<WrenchTasks>(); 
	
	public static boolean isThereTaskBlock(World world, int x, int y, int z) {
		Iterator<WrenchTasks> ite = wrenchTaskBlock.iterator();
		while(ite.hasNext()) {
			WrenchTasks task = ite.next();
			if(task.isInWorld(world) && task.x == x && task.y == y && task.z == z) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isThereTaskEntity(World world, int entityId) {
		Iterator<WrenchTasks> ite = wrenchTaskEntity.iterator();
		while(ite.hasNext()) {
			WrenchTasks task = ite.next();
			if(task.isInWorld(world) && task.entityId == entityId) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isThereTaskEntity(Entity entity) {
		return isThereTaskEntity(entity.worldObj, entity.entityId);
	}
	
	public static void addTaskBlock(EntityPlayer player, World world, int x, int y, int z) {
		WrenchTasks task = new WrenchTasks(player, world, x, y, z);
		if(!wrenchTaskBlock.contains(task)) {
			wrenchTaskBlock.add(task);
		}
		else {
			LogHelper.logInfo("A wrench task was tried to be added but failed due to one like it already being there.");
		}
	}
	
	public static void addTaskEntity(EntityPlayer player, World world, int entityId) {
		WrenchTasks task = new WrenchTasks(player, world, entityId);
		if(!wrenchTaskEntity.contains(task)) {
			wrenchTaskEntity.add(task);
		}
		else {
			LogHelper.logInfo("A wrench task was tried to be added but failed due to one like it already being there.");
		}
	}
	
	public static void addTaskEntity(EntityPlayer player, Entity entity) {
		WrenchTasks task = new WrenchTasks(player, entity);
		if(!wrenchTaskEntity.contains(task)) {
			wrenchTaskEntity.add(task);
		}
		else {
			LogHelper.logInfo("A wrench task was tried to be added but failed due to one like it already being there.");
		}
	}
	
	public static void removeTaskBlock(EntityPlayer player, World world, int x, int y, int z) {
		for(int index = 0; index < wrenchTaskBlock.size(); ++index) {
			WrenchTasks task = wrenchTaskBlock.get(index);
			if(task.isInWorld(world) && task.x == x && task.y == y && task.z == z) {
				wrenchTaskBlock.remove(index);
				LogHelper.logDebug("Removed Task!");
			}
		}
	}
	
	public static void removeTaskEntity(EntityPlayer player, World world, int entityId) {
		for(int index = 0; index < wrenchTaskEntity.size(); ++index) {
			WrenchTasks task = wrenchTaskEntity.get(index);
			if(task.isInWorld(world) && task.entityId == entityId) {
				wrenchTaskEntity.remove(index);
				LogHelper.logDebug("Removed Task!");
			}
		}
	}
	
	public static void removeTaskEntity(EntityPlayer player, Entity entity) {
		for(int index = 0; index < wrenchTaskEntity.size(); ++index) {
			WrenchTasks task = wrenchTaskEntity.get(index);
			if(task.isInWorld(entity.worldObj) && task.entityId == entity.entityId) {
				wrenchTaskEntity.remove(index);
				LogHelper.logDebug("Removed Task!");
			}
		}
	}
	
	public static EntityPlayer getPlayerTaskBlock(World world, int x, int y, int z) {
		Iterator<WrenchTasks> ite = wrenchTaskBlock.iterator();
		while(ite.hasNext()) {
			WrenchTasks task = ite.next();
			if(task.isInWorld(world) && task.x == x && task.y == y && task.z == z) {
				return task.player;
			}
		}
		return null;
	}
	
	public static EntityPlayer getPlayerTaskEntity(World world, int entityId) {
		Iterator<WrenchTasks> ite = wrenchTaskEntity.iterator();
		while(ite.hasNext()) {
			WrenchTasks task = ite.next();
			if(task.isInWorld(world) && task.entityId == entityId) {
				return task.player;
			}
		}
		return null;
	}
	
	public static EntityPlayer getPlayerTaskEntity(Entity entity) {
		return getPlayerTaskEntity(entity.worldObj, entity.entityId);
	}
	
	/** Class that stores data about what is being used in the world**/
	public int x, y, z;
	public int entityId;
	
	public World worldObj;
	public EntityPlayer player;
	public Mode mode;
	public enum Mode { ENTITY, BLOCK; }
	
	public WrenchTasks(EntityPlayer player, World worldObj, int x, int y, int z) {
		this.player = player;
		this.worldObj = worldObj;
		this.x = x;
		this.y = y;
	    this.z = z;
	    this.mode = Mode.BLOCK;
	}
	
	public WrenchTasks(EntityPlayer player, Entity entity) {
		this(player, entity.worldObj, entity.entityId);
	}
	
	public WrenchTasks(EntityPlayer player, World worldObj, int entityId) {
		this.player = player;
		this.worldObj = worldObj;
		this.entityId = entityId;
		this.mode = Mode.ENTITY;
	}
	
	/**
	 * Using dimension ids, it checks weather the stored id is the same as the given id
	 * @param world The world you want to check the task is in
	 * @return True if the worlds are equal
	 */
	public boolean isInWorld(World world) {
		int checkingDim = world.provider.dimensionId;
		int currentDim = world.provider.dimensionId;
		return checkingDim == currentDim;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WrenchTasks) {
			WrenchTasks target = (WrenchTasks)obj;
			switch(mode) {
			case BLOCK:
				return target.isInWorld(worldObj) && target.x == x && target.y == y && target.z == z;
			case ENTITY:
				return target.isInWorld(worldObj) && target.entityId == entityId;
			}
		}
		return false;
	}
}
