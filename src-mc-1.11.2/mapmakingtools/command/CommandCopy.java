package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jline.internal.Nullable;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandCopy extends CommandBase {

	@Override
	public String getName() {
		return "/copy";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.copy.usage";
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

		ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
		Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
		
		for(BlockPos pos : positions) {
			list.add(BlockCache.createCache(player, world, pos));
		}

		int blocksChanged = data.getActionStorage().addCopy(list);
			
		if(blocksChanged > 0) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.copy.complete", "" + blocksChanged);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
