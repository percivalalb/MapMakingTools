package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.CommandBlockUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCommandBlockAlias extends AbstractServerMessage {

	public String name;
	
	public PacketCommandBlockAlias() {}
	public PacketCommandBlockAlias(String name) {
		this.name = name;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.name = packetbuffer.readString(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeString(this.name);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;

		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			CommandBlockBaseLogic logic = CommandBlockUtil.getCommandLogic(container);

			CommandBlockUtil.setName(logic, this.name);
			
    		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.commandblockalias.complete", this.name);
			chatComponent.getStyle().setItalic(true);
			player.sendMessage(chatComponent);
			
		}
	}
}
