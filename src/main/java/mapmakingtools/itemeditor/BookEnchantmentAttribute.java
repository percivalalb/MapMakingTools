package mapmakingtools.itemeditor;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BookEnchantmentAttribute extends EnchantmentAttribute {

    @Override
    public boolean isApplicable(Player player, Item item) {
        return item == Items.ENCHANTED_BOOK;
    }

    @Override
    public String getNBTName() {
        return "StoredEnchantments";
    }

    @Override
    public void addEnchantment(ItemStack stack, Enchantment ench, int level) {
        EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(ench, level));
    }
}
