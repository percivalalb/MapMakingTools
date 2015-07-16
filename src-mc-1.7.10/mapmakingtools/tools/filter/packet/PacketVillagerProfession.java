package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.filter.VillagerProfessionServerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class PacketVillagerProfession extends IPacket {

	public int professionId;
	
	public PacketVillagerProfession() {}
	public PacketVillagerProfession(int profession) {
		this.professionId = profession;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.professionId = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.professionId);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof IContainerFilter) {
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerProfessionServerFilter) {
				VillagerProfessionServerFilter shop = (VillagerProfessionServerFilter)container.getCurrentFilter();
				Entity entity = container.getEntity();
				if(entity instanceof EntityVillager) {
					EntityVillager villager = (EntityVillager)entity;
					villager.setProfession(this.professionId);
					
					ChatComponentTranslation chatComponent = new ChatComponentTranslation("mapmakingtools.filter.villagerprofession.complete", this.professionId);
					chatComponent.getChatStyle().setItalic(true);
					player.addChatMessage(chatComponent);
				}
			}
		}

	}
}
