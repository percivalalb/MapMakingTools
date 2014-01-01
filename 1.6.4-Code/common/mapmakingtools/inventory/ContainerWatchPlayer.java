package mapmakingtools.inventory;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

/**
 * @author ProPercivalalb
 */
public class ContainerWatchPlayer extends Container {
	
	public String currentWatchedPlayer = "";
	public EntityPlayer player;
	
	public ContainerWatchPlayer(EntityPlayer player) {
		this.player = player;
		byte b0 = 20;
		int i;

        this.addSlots(player, null);
	}
	
	public boolean setWatchedPlayer(String newPlayer) {
		this.currentWatchedPlayer = newPlayer;
	    this.inventorySlots.clear();
	    this.inventoryItemStacks.clear();
	    this.addSlots(player, MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(newPlayer));
	    return true;
	}
	
	public void addSlots(EntityPlayer player, EntityPlayer watchedPlayer) {
		byte b0 = 20;
		int i;
		
		for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 58 + b0));
        }
        
        if(watchedPlayer != null) {
	        b0 = 110;
	        for (i = 0; i < 3; ++i) {
	            for (int j = 0; j < 9; ++j) {
	                this.addSlotToContainer(new Slot(watchedPlayer.inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
	            }
	        }
	
	        for (i = 0; i < 9; ++i) {
	            this.addSlotToContainer(new Slot(watchedPlayer.inventory, i, 8 + i * 18, 58 + b0));
	        }
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}