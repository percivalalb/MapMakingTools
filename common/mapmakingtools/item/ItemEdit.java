package mapmakingtools.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mapmakingtools.ModItems;
import mapmakingtools.api.FilterManager;
import mapmakingtools.lib.Constants;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends ItemAxe {
	
	private IIcon wrenchIcon;
	
    public ItemEdit() {
        super(ToolMaterial.WOOD);
        this.setTextureName("wood_axe");
        this.setUnlocalizedName("hatchetWood");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegistry) {
    	super.registerIcons(iconRegistry);
    	this.wrenchIcon = iconRegistry.registerIcon("mapmakingtools:wrench");
    	FilterManager.registerIcons(iconRegistry);
    	FilterManager.errorIcon = iconRegistry.registerIcon("mapmakingtools:error");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
    	return this.getIconIndex(stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
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
    
    public static boolean isAxe(ItemStack stack) {
    	if(stack != null)
    		return Item.func_150891_b(stack.getItem()) == Item.func_150891_b(ModItems.editItem);
    	return false;
    }
    
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{ getCreativeTab(), CreativeTabs.tabRedstone};
    }
    
    @Override
    public void func_150895_a(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    	if(par2CreativeTabs == this.getCreativeTabs()[0])
    		par3List.add(new ItemStack(item, 1, 0));
    	
    	else if(par2CreativeTabs == this.getCreativeTabs()[1]) {
    		ItemStack stack = new ItemStack(item, 1, 0);
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
    		return StatCollector.translateToLocal("item.mapmakingtools.wrench.name");
    	else
    		return StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".name");
    }
    
   // @Override
    public String getItemDisplayName(ItemStack stack) {
    	return this.getItemStackDisplayName(stack).trim();
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
    	if((PlayerAccess.canEdit(player) && Constants.QUICK_BUILD_ITEM == Item.func_150891_b(Items.wooden_axe)) && itemstack != null && !ItemEdit.isWrench(itemstack)) {
    		return true;
    	}
        return false;
    }
    
    @Override
    public void setDamage(ItemStack stack, int damage) {
    	if(!ItemEdit.isWrench(stack))
    		super.setDamage(stack, damage);
    }
}
