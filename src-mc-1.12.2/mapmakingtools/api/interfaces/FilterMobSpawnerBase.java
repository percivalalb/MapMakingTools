package mapmakingtools.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonPotentialSpawns;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsAdd;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsRemove;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public abstract class FilterMobSpawnerBase extends FilterClient {

	private static final int POTENTIAL_SPAWNS_BUTTON_ID_START = 200;
	private static final int MAX_POTENTIAL_SPAWNS = 17;
	
	public static int minecartIndex = 0;
	public int minecartsCount = 1;
	public List<GuiButton> minecartButtons = new ArrayList<GuiButton>();
	
	public void addMinecartButtons(IGuiFilter gui, int topX, int topY) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
		
		if(minecarts == null)
			return;

		this.minecartsCount = minecarts.size();
		this.minecartButtons.clear();
		
		if(this.minecartsCount <= minecartIndex) {
			minecartIndex = minecarts.size() - 1;
			this.onMinecartIndexChange(gui);
		}
			
		for(int i = 0; i < minecarts.size(); ++i) {
			GuiButtonPotentialSpawns button = new GuiButtonPotentialSpawns(POTENTIAL_SPAWNS_BUTTON_ID_START + i, topX + 14 * i + 2, topY - 13, 13, 12, "" + i);
			button.enabled = i == minecartIndex;
			this.minecartButtons.add(button);
			gui.getButtonList().add(button);
		}
	}
	
	public void removeMinecartButtons(IGuiFilter gui, int xMouse, int yMouse, int mouseButton, int topX, int topY) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
		
		// Check if there enough or too many potential spawns
		if(mouseButton != 1 && mouseButton != 2) return;
		if(mouseButton == 2 && minecarts.size() <= 1) return;
		if(mouseButton == 1 && minecarts.size() >= MAX_POTENTIAL_SPAWNS) return;
		
		// Find button you are hovering
		GuiButton button = null;
		for(GuiButton tempButton : gui.getButtonList())
			if(tempButton.id >= POTENTIAL_SPAWNS_BUTTON_ID_START && tempButton.id <= POTENTIAL_SPAWNS_BUTTON_ID_START + minecartsCount)
				if(tempButton.isMouseOver())
					button = tempButton;
		
		if(button != null) {
			// Remove old buttons
			for(GuiButton buttonToRemove : this.minecartButtons)
				gui.getButtonList().remove(buttonToRemove);
			
			if(mouseButton == 2) {
				minecarts.remove(button.id - POTENTIAL_SPAWNS_BUTTON_ID_START);
				
				PacketDispatcher.sendToServer(new PacketPotentialSpawnsRemove(gui.getBlockPos(), button.id - POTENTIAL_SPAWNS_BUTTON_ID_START));
				if(button.id - POTENTIAL_SPAWNS_BUTTON_ID_START < FilterMobSpawnerBase.minecartIndex)
					FilterMobSpawnerBase.minecartIndex--;
			}
			else if(mouseButton == 1) {
				minecarts.add(new WeightedSpawnerEntity());
				PacketDispatcher.sendToServer(new PacketPotentialSpawnsAdd(gui.getBlockPos(), button.id - POTENTIAL_SPAWNS_BUTTON_ID_START + 1));
				FilterMobSpawnerBase.minecartIndex = button.id - POTENTIAL_SPAWNS_BUTTON_ID_START + 1;
			}
		
			// Readd buttons
			this.addMinecartButtons(gui, topX, topY);
		}
	}
	
	@Override
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getPotentialSpawns(spawner.getSpawnerBaseLogic());
		
		for(GuiButton tempButton : gui.getButtonList()) {
			if(tempButton.id >= POTENTIAL_SPAWNS_BUTTON_ID_START && tempButton.id <= POTENTIAL_SPAWNS_BUTTON_ID_START + minecartsCount) {
				if(!tempButton.isMouseOver())
					continue;
				List<String> list = new ArrayList<String>();
    			list.add(SpawnerUtil.getMinecartType(minecarts.get(tempButton.id - POTENTIAL_SPAWNS_BUTTON_ID_START)).toString());
    			//list.add("NBT: ");
    			//list.add(SpawnerUtil.getMinecartProperties(minecarts.get(tempButton.id - 200)).toString());
    			
    			gui.drawHoveringTooltip(list, xMouse, yMouse);
			}
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if(button.id >= POTENTIAL_SPAWNS_BUTTON_ID_START && button.id <= POTENTIAL_SPAWNS_BUTTON_ID_START + minecartsCount) {
			minecartIndex = button.id - POTENTIAL_SPAWNS_BUTTON_ID_START;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.id >= POTENTIAL_SPAWNS_BUTTON_ID_START && tempButton.id <= POTENTIAL_SPAWNS_BUTTON_ID_START + minecartsCount) {
					if(tempButton.id - POTENTIAL_SPAWNS_BUTTON_ID_START != minecartIndex)
						tempButton.enabled = false;
					else {
						tempButton.enabled = true;
						this.onMinecartIndexChange(gui);
					}
				}
			}
		}
	}
	
	public void onMinecartIndexChange(IGuiFilter gui) {}
}
