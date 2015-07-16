package mapmakingtools.lib;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class ResourceReference {

	public static final ResourceLocation screenLarge = new ResourceLocation("mapmakingtools", getGuiTexturePath("guiTabEmptyLarge.png"));
	public static final ResourceLocation screenMedium = new ResourceLocation("mapmakingtools", getGuiTexturePath("guiTabEmpty.png"));
	public static final ResourceLocation screenSmall = new ResourceLocation("mapmakingtools", getGuiTexturePath("guiSmall.png"));
	public static final ResourceLocation screenMobArmor = new ResourceLocation("mapmakingtools", getGuiTexturePath("mobArmor.png"));
	public static final ResourceLocation screenOneSlot = new ResourceLocation("mapmakingtools", getGuiTexturePath("oneSlot.png"));
	public static final ResourceLocation screenVillagerShop = new ResourceLocation("mapmakingtools", getGuiTexturePath("villagerShop.png"));

	public static final ResourceLocation screenScroll = new ResourceLocation("mapmakingtools", getGuiTexturePath("guiTab.png"));
	
	public static final ResourceLocation buttonTextColour = new ResourceLocation("mapmakingtools", getGuiTexturePath("buttonTextColour.png"));

	public static final ResourceLocation tabs = new ResourceLocation("mapmakingtools", getGuiTexturePath("tab.png"));

	public static final ResourceLocation itemEditor = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_Editor.png"));
	public static final ResourceLocation itemEditorSlot = new ResourceLocation("mapmakingtools", getGuiTexturePath("item_Editor_slot.png"));
	
	public static final ResourceLocation watchPlayer = new ResourceLocation("mapmakingtools", getGuiTexturePath("watchPlayer.png"));

	public static final ResourceLocation transparentBackground = new ResourceLocation("mapmakingtools", getGuiTexturePath("transparent_background.png"));

	
	public static final ResourceLocation worldTransfer = new ResourceLocation("mapmakingtools", getGuiTexturePath("worldtransfer.png"));

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
