package mapmakingtools.tools.attribute;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonTick;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class TooltipFlagsAttribute extends IItemAttribute {

	private GuiButtonTick btn_ench;
	private GuiButtonTick btn_modif;
	private GuiButtonTick btn_unbreak;
	private GuiButtonTick btn_destroy;
	private GuiButtonTick btn_place;
	private GuiButtonTick btn_normal;
	private GuiButtonTick btn_all;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}
	
	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data == 0) {
			int flag = 0;
			
			if(!btn_ench.isTicked())
				flag |= 1 << 0; //Enchantment Info
			if(!btn_modif.isTicked())
				flag |= 1 << 1; //Modifier Info
			if(!btn_unbreak.isTicked())
				flag |= 1 << 2; //Unbreakable Info
			if(!btn_destroy.isTicked())
				flag |= 1 << 3; //Block Destroy Info
			if(!btn_place.isTicked())
				flag |= 1 << 4; //Can Place On Info
			if(!btn_normal.isTicked())
				flag |= 1 << 5; //Normal Info

			if(flag == 0) {
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
			        stack.getTagCompound().removeTag("HideFlags");
					this.btn_all.setTicked(true);
			        
			        if(stack.getTagCompound().hasNoTags())
			        	stack.setTagCompound(null);
				}
				return;
			}
			
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
				
			stack.getTagCompound().setInteger("HideFlags", flag);
			this.btn_all.setTicked(false);
			
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.tooltipflags.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
				int flag = stack.getTagCompound().getInteger("HideFlags");
				
				if((flag & (1 << 0)) == (1 << 0))
					this.btn_ench.setTicked(false);
				if((flag & (1 << 1)) == (1 << 1))
					this.btn_modif.setTicked(false);
				if((flag & (1 << 2)) == (1 << 2))
					this.btn_unbreak.setTicked(false);
				if((flag & (1 << 3)) == (1 << 3))
					this.btn_destroy.setTicked(false);
				if((flag & (1 << 4)) == (1 << 4))
					this.btn_place.setTicked(false);
				if((flag & (1 << 5)) == (1 << 5))
					this.btn_normal.setTicked(false);
				
				if(flag != ~flag)
					this.btn_all.setTicked(false);
			}
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString("Enchantment", x + 6, y + 25, 10526880);
		itemEditor.getFontRenderer().drawString("Attribute Modifiers", x + 6, y + 47, 10526880);
		itemEditor.getFontRenderer().drawString("Unbreakable", x + 6, y + 69, 10526880);
		itemEditor.getFontRenderer().drawString("Block Destroy", x + 6, y + 91, 10526880);
		itemEditor.getFontRenderer().drawString("Can Place On", x + 6, y + 113, 10526880);
		itemEditor.getFontRenderer().drawString("Normal Info", x + 6, y + 135, 10526880);
		itemEditor.getFontRenderer().drawString("Show all Information", x + 6, y + height - 22, 16777120);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		//this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
	    this.btn_ench = new GuiButtonTick(0, x + 102, y + 16, true);
	    this.btn_modif = new GuiButtonTick(1, x + 102, y + 38, true);
	    this.btn_unbreak = new GuiButtonTick(2, x + 102, y + 60, true);
	    this.btn_destroy = new GuiButtonTick(3, x + 102, y + 82, true);
	    this.btn_place = new GuiButtonTick(4, x + 102, y + 104, true);
	    this.btn_normal = new GuiButtonTick(5, x + 102, y + 126, true);
	    this.btn_all = new GuiButtonTick(6, x + 112, y + height - 29, true);
	    
		itemEditor.getButtonList().add(this.btn_ench);
		itemEditor.getButtonList().add(this.btn_modif);
		itemEditor.getButtonList().add(this.btn_unbreak);
		itemEditor.getButtonList().add(this.btn_destroy);
		itemEditor.getButtonList().add(this.btn_place);
		itemEditor.getButtonList().add(this.btn_normal);
		itemEditor.getButtonList().add(this.btn_all);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id >= 0 && button.id <= 5) {
			((GuiButtonTick)button).setTicked(!((GuiButtonTick)button).isTicked());
			itemEditor.sendUpdateToServer(0);
		}
		if(button.id == 6) {
			((GuiButtonTick)button).setTicked(!((GuiButtonTick)button).isTicked());
		    this.btn_ench.setTicked(((GuiButtonTick)button).isTicked());
		    this.btn_modif.setTicked(((GuiButtonTick)button).isTicked());
		    this.btn_unbreak.setTicked(((GuiButtonTick)button).isTicked());
		    this.btn_destroy.setTicked(((GuiButtonTick)button).isTicked());
		    this.btn_place.setTicked(((GuiButtonTick)button).isTicked());
		    this.btn_normal.setTicked(((GuiButtonTick)button).isTicked());
			itemEditor.sendUpdateToServer(0);
		}
	}
}
