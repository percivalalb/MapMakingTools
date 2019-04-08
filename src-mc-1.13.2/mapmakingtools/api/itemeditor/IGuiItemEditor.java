package mapmakingtools.api.itemeditor;

import mapmakingtools.client.gui.IGuiElementHandler;
import net.minecraft.client.gui.FontRenderer;

/**
 * @author ProPercivalalb
 */
public interface IGuiItemEditor extends IGuiElementHandler {

	public FontRenderer getFontRenderer();
	public void sendUpdateToServer(int data);
}
