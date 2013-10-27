package mapmakingtools.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.util.DataStorage;
import mapmakingtools.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends net.minecraft.item.ItemAxe {
	
	private Icon wrenchIcon;
	
    public ItemEdit() {
        super(15, EnumToolMaterial.WOOD);
        this.setTextureName("wood_axe");
        this.setUnlocalizedName("hatchetWood");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegistry) {
    	super.registerIcons(iconRegistry);
    	this.wrenchIcon = iconRegistry.registerIcon("mapmakingtools:wrench");
    	FilterManager.registerIcons(iconRegistry);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(ItemStack stack, int pass) {
    	return this.getIconIndex(stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconIndex(ItemStack stack) {
    	if(isWrench(stack))
    		return this.wrenchIcon;
    	else
    		return super.getIconIndex(stack);
    }
    
    public static boolean isWrench(ItemStack stack) {
    	if(stack != null && stack.hasTagCompound()) {
    		NBTTagCompound tag = stack.stackTagCompound;
    		return tag.getBoolean("WRENCH");
    	}
    	return false;
    }
    
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{ getCreativeTab(), CreativeTabs.tabRedstone};
    }
    
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    	if(par2CreativeTabs == this.getCreativeTabs()[0]) {
    		par3List.add(new ItemStack(par1, 1, 0));
    	}
    	else if(par2CreativeTabs == this.getCreativeTabs()[1]) {
    		ItemStack stack = new ItemStack(par1, 1, 0);
    		stack.stackTagCompound = new NBTTagCompound();
    		stack.stackTagCompound.setBoolean("WRENCH", true);
    		stack.stackTagCompound.setTag("AttributeModifiers", new NBTTagList());
    		par3List.add(stack);
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debug) {
    	
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(isWrench(stack)) 
    		return StatCollector.translateToLocal("item.mapmaking.wrench.name");
    	else
    		return StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".name");
    }
    
    @Override
    public String getItemDisplayName(ItemStack stack) {
    	return this.getItemStackDisplayName(stack).trim();
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
    	if((player.capabilities.isCreativeMode && Constants.QUICK_BUILD_ITEM == Item.axeWood.itemID) && itemstack != null && itemstack.itemID == Item.axeWood.itemID && !this.isWrench(itemstack)) {
    		return true;
    	}
        return false;
    }
}
