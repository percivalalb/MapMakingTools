package mapmakingtools.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class SlotFakeArmor extends SlotFake {

	public EntityPlayer player;
	public EntityEquipmentSlot armorType;
	
    public SlotFakeArmor(EntityPlayer playerIn, IInventory inventoryIn, int index, int xPosition, int yPosition, EntityEquipmentSlot armorType) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = playerIn;
        this.armorType = armorType;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
    	return stack.getItem().isValidArmor(stack, this.armorType, this.player);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return ItemArmor.EMPTY_SLOT_NAMES[this.armorType.getIndex()];
    }
}
