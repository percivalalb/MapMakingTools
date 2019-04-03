package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.CommandBlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCustomGive extends AbstractServerMessage {
	
	public PacketCustomGive() {}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		

		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			
			CommandBlockBaseLogic logic = CommandBlockUtil.getCommandLogic(container);

			if(container.getSlot(0).getStack() != null) {
    			String command = "/give @p";
    			ItemStack stack = container.getSlot(0).getStack().copy();
    			command += " " + Item.REGISTRY.getNameForObject(stack.getItem());
    			command += " " + stack.getCount();
    			command += " " + stack.getItemDamage();
    			
    			if(stack.hasTagCompound())
    				command += " " + String.valueOf(stack.getTagCompound());
				
    			CommandBlockUtil.setCommand(logic, command);
    			
    			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.customgive.complete", command);
				chatComponent.getStyle().setItalic(true);
				player.sendMessage(chatComponent);
    		}
			
		}
	}

}
