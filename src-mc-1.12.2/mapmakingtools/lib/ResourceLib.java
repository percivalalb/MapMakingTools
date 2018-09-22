package mapmakingtools.lib;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class ResourceLib {

	public static final ResourceLocation SCREEN_LARGE = getGuiResource("gui_tab_empty_large.png");
	public static final ResourceLocation SCREEN_MEDIUM = getGuiResource("gui_tab_empty.png");
	public static final ResourceLocation SCREEN_SMALL = getGuiResource("gui_small.png");
	public static final ResourceLocation SCREEN_MOB_ARMOUR = getGuiResource("mob_armor.png");
	public static final ResourceLocation SCREEN_ONE_SLOT = getGuiResource("one_slot.png");
	public static final ResourceLocation SCREEN_VILLAGER_SHOP = getGuiResource("villager_shop.png");

	public static final ResourceLocation SCREEN_SCROLL = getGuiResource("gui_tab.png");
	
	public static final ResourceLocation BUTTON_TEXT_COLOUR = getGuiResource("button_text_colour.png");

	public static final ResourceLocation TABS = getGuiResource("tab.png");

	public static final ResourceLocation ITEM_EDITOR = getGuiResource("item_editor.png");
	public static final ResourceLocation ITEM_EDITOR_SLOT = getGuiResource("item_editor_slot.png");
	
	public static final ResourceLocation WATCH_PLAYER = getGuiResource("watch_player.png");

	public static final ResourceLocation TRANSPARENT_BACKGROUND = getGuiResource("transparent_background.png");

	
	public static final ResourceLocation WORLD_TRANSFER = getGuiResource("world_transfer.png");

	public static final HashMap<String, ResourceLocation> TEXTURES_OTHER = new HashMap<String, ResourceLocation>();
	
    /**
     * Gets a local gui texture file path.
     * @param textureFileName The .png file that relates to the texture file. 
     * @return The whole path string including the given parameter.
     */
    public static ResourceLocation getGuiResource(String textureFileName) {
	    return get("textures/gui/" + textureFileName);
	}
    
    public static ResourceLocation getCachedGeneric(String pathIn) {
    	if(!TEXTURES_OTHER.containsKey(pathIn))
    		TEXTURES_OTHER.put(pathIn, new ResourceLocation(pathIn));
    	
    	return TEXTURES_OTHER.get(pathIn);
    }
    
    public static ResourceLocation getCached(String pathIn) {
    	if(!TEXTURES_OTHER.containsKey(pathIn))
    		TEXTURES_OTHER.put(pathIn, get(pathIn));
    	
    	return TEXTURES_OTHER.get(pathIn);
    }
    
    /**
     * @return ResourceLocation from mapmakingtools assets folder
     */
	private static ResourceLocation get(String pathIn) {
	    return new ResourceLocation(Reference.MOD_ID, pathIn);
	}
}
