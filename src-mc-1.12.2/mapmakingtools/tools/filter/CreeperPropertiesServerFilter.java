package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.FilterServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class CreeperPropertiesServerFilter extends FilterServer {

	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityCreeper; 
	}
	
}
