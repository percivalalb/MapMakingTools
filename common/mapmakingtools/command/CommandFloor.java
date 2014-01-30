package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.CachedBlock;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandFloor extends CommandBase {

	@Override
	public String getCommandName() {
		return "/floor";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.floor.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(param.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block block = func_147180_g(sender, param[0]);
			int meta = 0;
			
			if(param.length == 2)
				meta = parseInt(sender, param[1]);
			
			int minY = data.getMinY();
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			int blocks = 0;
			
			for(int x = data.getMinX(); x <= data.getMaxX(); ++x) {
				for(int z = data.getMinZ(); z <= data.getMaxZ(); ++z) {
					CachedBlock undo = new CachedBlock(world, x, minY, z);
					world.func_147465_d(x, minY, z, block, meta, 2);
					list.add(undo);
					++blocks;
				}
			}

			data.getActionStorage().addUndo(list);

			ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.commands.build.floor.complete", "" + blocks, param[0]);
			chatComponent.func_150256_b().func_150238_a(EnumChatFormatting.ITALIC);
			player.func_145747_a(chatComponent);
			
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Block.field_149771_c.func_148742_b()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
	
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

}
