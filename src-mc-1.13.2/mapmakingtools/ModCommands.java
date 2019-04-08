package mapmakingtools;

import com.mojang.brigadier.CommandDispatcher;

import mapmakingtools.command.BoxCommand;
import mapmakingtools.command.ClearPointsCommand;
import mapmakingtools.command.CopyCommand;
import mapmakingtools.command.CopyInventoryCommand;
import mapmakingtools.command.ExpandCommand;
import mapmakingtools.command.FlipCommand;
import mapmakingtools.command.FloorCommand;
import mapmakingtools.command.KillAllCommand;
import mapmakingtools.command.MazeCommand;
import mapmakingtools.command.PasteCommand;
import mapmakingtools.command.Pos1Command;
import mapmakingtools.command.Pos2Command;
import mapmakingtools.command.RedoCommand;
import mapmakingtools.command.ReplaceCommand;
import mapmakingtools.command.RoofCommand;
import mapmakingtools.command.RotateCommand;
import mapmakingtools.command.SelectionSizeCommand;
import mapmakingtools.command.SetBiomeCommand;
import mapmakingtools.command.SetCommand;
import mapmakingtools.command.ShrinkCommand;
import mapmakingtools.command.UndoCommand;
import mapmakingtools.command.WallCommand;
import mapmakingtools.lib.Reference;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * @author ProPercivalalb
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

	@SubscribeEvent
    public static void initCommands(final FMLServerStartingEvent event) {
    	CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
    	
    	Pos1Command.register(dispatcher);
    	Pos2Command.register(dispatcher);
    	ClearPointsCommand.register(dispatcher);
    	SelectionSizeCommand.register(dispatcher);
    	ExpandCommand.register(dispatcher);
    	ShrinkCommand.register(dispatcher);
    	
    	
    	SetCommand.register(dispatcher);
    	
    	WallCommand.register(dispatcher);
    	FloorCommand.register(dispatcher);
    	RoofCommand.register(dispatcher);
    	BoxCommand.register(dispatcher);
    	
    	ReplaceCommand.register(dispatcher);
    	
    	CopyCommand.register(dispatcher);
    	PasteCommand.register(dispatcher);
    	RotateCommand.register(dispatcher);
    	FlipCommand.register(dispatcher);
    	
    	UndoCommand.register(dispatcher);
    	RedoCommand.register(dispatcher);
    	
    	SetBiomeCommand.register(dispatcher);
    	MazeCommand.register(dispatcher);
    	
    	CopyInventoryCommand.register(dispatcher);
    	KillAllCommand.register(dispatcher);
    	
        /**event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandRoof());
        event.registerServerCommand(new CommandFloor());
        event.registerServerCommand(new CommandWall());
        event.registerServerCommand(new CommandBox());
        event.registerServerCommand(new CommandOutline());
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
        event.registerServerCommand(new CommandDrawPNG());
        event.registerServerCommand(new CommandMove());
        event.registerServerCommand(new CommandMaze());
        
        event.registerServerCommand(new CommandWorldTransfer());
        event.registerServerCommand(new CommandKillAll());
        
        event.registerServerCommand(new CommandDebug());
        event.registerServerCommand(new CommandCopyInventory());**/
        
    }
}
