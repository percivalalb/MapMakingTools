package mapmakingtools.core.handler;

import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class FlyHandler implements ITickHandler {
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.PLAYER)) {
			EntityPlayer player = (EntityPlayer)tickData[0];
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "MMT|Fly Handler";
	}

	public static void setFlySpeed(EntityPlayer p, int speed) {
		p.capabilities.setFlySpeed(0.05F * (float)speed);
		if(p.worldObj.isRemote) {
			net.minecraft.client.entity.EntityPlayerSP player = (net.minecraft.client.entity.EntityPlayerSP)p;
			player.sendPlayerAbilities();
		}
	}
}
