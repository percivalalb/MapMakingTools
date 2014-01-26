package mapmakingtools.util;

import java.lang.reflect.Field;

import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.helper.ServerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 */
public class SpawnerUtil {

	private static Field mobIdField = ReflectionHelper.getField(MobSpawnerBaseLogic.class, 1);
	
	
	public static String getMobId(MobSpawnerBaseLogic spawnerLogic) {
		return ReflectionHelper.getField(mobIdField, String.class, spawnerLogic);
	}

	public static void setMobId(MobSpawnerBaseLogic spawnerLogic, String mobId) {//, int minecartIndex) {
		//minecartIndex = minecartIndex == -1 ? spawnerLogic.: 
		ReflectionHelper.setField(mobIdField, spawnerLogic, mobId);
	}
	
	public static ItemStack[] getMobArmor(MobSpawnerBaseLogic spawnerLogic) {
		ItemStack[] equipment = new ItemStack[5];
		if(spawnerLogic.getRandomMinecart() != null) {
			NBTTagCompound tag =  spawnerLogic.getRandomMinecart().field_98222_b;
			if (tag.hasKey("Equipment")) {
				NBTTagList nbttaglist = (NBTTagList)tag.getTag("Equipment");

		        for (int i = 0; i < equipment.length; ++i) {
		            equipment[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbttaglist.func_150305_b(i));
		        }
		    }
		}
		return equipment;
	}
	
	public static void sendSpawnerPacketToAllPlayers(TileEntityMobSpawner spawner) {
		if(!ServerHelper.isServer())
			return;
		MinecraftServer server = MinecraftServer.getServer();
		server.getConfigurationManager().func_148537_a(spawner.func_145844_m(), spawner.func_145831_w().provider.dimensionId);
	}

}
