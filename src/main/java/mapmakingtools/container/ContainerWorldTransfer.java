package mapmakingtools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * @author ProPercivalalb
 */
public class ContainerWorldTransfer extends Container {

	public ContainerWorldTransfer() {
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
