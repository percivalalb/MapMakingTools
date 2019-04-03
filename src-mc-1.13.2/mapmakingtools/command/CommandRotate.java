package mapmakingtools.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandRotate extends CommandBase {

	@Override
	public String getName() {
		return "/rotate";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.rotate.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToPaste())
			throw new CommandException("mapmakingtools.commands.build.nothingtorotate", new Object[0]);
		
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			Rotation rotation = Rotation.values()[Numbers.parse(args[0]) / 90];


			if(rotation == null)
				throw new CommandException("mapmakingtools.commands.build.rotatemodeerror", new Object[0]);
			
			boolean didChange = data.getActionStorage().setRotation(rotation);
			
			if(didChange) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.rotate.complete", args[0]);
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			}
			else {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.rotationnot90");
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			}
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, getModeNames()) : Collections.<String>emptyList();
	}
	
	public static List<String> getModeNames() {
		return Arrays.asList("0", "90", "180", "270");
	}
}
