package mapmakingtools.core.addon;

import java.util.List;

import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiItemEditor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;

public class NEIGuiHandler implements INEIGuiHandler {

	@Override
	public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) { 
		if(gui instanceof GuiFilterMenu || gui instanceof GuiItemEditor) {
			currentVisibility.showNEI = false;
		}
		return currentVisibility;
	}

	@Override
	public int getItemSpawnSlot(GuiContainer gui, ItemStack item) {
		return -1;
	}

	@Override
	public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
		return null;
	}

	@Override
	public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
		return false;
	}

}
