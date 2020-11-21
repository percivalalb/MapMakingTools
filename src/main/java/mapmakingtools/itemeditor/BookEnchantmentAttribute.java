package mapmakingtools.itemeditor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BookEnchantmentAttribute extends EnchantmentAttribute {

    @Override
    public boolean isApplicable(PlayerEntity player, Item item) {
        return item == Items.ENCHANTED_BOOK;
    }

    @Override
    public String getNBTName() {
        return "StoredEnchantments";
    }

    @Override
    public void addEnchantment(ItemStack stack, Enchantment ench, int level) {
        EnchantedBookItem.addEnchantment(stack, new EnchantmentData(ench, level));
    }
}
