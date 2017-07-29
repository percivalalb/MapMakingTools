package mapmakingtools.lib;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class ResourceReference {

	public static final ResourceLocation SCREEN_LARGE = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab_empty_large.png"));
	public static final ResourceLocation SCREEN_MEDIUM = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab_empty.png"));
	public static final ResourceLocation SCREEN_SMALL = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_small.png"));
	public static final ResourceLocation SCREEN_MOB_ARMOUR = new ResourceLocation("mapmakingtools", getGuiTexturePath("mob_armor.png"));
	public static final ResourceLocation SCREEN_ONE_SLOT = new ResourceLocation("mapmakingtools", getGuiTexturePath("one_slot.png"));
	public static final ResourceLocation SCREEN_VILLAGER_SHOP = new ResourceLocation("mapmakingtools", getGuiTexturePath("villager_shop.png"));

	public static final ResourceLocation SCREEN_SCROLL = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab.png"));
	
	public static final ResourceLocation BUTTON_TEXT_COLOUR = new ResourceLocation("mapmakingtools", getGuiTexturePath("button_text_colour.png"));

	public static final ResourceLocation TABS = new ResourceLocation("mapmakingtools", getGuiTexturePath("tab.png"));

	public static final ResourceLocation ITEM_EDITOR = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_editor.png"));
	public static final ResourceLocation ITEM_EDITOR_SLOT = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_editor_slot.png"));
	
	public static final ResourceLocation WATCH_PLAYER = new ResourceLocation("mapmakingtools", getGuiTexturePath("watch_player.png"));

	public static final ResourceLocation TRANSPARENT_BACKGROUND = new ResourceLocation("mapmakingtools", getGuiTexturePath("transparent_background.png"));

	
	public static final ResourceLocation WORLD_TRANSFER = new ResourceLocation("mapmakingtools", getGuiTexturePath("world_transfer.png"));

	public static final HashMap<String, ResourceLocation> TEXTURES_OTHER = new HashMap<String, ResourceLocation>();
	
    /**
     * Gets a local gui texture file path.
     * @param textureFileName The .png file that relates to the texture file. 
     * @return The whole path string including the given parameter.
     */
    public static String getGuiTexturePath(String textureFileName) {
	    return String.format("%s/gui/%s", new Object[] {getOverrideTexturesPath(), textureFileName});
	}
    
    public static ResourceLocation getTexture(String texturePath) {
    	
    	if(!TEXTURES_OTHER.containsKey(texturePath))
    		TEXTURES_OTHER.put(texturePath, new ResourceLocation(texturePath));
    	
    	return TEXTURES_OTHER.get(texturePath);
    }
    
	
    /**
     * Gets the location of the mods textures.
     * @return The default texture local
     */
	private static String getOverrideTexturesPath() {
	    return "textures";
	}
}
