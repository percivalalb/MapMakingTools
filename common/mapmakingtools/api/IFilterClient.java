package mapmakingtools.api;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterClient extends IFilter {
	
	public IIcon icon;
	
	public abstract String getUnlocalizedName();
	public String getFilterName() { return StatCollector.translateToLocal(this.getUnlocalizedName()); }
	public IIcon getDisplayIcon() { return this.icon; }
	public void registerIcons(IIconRegister iconRegistry) { this.icon = iconRegistry.registerIcon(this.getIconPath()); };
	public abstract String getIconPath();
	
	public void initGui(IGuiFilter gui) {}
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {}
	public void drawGuiContainerForegroundLayer(IGuiFilter gui, int xMouse, int yMouse) {}
	public void updateScreen(IGuiFilter gui) {}
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {}
	public void keyTyped(IGuiFilter gui, char cha, int charIndex) {}
	public boolean doClosingKeysWork(IGuiFilter gui, char cha, int charIndex) { return true; }
	public void actionPerformed(IGuiFilter gui, GuiButton button) {}
	public boolean drawBackground(IGuiFilter gui) { return false; }
	public List<String> getFilterInfo() { return null; }
	public boolean hasUpdateButton() { return false; }
	public void updateButtonClicked() {}
	
	private IFilterServer serverFilter;
	
	public final IFilterClient setServerFilter(IFilterServer serverFilter) {
		this.serverFilter = serverFilter;
		return this;
	}
	
	public final IFilterServer getServerFilter() {
		return this.serverFilter;
	}
}
