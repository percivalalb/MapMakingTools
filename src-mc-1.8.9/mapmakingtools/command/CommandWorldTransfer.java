package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.Unpooled;
import mapmakingtools.MapMakingTools;
import mapmakingtools.lib.PacketLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.worldtransfer.PacketAddArea;
import mapmakingtools.tools.worldtransfer.PacketPaste;
import mapmakingtools.tools.worldtransfer.PacketPasteNotify;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class CommandWorldTransfer extends CommandBase {

	@Override
	public String getCommandName() {
		return "/worldtransfer";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.worldtransfer.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		
		String mode = param[0].toLowerCase();
		
		if(!this.getModes().contains(mode))
			throw new CommandException("mapmakingtools.commands.build.worldtransfer.modeerror", new Object[] {mode});
		
		if("copy".equals(mode)) {
			
			if(param.length < 2)
				throw new WrongUsageException(this.getCommandUsage(sender) + ".copy", new Object[0]);
			
			if(!data.hasSelectedPoints())
				throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
			
			String name = param[1];
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getMinPos(), data.getMaxPos());
			
			for(BlockPos pos : positions) {
				FMLLog.info("" + pos.toString());
				list.add(BlockCache.createCache(player, world, pos));
			}
			
			try {
				int packetCount = 0;
				
				int index = 0;
				boolean first = true;
				
				int start = 0;
				ArrayList<BlockCache> part = new ArrayList<BlockCache>();
				
				for(BlockCache cache : list) {
					boolean notFinal = false;
					
					int size = cache.calculateSizeCompact();
					if(start + size <= PacketLib.MAX_SIZE_TO_CLIENT) {
						part.add(cache);
						
						start += size;
					}
					else
						notFinal = true;

					
					if(notFinal || index == list.size() - 1) {
						PacketDispatcher.sendTo(new PacketAddArea(name, part, first, index == list.size() - 1, data), player);
						packetCount += 1;
						start = 0;
						first = false;
						part.clear();
						
						if(notFinal) {
							part.add(cache);
							start += size;
						}
					}
					
					index += 1;
				}

			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if("paste".equals(mode)) {
			if(param.length < 2)
				throw new WrongUsageException(this.getCommandUsage(sender) + ".paste", new Object[0]);
			
			String name = param[1];
			
			PacketDispatcher.sendTo(new PacketPasteNotify(name), player);
		}
		else if("gui".equals(mode)) {
			player.openGui(MapMakingTools.instance, CommonProxy.GUI_ID_WORLD_TRANSFER, player.worldObj, 0, 0, 0);
		}
		
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] param, BlockPos pos) {
        return param.length == 1 ? getListOfStringsMatchingLastWord(param, getModes()) : null;
    }
	
	public static List<String> getModes() {
		return Arrays.asList("copy", "paste", "gui");
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
}
