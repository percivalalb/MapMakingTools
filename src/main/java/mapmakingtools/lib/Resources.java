package mapmakingtools.lib;

import mapmakingtools.util.Util;
import net.minecraft.util.ResourceLocation;

public class Resources {

    public static final ResourceLocation ITEM_EDITOR = Resources.getGuiResource("item_editor");
    public static final ResourceLocation ITEM_EDITOR_SLOT = Resources.getGuiResource("item_editor_slot");
    public static final ResourceLocation SCREEN_SCROLL = Resources.getGuiResource("gui_tab");
    public static final ResourceLocation BUTTON_TEXT_COLOR = Resources.getGuiResource("button_text_colour");

    public static ResourceLocation getGuiResource(String textureFileName) {
        return Util.getResource("textures/gui/" + textureFileName + ".png");
    }
}
