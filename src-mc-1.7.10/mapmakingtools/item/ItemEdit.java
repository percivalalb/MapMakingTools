package mapmakingtools.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends Item {
	
	private IIcon wrenchIcon;
	
    public ItemEdit() {
    	this.setCreativeTab(CreativeTabs.tabMisc);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegistry) {
    	super.registerIcons(iconRegistry);
    	this.wrenchIcon = iconRegistry.registerIcon("mapmakingtools:wrench");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
    	return this.getIconIndex(stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
    	if(stack.getItemDamage() == 1) 
    		return this.wrenchIcon;
    	else
    		return Items.wooden_axe.getIconFromDamage(0);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    	par3List.add(new ItemStack(item, 1, 0));
    	par3List.add(new ItemStack(item, 1, 1));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debug) {
    	
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(stack.getItemDamage() == 1) 
    		return StatCollector.translateToLocal("item.mapmakingtools.wrench.name");
    	else
    		return StatCollector.translateToLocal("item.mapmakingtools.edit_item.name");
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if(stack != null && stack.getItemDamage() == 0)
        	return true;
        
        return false;
    }
}
