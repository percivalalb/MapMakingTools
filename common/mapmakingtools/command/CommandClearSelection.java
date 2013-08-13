package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.core.handler.FlyHandler;
import mapmakingtools.core.helper.LookHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.proxy.CommonProxy;
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
public class CommandClearSelection extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/clearselections";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.clearPostions.usage";
    }

    public void processCommand(ICommandSender sender, String[] param) {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		DataStorage.setPlayerLeftClick(player, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED);
    		DataStorage.setPlayerRightClick(player, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED, DataStorage.INDEX_UNSELECTED);
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
