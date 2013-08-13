package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.LookHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.proxy.CommonProxy;
import mapmakingtools.core.util.CachedBlockPlacement;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.NBTData;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketFly;
import mapmakingtools.network.packet.PacketSkullModify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 **/
public class CommandFloatingIsland extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/floatingisland";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.floatingIsland.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		World world = player.worldObj;
    		int secMinX = DataStorage.getSelectedPosFromPlayer(player)[0];
    		int secMinY = DataStorage.getSelectedPosFromPlayer(player)[1];
    		int secMinZ = DataStorage.getSelectedPosFromPlayer(player)[2];
    		int secMaxX = DataStorage.getSelectedPosFromPlayer(player)[3];
    		int secMaxY = DataStorage.getSelectedPosFromPlayer(player)[4];
    		int secMaxZ = DataStorage.getSelectedPosFromPlayer(player)[5]; 
    		int minX = MathHelper.small(secMinX, secMaxX);
    		int minY = MathHelper.small(secMinY, secMaxY);
    		int minZ = MathHelper.small(secMinZ, secMaxZ);
    		int maxX = MathHelper.big(secMinX, secMaxX)+1;
    		int maxY = MathHelper.big(secMinY, secMaxY)+1;
    		int maxZ = MathHelper.big(secMinZ, secMaxZ)+1;
    		
			ArrayList<CachedBlockPlacement> list = new ArrayList<CachedBlockPlacement>();
			int blocks = 0;
			float chance = Float.parseFloat("2") / 100F;
			for(int y = minY; y < minY + 2; ++y) {
				for(int x = minX; x < maxX; ++x) {
					for(int z = minZ; z < maxZ; ++z) {
						
						if(world.getBlockId(x - 1, y, z) != 0 && world.getBlockId(x+1, y, z) != 0 && world.getBlockId(x, y, z-1) != 0 && world.getBlockId(x-1, y, z+1) != 0 && world.rand.nextFloat() > chance) {
							++blocks;
							CachedBlockPlacement undo = new CachedBlockPlacement(world, x, y, z);
							list.add(undo);
							world.setBlock(x, y, z, 0, 0, 2);
							++blocks;
						}
					} 
				}
			}
			//String blockName = blockId == 0 ? "Air" : new ItemStack(blockId, 1, blockMeta).getDisplayName();
			player.addChatMessage(blocks + " block(s) have been changed.");
			DataStorage.addUndo(list, player);
    	}
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] par2ArrayOfStr) {
    	return null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
