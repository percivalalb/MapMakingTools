package mapmakingtools.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public interface IItemAttribute {

	public boolean isApplicable(EntityPlayer player, ItemStack stack);
}
