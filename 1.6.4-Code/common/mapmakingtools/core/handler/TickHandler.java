package mapmakingtools.core.handler;

import java.util.EnumSet;
import java.util.logging.Level;

import mapmakingtools.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * @author ProPercivalalb
 **/
public class TickHandler implements ITickHandler {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static float particleTicks;
	
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if (type.equals(EnumSet.of(TickType.RENDER))) {
            onRenderTick();
        	float ticks1 = ((Float)tickData[0]).floatValue();
        	particleTicks = ticks1;
            onRenderTickEnd(ticks1);
        }
        else if (type.equals(EnumSet.of(TickType.CLIENT))) {
            GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
            if (guiscreen != null) {
                onTickInGUI(guiscreen);
            } 
            else {
                onTickInGame();
            }
        }
        else if (type.equals(EnumSet.of(TickType.PLAYER))) {
        	EntityPlayer player = (EntityPlayer)tickData[0];
        }
        
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT, TickType.PLAYER, TickType.WORLD);
    }

    @Override
    public String getLabel() {
    	return Reference.MOD_ID + "(Client)";
    }

    public void onRenderTick() {
    	
    }

    public void onRenderTickEnd(float ticks) {

    }

    public void onTickInGUI(GuiScreen guiscreen) {
		
    }

    public void onTickInGame() {
    	onTick(mc);
    }
       
    public void onTick(Minecraft mc) {   
    	
    }
}