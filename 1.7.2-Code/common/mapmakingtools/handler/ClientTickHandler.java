package mapmakingtools.handler;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.VersionHelper;
import mapmakingtools.helper.VersionHelper.UpdateType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 **/
public class ClientTickHandler {
	
	public static boolean checkedVersion = false;
	
	@SubscribeEvent
	public void tickEnd(ClientTickEvent event) {
		if(event.phase != Phase.END || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT)
			return;
		GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
		if (guiscreen == null) {
          	if(!checkedVersion && ClientHelper.mc.thePlayer != null) {
          		VersionHelper.checkVersion(UpdateType.COLOURED);
          		checkedVersion = true;
          	}
		}
	}
}