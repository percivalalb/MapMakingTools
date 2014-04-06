package mapmakingtools.handler;

import mapmakingtools.command.CommandClearPoints;
import mapmakingtools.command.CommandCopy;
import mapmakingtools.command.CommandFlip;
import mapmakingtools.command.CommandFloor;
import mapmakingtools.command.CommandPaste;
import mapmakingtools.command.CommandPos1;
import mapmakingtools.command.CommandPos2;
import mapmakingtools.command.CommandRedo;
import mapmakingtools.command.CommandReplace;
import mapmakingtools.command.CommandRoof;
import mapmakingtools.command.CommandRotate;
import mapmakingtools.command.CommandSet;
import mapmakingtools.command.CommandTemp;
import mapmakingtools.command.CommandUndo;
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
        event.registerServerCommand(new CommandRotate());
        event.registerServerCommand(new CommandFlip());
        event.registerServerCommand(new CommandReplace());
        event.registerServerCommand(new CommandCopy());
        event.registerServerCommand(new CommandPaste());
        event.registerServerCommand(new CommandPos1());
        event.registerServerCommand(new CommandPos2());
    }
}