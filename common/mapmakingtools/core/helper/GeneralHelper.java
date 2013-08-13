package mapmakingtools.core.helper;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.CoreModManager;

import mapmakingtools.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

/**
 * @author ProPercivalalb
 */
public class GeneralHelper {
	
	public static boolean inCreative(EntityPlayer player) {
		return player.capabilities.isCreativeMode;
	}
	
	public static boolean isDebugModeEnabled() {
		return Reference.DEBUG;
	}
	
	public static boolean isDevEnviroment() {
		return ReflectionHelper.getField(CoreModManager.class, Boolean.TYPE, null, "deobfuscatedEnvironment"); 
	}
}
