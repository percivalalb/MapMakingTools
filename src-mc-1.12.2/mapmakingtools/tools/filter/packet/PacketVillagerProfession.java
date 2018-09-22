package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.filter.VillagerProfessionServerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerProfession extends AbstractServerMessage {

	public VillagerProfession professionId;
	
	public PacketVillagerProfession() {}
	public PacketVillagerProfession(VillagerProfession profession) {
		this.professionId = profession;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.professionId = VillagerRegistry.getById(packetbuffer.readInt());
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(VillagerRegistry.getId(this.professionId));
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		
		if(player.openContainer instanceof IContainerFilter) {
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerProfessionServerFilter) {
				VillagerProfessionServerFilter shop = (VillagerProfessionServerFilter)container.getCurrentFilter();
				Entity entity = container.getEntity();
				if(entity instanceof EntityVillager) {
					EntityVillager villager = (EntityVillager)entity;
					villager.setProfession(this.professionId);
					
					TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagerprofession.complete", this.professionId.getRegistryName());
					chatComponent.getStyle().setItalic(true);
					player.sendMessage(chatComponent);
				}
			}
		}

	}
}
