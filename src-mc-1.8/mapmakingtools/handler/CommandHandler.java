package mapmakingtools.handler;

import mapmakingtools.command.CommandClearPoints;
import mapmakingtools.command.CommandCopy;
import mapmakingtools.command.CommandCopyInventory;
import mapmakingtools.command.CommandDebug;
import mapmakingtools.command.CommandFlip;
import mapmakingtools.command.CommandFloor;
import mapmakingtools.command.CommandKillAll;
import mapmakingtools.command.CommandMaze;
import mapmakingtools.command.CommandMove;
import mapmakingtools.command.CommandPaste;
import mapmakingtools.command.CommandPlayerStatue;
import mapmakingtools.command.CommandPos1;
import mapmakingtools.command.CommandPos2;
import mapmakingtools.command.CommandRedo;
import mapmakingtools.command.CommandReplace;
import mapmakingtools.command.CommandRoof;
import mapmakingtools.command.CommandRotate;
import mapmakingtools.command.CommandSelectionSize;
import mapmakingtools.command.CommandSet;
import mapmakingtools.command.CommandSetBiome;
import mapmakingtools.command.CommandUndo;
import mapmakingtools.command.CommandWall;
import mapmakingtools.command.CommandWorldTransfer;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * @author ProPercivalalb
 */
public class CommandHandler {

    public static void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandClearPoints());
        event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandRoof());
        event.registerServerCommand(new CommandFloor());
        event.registerServerCommand(new CommandWall());
        event.registerServerCommand(new CommandRotate());
        event.registerServerCommand(new CommandFlip());
        event.registerServerCommand(new CommandReplace());
        event.registerServerCommand(new CommandCopy());
        event.registerServerCommand(new CommandPaste());
        event.registerServerCommand(new CommandUndo());
        event.registerServerCommand(new CommandRedo());
        event.registerServerCommand(new CommandPos1());
        event.registerServerCommand(new CommandPos2());
        event.registerServerCommand(new CommandSetBiome());
        event.registerServerCommand(new CommandPlayerStatue());
        event.registerServerCommand(new CommandSelectionSize());
       // event.registerServerCommand(new CommandExpand());
        //event.registerServerCommand(new CommandShrink());
        event.registerServerCommand(new CommandMove());
        event.registerServerCommand(new CommandMaze());
        
        event.registerServerCommand(new CommandWorldTransfer());
        event.registerServerCommand(new CommandKillAll());
        
        event.registerServerCommand(new CommandDebug());
        event.registerServerCommand(new CommandCopyInventory());
        
    }
}
