package mapmakingtools.tools.attribute;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author ProPercivalalb
 */
public class ItemNameAttribute extends IItemAttribute {

	private GuiTextField fld_name;
	private GuiButton btn_remove;
	private String name;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(this.name != null && data == 0)
			stack.setStackDisplayName(EnumChatFormatting.RESET + this.name);
		if(data == 1) {
			if(stack.hasTagCompound()) {
				if(stack.getTagCompound().hasKey("display", 10)) {
					NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
					display.removeTag("Name");
					if(display.hasNoTags())
						stack.getTagCompound().removeTag("display");
					if(stack.getTagCompound().hasNoTags())
						stack.setTagCompound(null);
					this.fld_name.setText(stack.getDisplayName());
				}
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.displayname.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			String displayname = stack.getDisplayName();
			if(displayname.startsWith(EnumChatFormatting.RESET.toString())) 
				displayname = displayname.substring(2, displayname.length());
			this.fld_name.setText(displayname);
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(itemEditor.getFontRenderer(), x + 2, y + 15, width - 4, 13);
		this.fld_name.setMaxStringLength(64);
		this.btn_remove = new GuiButton(0, x + width / 2 - 100, y + 40, 200, 20, "Remove custom display name tag");
		
		itemEditor.getTextBoxList().add(this.fld_name);
		itemEditor.getButtonList().add(this.btn_remove);
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(1);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(0);
		}
	}
}
