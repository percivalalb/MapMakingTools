package mapmakingtools.handler;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiWorldTransfer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;

@OnlyIn(Dist.CLIENT)
public class GuiHandler {
	
    public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer) {
    	String guiId = openContainer.getId().toString();
    	
    	EntityPlayer player = MapMakingTools.PROXY.getPlayerEntity();
    	
    	if(guiId.equals("mapmakingtools:item_editor")) {
    		int slotId = openContainer.getAdditionalData().readInt();
        	
    		return new GuiItemEditor(player, slotId);
    	}
    	else if(guiId.equals("mapmakingtools:filter")) {
    		byte type = openContainer.getAdditionalData().readByte();
    		
    		if(type == 0) {
	    		int entityId = openContainer.getAdditionalData().readInt();
	    		
	    		List<FilterClient> filterList = FilterManager.getClientEntitiesFilters(player, player.world.getEntityByID(entityId));
				if(filterList.size() > 0)
					return new GuiFilter(filterList, player, entityId);
    		}
    		else if(type == 1) {
    			BlockPos pos = openContainer.getAdditionalData().readBlockPos();
        		
        		List<FilterClient> filterList = FilterManager.getClientBlocksFilters(player, player.world, pos);
    			if(filterList.size() > 0)
    				return new GuiFilter(filterList, player, pos.toImmutable());
    		}
    		
    	}
    	else if(guiId.equals("mapmakingtools:world_transfer")) {
    		return new GuiWorldTransfer();
    	}
    	
    	return null;
    }
}