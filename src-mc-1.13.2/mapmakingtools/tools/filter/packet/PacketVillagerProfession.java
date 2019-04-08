package mapmakingtools.tools.filter.packet;

import java.util.function.Supplier;

import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerProfession {

	public VillagerProfession professionId;
	public int minecartIndex;
	
	public PacketVillagerProfession(VillagerProfession profession,  int minecartIndex) {
		this.professionId = profession;
		this.minecartIndex = minecartIndex;
	}
	
	public static void encode(PacketVillagerProfession msg, PacketBuffer buf) {
		buf.writeResourceLocation(msg.professionId.getRegistryName());
		buf.writeInt(msg.minecartIndex);
	}
	
	public static PacketVillagerProfession decode(PacketBuffer buf) {
		VillagerProfession professionId = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(buf.readResourceLocation());
		int minecartIndex = buf.readInt();
		return new PacketVillagerProfession(professionId, minecartIndex);
	}
	
	public static class Handler {
        public static void handle(final PacketVillagerProfession msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(player.openContainer instanceof ContainerFilter) {
        			ContainerFilter container = (ContainerFilter)player.openContainer;
        			
        			if(SpawnerUtil.isSpawner(container)) {
        				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
        			
        				SpawnerUtil.setVillagerProfession(spawnerLogic, msg.minecartIndex, VillagerRegistry.getId(msg.professionId));
        				
        				if(container.getTargetType() == TargetType.BLOCK) {
        					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
        	
        					//TODO PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
        					PacketUtil.sendTileEntityUpdateToWatching(spawner);
        				}
        			}
        			else if(container.getTargetType() == TargetType.ENTITY) {
        				Entity entity = container.getEntity();
        				if(entity instanceof EntityVillager) {
        					EntityVillager villager = (EntityVillager)container.getEntity();
        					villager.setProfession(msg.professionId);
        				}
        			}
        			
        			player.sendMessage(new TextComponentTranslation("mapmakingtools.filter.villagerprofession.complete", msg.professionId.getRegistryName()).applyTextStyle(TextFormatting.ITALIC));
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
