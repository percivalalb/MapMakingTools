package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.FilterServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class VillagerProfessionServerFilter extends FilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
}
