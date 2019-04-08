package mapmakingtools.api.filter;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/**
 * @author ProPercivalalb
 */
public abstract class FilterClient extends FilterBase {
	
	public abstract String getUnlocalizedName();
	public String getFilterName() { return I18n.format(this.getUnlocalizedName()); }
	public abstract String getIconPath();
	
	public void initGui(IFilterGui gui) {}
	public void drawGuiContainerBackgroundLayer(IFilterGui gui, float partialTicks, int xMouse, int yMouse) {}
	public void drawGuiContainerForegroundLayer(IFilterGui gui, int xMouse, int yMouse) {}
	public void drawToolTips(IFilterGui gui, int xMouse, int yMouse) {}
	public void updateScreen(IFilterGui gui) {}
	public void mouseClicked(IFilterGui gui, double mouseX, double mouseY, int mouseButton) {}
	public void keyTyped(IFilterGui gui, char cha, int charIndex) {}
	public boolean doClosingKeysWork(IFilterGui gui, char cha, int charIndex) { return true; }
	public void actionPerformed(IFilterGui gui, GuiButton button) {}
	public boolean drawBackground(IFilterGui gui) { return false; }
	public List<String> getFilterInfo(IFilterGui gui) { return null; }
	public boolean hasUpdateButton(IFilterGui gui) { return false; }
	public void updateButtonClicked(IFilterGui gui) {}
	public boolean showErrorIcon(IFilterGui gui) { return false; }
	public String getErrorMessage(IFilterGui gui) { return null; }
	
	private FilterServer serverFilter;
	
	public final FilterClient setServerFilter(FilterServer serverFilter) {
		this.serverFilter = serverFilter;
		return this;
	}
	
	public final FilterServer getServerFilter() {
		return this.serverFilter;
	}
	
}
