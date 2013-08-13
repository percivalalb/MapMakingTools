package mapmakingtools.command;

import mapmakingtools.core.helper.LogHelper;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * @author ProPercivalalb
 */
public class CommandHandler {

    public static void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPotionCreator());
        event.registerServerCommand(new CommandFly());
        //Quick Build Commands
        event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandUndo());
        event.registerServerCommand(new CommandRedo());
        event.registerServerCommand(new CommandFloor());
        event.registerServerCommand(new CommandRoof());
        event.registerServerCommand(new CommandWalls());
        event.registerServerCommand(new CommandClearSelection());
        event.registerServerCommand(new CommandPos1());
        event.registerServerCommand(new CommandPos2());
        event.registerServerCommand(new CommandCopy());
        event.registerServerCommand(new CommandPaste());
        event.registerServerCommand(new CommandRotation());
        event.registerServerCommand(new CommandReplace());
        event.registerServerCommand(new CommandFlip());
        event.registerServerCommand(new CommandPlayerStatue());
        event.registerServerCommand(new CommandSetBiome());
        event.registerServerCommand(new CommandCombineSpawners());
        //event.registerServerCommand(new CommandFloatingIsland());
        //Mod Commands
        event.registerServerCommand(new CommandConvertStructure());
    }
}
