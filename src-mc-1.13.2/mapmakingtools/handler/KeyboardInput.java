package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class KeyboardInput {

    public static final KeyBinding KEY_ITEM_EDITOR = new KeyBinding("mapmakingtools.key.itemeditor", InputMappings.getInputByName("key.keyboard.m").getKeyCode(), "mapmakingtools.key.category");


	@SubscribeEvent
	public void onGuiKeyboardEvent(KeyboardKeyPressedEvent.Pre event) {
		//TODO 
		GuiScreen guiScreen = event.getGui();
		if(guiScreen instanceof GuiContainer && KEY_ITEM_EDITOR.isActiveAndMatches(InputMappings.getInputByCode(event.getKeyCode(), event.getScanCode()))) {
			GuiContainer guiContainer = (GuiContainer)guiScreen;

			//double x = guiScreen.mc.mouseHelper.getMouseX() * guiScreen.width / guiScreen.mc.mainWindow.getWidth();
			//double y = guiScreen.height - guiScreen.mc.mouseHelper.getMouseY() * guiScreen.height / guiScreen.mc.mainWindow.getHeight() - 1;
			
			//if(Keyboard.getEventKeyState()) {
				Slot slot = guiContainer.getSlotUnderMouse();
                    
				if(slot != null && slot.inventory instanceof InventoryPlayer && slot.isEnabled()) {
					if(slot.getHasStack()) {
						guiScreen.mc.displayGuiScreen(new GuiItemEditor(MapMakingTools.PROXY.getPlayerEntity(), slot.getSlotIndex()));
						PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketOpenItemEditor(slot.getSlotIndex()));
						event.setCanceled(true);
					}
        		}
				
		}
	}
}
