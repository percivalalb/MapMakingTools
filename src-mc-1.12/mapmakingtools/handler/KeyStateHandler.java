package mapmakingtools.handler;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * @author ProPercivalalb
 **/
public class KeyStateHandler {
	
    public static final KeyBinding keyItemEditor = new KeyBinding("mapmakingtools.key.itemeditor", Keyboard.KEY_M, "mapmakingtools.key.category");

    protected boolean keyDown;
    
    @SubscribeEvent
    public void keyEvent(ClientTickEvent event) {
    	this.keyTick(event.phase == Phase.END);
    }
    
    private void keyTick(boolean tickEnd) {
    	int keyCode = keyItemEditor.getKeyCode();
        boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
        if (state != keyDown) {
            if (state && !tickEnd) {
            	//Key Pressed
            	if(PlayerAccess.canEdit(ClientHelper.getClient().player) && ClientHelper.getClient().currentScreen instanceof GuiContainer) {
            		GuiContainer container = (GuiContainer)ClientHelper.getClient().currentScreen;
            	    final ScaledResolution scaledresolution = new ScaledResolution(ClientHelper.getClient());
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    int xMouse = Mouse.getX() * i / ClientHelper.getClient().displayWidth;
                    int yMouse = j - Mouse.getY() * j / ClientHelper.getClient().displayHeight - 1;
            		for (int j1 = 0; j1 < container.inventorySlots.inventorySlots.size(); ++j1) {
                        Slot slot = (Slot)container.inventorySlots.inventorySlots.get(j1);
                        
                        if (slot.inventory instanceof InventoryPlayer && this.isPointInRegion(container, slot.xPos, slot.yPos, 16, 16, xMouse, yMouse) && slot.isEnabled()) {
                        	InventoryPlayer playerInventory = (InventoryPlayer)slot.inventory;
                        	if(slot.getHasStack()) {
                            	PacketDispatcher.sendToServer(new PacketOpenItemEditor(slot.getSlotIndex()));
                        	}
                        }
            		}
            	}
            }
            else if(!tickEnd) {
            	//Key Released
            }
            if (tickEnd)
                keyDown = state;
        }
    }
    
    public boolean isPointInRegion(GuiContainer container, int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        int i = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 4); //GuiContainer.guiLeft
        int j = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 5); //GuiContainer.guiTop
        pointX = pointX - i;
        pointY = pointY - j;
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }
}