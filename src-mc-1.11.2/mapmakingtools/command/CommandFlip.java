package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jline.internal.Nullable;
import mapmakingtools.api.enums.MovementType;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandFlip extends CommandBase {

	@Override
	public String getName() {
		return "/flip";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.flip.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.world;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		
		if(args.length < 1)
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		else {
			Mirror mirror = Mirror.values()[NumberParse.getInteger(args[0])];


			if(mirror.equals(Mirror.NONE))
				throw new CommandException("mapmakingtools.commands.build.flipmodeerror", new Object[] {mirror});
				
			data.getActionStorage().setFlipping(mirror);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
			}

			int blocksChanged = data.getActionStorage().flip(list);
			
			if(blocksChanged > 0) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.flip.complete", "" + blocksChanged);
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
			}
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getModeNames()) : Collections.<String>emptyList();
    }
	
	public static List<String> getModeNames() {
		return Arrays.asList("0", "1", "2");
	}
}
