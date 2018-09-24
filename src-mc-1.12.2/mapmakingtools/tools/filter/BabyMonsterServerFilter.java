package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.FilterServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class BabyMonsterServerFilter extends FilterServer {

	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityZombie; 
	}
}
