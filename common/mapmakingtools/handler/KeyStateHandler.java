package mapmakingtools.handler;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.ReflectionHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

/**
 * @author ProPercivalalb
 **/
public class KeyStateHandler {
	
    public static final KeyBinding keyItemEditor = new KeyBinding("mapmakingtools.key.itemEditor", Keyboard.KEY_M, "mapmakingtools.key.category");

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
            	if(ClientHelper.mc.currentScreen instanceof GuiContainer) {
            		GuiContainer container = (GuiContainer)ClientHelper.mc.currentScreen;
            	    final ScaledResolution scaledresolution = new ScaledResolution(ClientHelper.mc.gameSettings, ClientHelper.mc.displayWidth, ClientHelper.mc.displayHeight);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    int xMouse = Mouse.getX() * i / ClientHelper.mc.displayWidth;
                    int yMouse = j - Mouse.getY() * j / ClientHelper.mc.displayHeight - 1;
                	FMLLog.info("" + xMouse + " " + yMouse);
            		for (int j1 = 0; j1 < container.inventorySlots.inventorySlots.size(); ++j1) {
                        Slot slot = (Slot)container.inventorySlots.inventorySlots.get(j1);
                        
                        if (slot.inventory instanceof InventoryPlayer && isPointInRegion(container, slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, xMouse, yMouse) && slot.func_111238_b()) {
                        	if(slot.getHasStack()) {
                        		ItemStack stack = slot.getStack();
                        		int index = slot.getSlotIndex();
                        		
                            	FMLLog.info(stack.getDisplayName() + " " + index);
 
                            	
                            	
                        	}
                        }
            		}
            	}
            }
            else if(!tickEnd) {
            	//Key Released
            	FMLLog.info("Released");
            }
            if (tickEnd)
                keyDown = state;
        }
    }
    
    protected boolean isPointInRegion(GuiContainer container, int par1, int par2, int par3, int par4, int par5, int par6) {
        int k1 = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 4);
        int l1 = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 5);
        par5 -= k1;
        par6 -= l1;
        return par5 >= par1 - 1 && par5 < par1 + par3 + 1 && par6 >= par2 - 1 && par6 < par2 + par4 + 1;
    }
}