package mapmakingtools.api.interfaces;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterClient extends IFilter {
	
	public abstract String getUnlocalizedName();
	public String getFilterName() { return I18n.translateToLocal(this.getUnlocalizedName()); }
	public abstract String getIconPath();
	
	public void initGui(IGuiFilter gui) {}
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {}
	public void drawGuiContainerForegroundLayer(IGuiFilter gui, int xMouse, int yMouse) {}
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {}
	public void updateScreen(IGuiFilter gui) {}
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {}
	public void keyTyped(IGuiFilter gui, char cha, int charIndex) {}
	public boolean doClosingKeysWork(IGuiFilter gui, char cha, int charIndex) { return true; }
	public void actionPerformed(IGuiFilter gui, GuiButton button) {}
	public boolean drawBackground(IGuiFilter gui) { return false; }
	public List<String> getFilterInfo(IGuiFilter gui) { return null; }
	public boolean hasUpdateButton(IGuiFilter gui) { return false; }
	public void updateButtonClicked(IGuiFilter gui) {}
	public boolean showErrorIcon(IGuiFilter gui) { return false; }
	public String getErrorMessage(IGuiFilter gui) { return null; }
	
	private IFilterServer serverFilter;
	
	public final IFilterClient setServerFilter(IFilterServer serverFilter) {
		this.serverFilter = serverFilter;
		return this;
	}
	
	public final IFilterServer getServerFilter() {
		return this.serverFilter;
	}
	
}
