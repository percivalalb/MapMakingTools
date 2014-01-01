package mapmakingtools.core.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

/**
 * @author ProPercivalalb
 **/
public class MMTKeyHandler extends KeyHandler {
	
    static KeyBinding openItemEditor = new KeyBinding("Item Editor", Keyboard.KEY_M);
    static Minecraft mc = Minecraft.getMinecraft();

    public MMTKeyHandler() {
        super(new KeyBinding[]{openItemEditor}, new boolean[]{false});
    }

    @Override
    public String getLabel() {
         return Reference.MOD_NAME + " Key Bindings";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
    	if(kb == openItemEditor && !tickEnd && mc.currentScreen instanceof GuiContainer) {
    		GuiContainer container = (GuiContainer)mc.currentScreen;
            int xMouse = Mouse.getX() * container.width / this.mc.displayWidth;
            int yMouse = container.height - Mouse.getY() * container.height / this.mc.displayHeight - 1;
    		
    		for (int j1 = 0; j1 < container.inventorySlots.inventorySlots.size(); ++j1) {
                Slot slot = (Slot)container.inventorySlots.inventorySlots.get(j1);
                
                if (slot.inventory instanceof InventoryPlayer && isPointInRegion(container, slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, xMouse, yMouse) && slot.func_111238_b()) {
                	if(slot.getHasStack()) {
                		ItemStack stack = slot.getStack();
                		int index = 0;
                		for(int i = 0; i < ((InventoryPlayer)slot.inventory).getSizeInventory(); ++i) {
                			ItemStack playerStack = ((InventoryPlayer)slot.inventory).getStackInSlot(i);
                			if(stack == playerStack) {
                				index = i;
                			}
                		}
                		
                		LogHelper.logDebug(stack.getDisplayName() + " " + index);
                    	PacketTypeHandler.populatePacketAndSendToServer(new PacketOpenItemEditor(index));
                	}
                }
            }
    	}
    }
    
    private boolean isMouseOverSlot(GuiContainer container, Slot slot, int par2, int par3) {
        return this.isPointInRegion(container, slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, par2, par3);
    }
    
    protected boolean isPointInRegion(GuiContainer container, int par1, int par2, int par3, int par4, int par5, int par6) {
        int k1 = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 5);
        int l1 = ReflectionHelper.getField(GuiContainer.class, Integer.TYPE, container, 6);
        par5 -= k1;
        par6 -= l1;
        return par5 >= par1 - 1 && par5 < par1 + par3 + 1 && par6 >= par2 - 1 && par6 < par2 + par4 + 1;
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }
}