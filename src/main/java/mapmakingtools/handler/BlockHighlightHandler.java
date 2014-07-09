package mapmakingtools.handler;

import org.lwjgl.opengl.GL11;

import mapmakingtools.helper.ClientHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class BlockHighlightHandler {

	@SubscribeEvent
	public void blockHighlight(DrawBlockHighlightEvent event) {
		
	}
}
