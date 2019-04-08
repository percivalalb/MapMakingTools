package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.helper.ClientHelper;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEditCommandBlockMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiOpen {

	@SubscribeEvent
	public void onPreRenderGameOverlay(GuiOpenEvent event) {
		if(ClientHelper.getClient().player == null) return;
		ItemStack stack = ClientHelper.getClient().player.getHeldItemMainhand();
		
		if(stack.getItem() == ModItems.WRENCH) {
			if(event.getGui() instanceof GuiEditCommandBlockMinecart)
				event.setCanceled(true);
			else if(event.getGui() instanceof GuiCommandBlock)
				event.setCanceled(true);
		}
	}
}
