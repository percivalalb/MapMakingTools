package mapmakingtools.api.interfaces;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public abstract class IItemAttribute {

	public abstract boolean isApplicable(EntityPlayer player, ItemStack stack);
	public abstract void onItemCreation(ItemStack stack, int data);
	public abstract String getUnlocalizedName();
	public String getAttributeName() { return StatCollector.translateToLocal(this.getUnlocalizedName()); }
	public abstract void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first);
	public abstract void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height);
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {}
	public void drawGuiContainerForegroundLayer(IGuiItemEditor itemEditor, int xMouse, int yMouse) {}
	public abstract void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height);
	public void updateScreen(IGuiItemEditor itemEditor) {}
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {}
	public void keyTyped(IGuiItemEditor itemEditor, char character, int keyId) {}
	public void mouseClicked(IGuiItemEditor itemEditor, int xMouse, int yMouse, int mouseButton) {}
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {}
}
