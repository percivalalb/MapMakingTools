package mapmakingtools.handler;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KeyboardInput {

    public static final KeyBinding KEY_ITEM_EDITOR = new KeyBinding("mapmakingtools.key.itemeditor", Keyboard.KEY_M, "mapmakingtools.key.category");


	@SubscribeEvent
	public void onGuiKeyboardEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		GuiScreen guiScreen = event.getGui();
		if(guiScreen instanceof GuiContainer && KEY_ITEM_EDITOR.isActiveAndMatches(Keyboard.getEventKey())) {
			GuiContainer guiContainer = (GuiContainer)guiScreen;
			int x = Mouse.getEventX() * guiScreen.width / guiScreen.mc.displayWidth;
			int y = guiScreen.height - Mouse.getEventY() * guiScreen.height / guiScreen.mc.displayHeight - 1;
			
			if(Keyboard.getEventKeyState()) {
				Slot slot = guiContainer.getSlotUnderMouse();
                    
				if(slot != null && slot.inventory instanceof InventoryPlayer && slot.isEnabled()) {
					if(slot.getHasStack()) {
						PacketDispatcher.sendToServer(new PacketOpenItemEditor(slot.getSlotIndex()));
						event.setCanceled(true);
					}
        		}
			}
		}
	}
}
