package mapmakingtools.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonPotentialSpawns;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsAdd;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsRemove;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;

/**
 * @author ProPercivalalb
 */
public abstract class FilterMobSpawnerBase extends FilterClient {

	private static final int BUTTON_ID_START = 200;
	private static final int MAX_POTENTIAL_SPAWNS = 17;
	
	public static int potentialSpawnIndex = 0;
	public int potentialSpawnsCount = 1;
	public List<GuiButton> potentialSpawnButtons = new ArrayList<>();
	
	public void addPotentialSpawnButtons(IGuiFilter gui, int topX, int topY) {
		List<WeightedSpawnerEntity> potentialSpawns = SpawnerUtil.getPotentialSpawns(gui);
		if(potentialSpawns == null) return;

		this.potentialSpawnsCount = potentialSpawns.size();
		this.potentialSpawnButtons.clear();
		
		potentialSpawnIndex = Math.max(Math.min(potentialSpawnIndex, potentialSpawns.size() - 1), 0);
			
		for(int i = 0; i < potentialSpawns.size(); ++i) {
			GuiButtonPotentialSpawns button = new GuiButtonPotentialSpawns(BUTTON_ID_START + i, topX + 14 * i + 2, topY - 13, 13, 12, "" + i);
			button.enabled = i == potentialSpawnIndex;
			this.potentialSpawnButtons.add(button);
			gui.getButtonList().add(button);
		}
	}
	
	public void removePotentialSpawnButtons(IGuiFilter gui, int xMouse, int yMouse, int mouseButton, int topX, int topY) {
		List<WeightedSpawnerEntity> potentialSpawns = SpawnerUtil.getPotentialSpawns(gui);
		if(potentialSpawns == null) return;
		
		// Check if there enough or too many potential spawns
		if(mouseButton != 1 && mouseButton != 2) return;
		if(mouseButton == 2 && potentialSpawns.size() <= 1) return;
		if(mouseButton == 1 && potentialSpawns.size() >= MAX_POTENTIAL_SPAWNS) return;
		
		// Find button you are hovering
		GuiButton button = null;
		for(GuiButton tempButton : gui.getButtonList())
			if(tempButton.id >= BUTTON_ID_START && tempButton.id <= BUTTON_ID_START + potentialSpawnsCount)
				if(tempButton.isMouseOver())
					button = tempButton;
		
		if(button != null) {
			// Remove old buttons
			for(GuiButton buttonToRemove : this.potentialSpawnButtons)
				gui.getButtonList().remove(buttonToRemove);
			
			if(mouseButton == 2) {
				potentialSpawns.remove(button.id - BUTTON_ID_START);
				
				PacketDispatcher.sendToServer(new PacketPotentialSpawnsRemove(button.id - BUTTON_ID_START));
				if(button.id - BUTTON_ID_START < FilterMobSpawnerBase.potentialSpawnIndex)
					FilterMobSpawnerBase.potentialSpawnIndex--;
			}
			else if(mouseButton == 1) {
				potentialSpawns.add(new WeightedSpawnerEntity());
				PacketDispatcher.sendToServer(new PacketPotentialSpawnsAdd(button.id - BUTTON_ID_START + 1));
				FilterMobSpawnerBase.potentialSpawnIndex = button.id - BUTTON_ID_START + 1;
			}
		
			// Readd buttons
			this.addPotentialSpawnButtons(gui, topX, topY);
			this.onPotentialSpawnChange(gui);
		}
	}
	
	@Override
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {
		if(SpawnerUtil.isSpawner(gui)) {
			List<WeightedSpawnerEntity> potentialSpawns = SpawnerUtil.getPotentialSpawns(gui);
			if(potentialSpawns == null) return;
				
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.id >= BUTTON_ID_START && tempButton.id <= BUTTON_ID_START + potentialSpawnsCount) {
					if(!tempButton.isMouseOver())
						continue;
						
					List<String> list = new ArrayList<>();
		    		list.add(SpawnerUtil.getMinecartType(potentialSpawns.get(tempButton.id - BUTTON_ID_START)).toString());
		    		list.add("NBT: ");
		    		list.add(SpawnerUtil.getMinecartProperties(potentialSpawns.get(tempButton.id - BUTTON_ID_START)).toString());
		    			
		    		gui.drawHoveringTooltip(list, xMouse, yMouse);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if(button.id >= BUTTON_ID_START && button.id <= BUTTON_ID_START + potentialSpawnsCount) {
			int newIndex = button.id - BUTTON_ID_START;
				
			if(newIndex != potentialSpawnIndex) {
				potentialSpawnIndex = newIndex;
				
				for(GuiButton tempButton : this.potentialSpawnButtons) {
					tempButton.enabled = tempButton.id - BUTTON_ID_START == potentialSpawnIndex;
					this.onPotentialSpawnChange(gui);
				}
			}
		}
	}
	
	public void onPotentialSpawnChange(IGuiFilter gui) {}
}
