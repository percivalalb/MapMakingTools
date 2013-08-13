package mapmakingtools.item;

import mapmakingtools.core.helper.MathHelper;
import mapmakingtools.core.util.DataStorage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemAxe extends net.minecraft.item.ItemAxe {
	
    public ItemAxe() {
        super(15, EnumToolMaterial.WOOD);
        this.func_111206_d("wood_axe");
        this.setUnlocalizedName("hatchetWood");
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
    	if(player.capabilities.isCreativeMode && itemstack != null && itemstack.itemID == Item.axeWood.itemID) {
    		return true;
    	}
        return false;
    }
}
