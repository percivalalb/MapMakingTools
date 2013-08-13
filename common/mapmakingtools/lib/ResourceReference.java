package mapmakingtools.lib;

import net.minecraft.util.ResourceLocation;
import mapmakingtools.core.helper.TextureHelper;

/**
 * @author ProPercivalalb
 */
public class ResourceReference {

	public static final ResourceLocation screenLarge = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("guiTabEmptyLarge.png"));
	public static final ResourceLocation screenMedium = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("guiTabEmpty.png"));
	public static final ResourceLocation screenSmall = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("guiSkull.png"));
	public static final ResourceLocation screenMobArmor = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("mobArmor.png"));
	public static final ResourceLocation screenOneSlot = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("oneSlot.png"));
	public static final ResourceLocation screenVillagerShop = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("villagerShop.png"));

	public static final ResourceLocation screenScroll = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("guiTab.png"));

	public static final ResourceLocation tabs = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("tab.png"));

	public static final ResourceLocation itemEditor = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("item_Editor.png"));

	public static final ResourceLocation transparentBackground = new ResourceLocation("mapmakingtools", TextureHelper.getGuiTexturePath("transparent_background.png"));
}
