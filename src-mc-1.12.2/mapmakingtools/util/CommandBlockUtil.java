package mapmakingtools.util;

import mapmakingtools.api.filter.IFilterBase;
import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.api.manager.FakeWorldManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;

public class CommandBlockUtil {

	public static void setName(CommandBlockBaseLogic logic, String name) {
		logic.setName(name);
	}
	
	public static String getName(CommandBlockBaseLogic logic) {
		return logic.getName();
	}
	
	public static void setCommand(CommandBlockBaseLogic logic, String command) {
		logic.setCommand(command);
	}
	
	public static String getCommand(CommandBlockBaseLogic logic) {
		return logic.getCommand();
	}
	
	public static CommandBlockBaseLogic getCommandLogic(IFilterBase gui) {
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			if(tile instanceof TileEntityCommandBlock) {
				TileEntityCommandBlock comamndblock = (TileEntityCommandBlock)tile;
				return comamndblock.getCommandBlockLogic();
			}
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = FakeWorldManager.getEntity(gui.getWorld(), gui.getEntityId());
			if(entity instanceof EntityMinecartCommandBlock) {
				EntityMinecartCommandBlock comamndblock = (EntityMinecartCommandBlock)entity;
				return comamndblock.getCommandBlockLogic();
			}
		}
		
		return null;
	}
	
	/** Is it a block or minecart command block */
	public static boolean isCommand(IFilterBase gui) {
		if(gui.getTargetType() == TargetType.BLOCK) {
			TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
			return tile instanceof TileEntityCommandBlock;
		}
		else if(gui.getTargetType() == TargetType.ENTITY) {
			Entity entity = FakeWorldManager.getEntity(gui.getWorld(), gui.getEntityId());
			return entity instanceof EntityMinecartCommandBlock;
		}
		
		return false;
	}
}
