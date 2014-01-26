package mapmakingtools.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotFakeArmor extends Slot implements IPhantomSlot
{
	public boolean isUnlimited = false;
	
    /**
     * The armor type that can be placed on that slot, it uses the same values of armorType field on ItemArmor.
     */
    final int armorType;

    /**
     * The parent class of this clot, ContainerPlayer, SlotArmor is a Anon inner class.
     */
    final EntityPlayer parent;

    public SlotFakeArmor(EntityPlayer par1EntityPlayer, IInventory par2IInventory, int par3, int par4, int par5, int par6) {
        super(par2IInventory, par3, par4, par5);
        this.parent = par1EntityPlayer;
        this.armorType = par6;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        Item item = (par1ItemStack == null ? null : par1ItemStack.getItem());
        return item != null && item.isValidArmor(par1ItemStack, armorType, parent);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the icon index on items.png that is used as background image of the slot.
     */
    public IIcon getBackgroundIconIndex()
    {
        return ItemArmor.func_94602_b(this.armorType);
    }

	@Override
	public boolean canAdjust() {
		return true;
	}
	
	@Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }
	
	@Override
	public void setIsUnlimited(boolean isUnlimited) {
		if(this.inventory instanceof IUnlimitedInventory)
			((IUnlimitedInventory)this.inventory).setSlotUnlimited(this.getSlotIndex(), isUnlimited);
		this.isUnlimited = isUnlimited;
	}

	@Override
	public boolean isUnlimited() {
		if(this.inventory instanceof IUnlimitedInventory)
			return ((IUnlimitedInventory)this.inventory).isSlotUnlimited(this.getSlotIndex());
		return this.isUnlimited;
	}
}
