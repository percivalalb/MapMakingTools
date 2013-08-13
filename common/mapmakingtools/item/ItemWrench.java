package mapmakingtools.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mapmakingtools.api.FilterRegistry;
import mapmakingtools.core.helper.LogHelper;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemWrench extends Item {

	public ItemWrench(int par1) {
		super(par1);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegistry) {
    	this.itemIcon = iconRegistry.registerIcon("mapmakingtools:wrench");
    	FilterRegistry.registerIcons(iconRegistry);
    	LogHelper.logDebug("Register Icons");
    }

}
