package mapmakingtools.item;

import java.util.List;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class ItemEdit extends Item {
	
    public ItemEdit() {
    	this.setCreativeTab(CreativeTabs.MISC);
    	this.setMaxStackSize(1);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    	if(this.isInCreativeTab(tab)) {
    		items.add(new ItemStack(this, 1, 0));
    		items.add(new ItemStack(this, 1, 1));
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
    	if(stack.getMetadata() == 1) 
    		return I18n.translateToLocal("item.mapmakingtools.wrench.name");
    	else
    		return I18n.translateToLocal("item.mapmakingtools.edit_item.name");
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if(stack != null && stack.getMetadata() == 0)
        	return true;
        
        return false;
    }
}
