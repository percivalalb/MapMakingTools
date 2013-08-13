package mapmakingtools.core.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet250CustomPayload;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

/**
 * @author ProPercivalalb
 **/
public class MMTKeyHandler extends KeyHandler {
	
    static KeyBinding openItemEditor = new KeyBinding("Item Editor", Keyboard.KEY_M);

    public MMTKeyHandler() {
        super(new KeyBinding[]{openItemEditor}, new boolean[]{false});
    }

    @Override
    public String getLabel() {
         return Reference.MOD_NAME + " Key Bindings";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
    	//PacketTypeHandler.populatePacketAndSendToServer(new PacketOpenItemEditor());
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }
}