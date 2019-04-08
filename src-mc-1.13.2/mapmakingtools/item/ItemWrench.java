package mapmakingtools.item;

import java.util.List;

import javax.annotation.Nullable;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author ProPercivalalb
 */
public class ItemWrench extends Item {
	
    public ItemWrench() {
    	super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
    }
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
    		items.add(new ItemStack(this, 1));
    		items.add(new ItemStack(Blocks.COMMAND_BLOCK));
    		items.add(new ItemStack(Blocks.SPAWNER));
    		items.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
    		items.add(new ItemStack(Items.KNOWLEDGE_BOOK));
    		items.add(new ItemStack(Items.FIREWORK_ROCKET));
    	}
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
       return PlayerAccess.canEdit(player);
    }
}
