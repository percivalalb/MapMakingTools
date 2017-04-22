package mapmakingtools.lib;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class ResourceReference {

	public static final ResourceLocation screenLarge = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab_empty_large.png"));
	public static final ResourceLocation screenMedium = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab_empty.png"));
	public static final ResourceLocation screenSmall = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_small.png"));
	public static final ResourceLocation screenMobArmor = new ResourceLocation("mapmakingtools", getGuiTexturePath("mob_armor.png"));
	public static final ResourceLocation screenOneSlot = new ResourceLocation("mapmakingtools", getGuiTexturePath("one_slot.png"));
	public static final ResourceLocation screenVillagerShop = new ResourceLocation("mapmakingtools", getGuiTexturePath("villager_shop.png"));

	public static final ResourceLocation screenScroll = new ResourceLocation("mapmakingtools", getGuiTexturePath("gui_tab.png"));
	
	public static final ResourceLocation buttonTextColour = new ResourceLocation("mapmakingtools", getGuiTexturePath("button_text_colour.png"));

	public static final ResourceLocation tabs = new ResourceLocation("mapmakingtools", getGuiTexturePath("tab.png"));

	public static final ResourceLocation itemEditor = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_editor.png"));
	public static final ResourceLocation itemEditorSlot = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_editor_slot.png"));
	
	public static final ResourceLocation watchPlayer = new ResourceLocation("mapmakingtools", getGuiTexturePath("watch_player.png"));

	public static final ResourceLocation transparentBackground = new ResourceLocation("mapmakingtools", getGuiTexturePath("transparent_background.png"));

	
	public static final ResourceLocation worldTransfer = new ResourceLocation("mapmakingtools", getGuiTexturePath("world_transfer.png"));

	public static final HashMap<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();
	
    /**
     * Gets a local gui texture file path.
     * @param textureFileName The .png file that relates to the texture file. 
     * @return The whole path string including the given parameter.
     */
    public static String getGuiTexturePath(String textureFileName) {
	    return String.format("%s/gui/%s", new Object[] {getOverrideTexturesPath(), textureFileName});
	}
    
    public static ResourceLocation getTexture(String texturePath) {
    	
    	if(!textures.containsKey(texturePath))
    		textures.put(texturePath, new ResourceLocation(texturePath));
    	
    	return textures.get(texturePath);
    }
    
	
    /**
     * Gets the location of the mods textures.
     * @return The default texture local
     */
	private static String getOverrideTexturesPath() {
	    return "textures";
	}
}
