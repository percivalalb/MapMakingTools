package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.api.filter.FilterBase.TargetType;
import mapmakingtools.helper.Numbers;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.VillagerProfessionServerFilter;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerProfession extends AbstractServerMessage {

	public VillagerProfession professionId;
	public int minecartIndex;
	
	public PacketVillagerProfession() {}
	public PacketVillagerProfession(VillagerProfession profession,  int minecartIndex) {
		this.professionId = profession;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.professionId = VillagerRegistry.getById(packetbuffer.readInt());
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(VillagerRegistry.getId(this.professionId));
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			if(SpawnerUtil.isSpawner(container)) {
				MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(container);
			
				SpawnerUtil.setVillagerProfession(spawnerLogic, this.minecartIndex, VillagerRegistry.getId(this.professionId));
				
				if(container.getTargetType() == TargetType.BLOCK) {
					TileEntityMobSpawner spawner = (TileEntityMobSpawner)player.world.getTileEntity(container.getBlockPos());
	
					PacketDispatcher.sendTo(new PacketUpdateBlock(spawner, container.getBlockPos(), true), player);
					PacketUtil.sendTileEntityUpdateToWatching(spawner);
				}
			}
			else if(container.getTargetType() == TargetType.ENTITY) {
				Entity entity = container.getEntity();
				if(entity instanceof EntityVillager) {
					EntityVillager villager = (EntityVillager)container.getEntity();
					villager.setProfession(this.professionId);
				}
			}
			
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagerprofession.complete", this.professionId.getRegistryName());
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
		}
	}
}
