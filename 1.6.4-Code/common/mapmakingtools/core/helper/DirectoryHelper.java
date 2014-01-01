package mapmakingtools.core.helper;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * @author ProPercivalalb
 */
public class DirectoryHelper {

	public static File mcDir;
	
	public static void setMcDir(FMLPreInitializationEvent event) {
		mcDir = event.getModConfigurationDirectory().getParentFile();
	}
}
