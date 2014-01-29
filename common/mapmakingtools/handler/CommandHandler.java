package mapmakingtools.handler;

import mapmakingtools.command.*;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * @author ProPercivalalb
 */
public class CommandHandler {

    public static void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTemp());
        event.registerServerCommand(new CommandClearPoints());
        event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandRoof());
        event.registerServerCommand(new CommandFloor());
        event.registerServerCommand(new CommandUndo());
        event.registerServerCommand(new CommandRedo());
    }
}
