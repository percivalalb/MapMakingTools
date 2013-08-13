package mapmakingtools.core.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import mapmakingtools.lib.Reference;

import cpw.mods.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class LogHelper {

    private static Logger mapLogger = Logger.getLogger(Reference.MOD_ID);

    public static void init() {
    	mapLogger.setParent(FMLLog.getLogger());
    }

    public static void log(Level logLevel, String message) {
    	mapLogger.log(logLevel, message);
    }
    
    public static void logWarning(String message) {
    	mapLogger.log(Level.WARNING, message);
    }
    
    public static void logInfo(String message) {
    	mapLogger.log(Level.INFO, message);
    }

    public static void logSevere(String message) {
    	mapLogger.log(Level.SEVERE, message);
    }
    
    /**
     * Logs a message if the debug mode is enabled.
     * @param message The message to log
     */
    public static void logDebug(String message) {
    	if(GeneralHelper.isDebugModeEnabled() || GeneralHelper.isDevEnviroment()) {
    		mapLogger.log(Level.INFO, "[Debug] " + message);
    	}
    }
}
