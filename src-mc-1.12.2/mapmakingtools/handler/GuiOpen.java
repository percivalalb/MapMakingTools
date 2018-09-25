package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.helper.ClientHelper;
import net.minecraft.client.gui.inventory.GuiEditCommandBlockMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOpen {

	@SubscribeEvent
	public void onPreRenderGameOverlay(GuiOpenEvent event) {
		if(ClientHelper.getClient().player == null) return;
		ItemStack stack = ClientHelper.getClient().player.getHeldItemMainhand();
		
		if(stack.getItem() == ModItems.EDIT_ITEM && stack.getMetadata() == 1) {
			if(event.getGui() instanceof GuiEditCommandBlockMinecart)
				event.setCanceled(true);
			
		}
	}
}
