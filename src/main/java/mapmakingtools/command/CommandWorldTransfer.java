package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.SelectedPoint;
import mapmakingtools.tools.WorldData;
import mapmakingtools.tools.worldtransfer.PacketAddArea;
import mapmakingtools.tools.worldtransfer.PacketPasteNotify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
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
	public void processCommand(ICommandSender sender, String[] param) {
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
			
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int y = data.getMinY(); y <= data.getMaxY(); ++y) {
					for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
						CachedBlock undo = new CachedBlock(world, x, y, z, player);
						list.add(undo);
					}
				}
			}
			
			if(list.size() > 1000)
				throw new CommandException("mapmakingtools.commands.build.worldtransfer.toolarge", new Object[0]);
			
			MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketAddArea(name, false, list), player);
		}
		else if("paste".equals(mode)) {
			if(param.length < 2)
				throw new WrongUsageException(this.getCommandUsage(sender) + ".mode", new Object[0]);
			
			String name = param[1];
			
			MapMakingTools.NETWORK_MANAGER.sendPacketToPlayer(new PacketPasteNotify(name), player);
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
