package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.Unpooled;
import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.worldtransfer.PacketAddArea;
import mapmakingtools.tools.worldtransfer.PacketPasteNotify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

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
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
			}
			
			try {
			
				PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
				ArrayList<Integer> sendClientData = new ArrayList<Integer>();
				ArrayList<Integer> sendServerData = new ArrayList<Integer>();
				
				int startClient = packetbuffer.writerIndex();
				int startServer = packetbuffer.writerIndex();
				int totalClient = 0;
				int totalServer = 0;
				for(int i = 0; i < list.size(); ++i) {
					int s = packetbuffer.writerIndex();
					list.get(i).writeToPacketBuffer(packetbuffer);
					int e = packetbuffer.writerIndex();
					
					if(e - startClient > 30000) {
						sendClientData.add(totalClient);
						startClient = s;
						totalClient = 1;
						
						if(i == list.size() - 1)
							sendClientData.add(totalClient);
					}
					else {
						totalClient += 1;
						
						if(i == list.size() - 1)
							sendClientData.add(totalClient);
					}
					
					if(e - startServer > 1000000) {
						sendServerData.add(totalServer);
						startServer = s;
						totalServer = 1;
						
						if(i == list.size() - 1)
							sendServerData.add(totalServer);
					}
					else {
						totalServer += 1;
						
						if(i == list.size() - 1)
							sendServerData.add(totalServer);
					}
				}
				
				int start2 = 0;
				
				for(int i = 0; i < sendServerData.size(); ++i) {
					int amount = sendServerData.get(i);
					ArrayList<BlockCache> part = new ArrayList<BlockCache>();
					
					for(int j = start2; j < start2 + amount; ++j)
						part.add(list.get(j));
					
					PacketDispatcher.sendTo(new PacketAddArea(name, part, sendClientData, i == 0, i == sendServerData.size() - 1), player);
					start2 += amount;
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
	public List addTabCompletionOptions(ICommandSender sender, String[] param) {
        return param.length == 1 ? getListOfStringsFromIterableMatchingLastWord(param, getModes()) : null;
    }
	
	public static List<String> getModes() {
		return Arrays.asList("copy", "paste", "gui");
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
