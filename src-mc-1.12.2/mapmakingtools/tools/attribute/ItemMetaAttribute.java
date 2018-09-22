package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class ItemMetaAttribute extends IItemAttribute {

	private GuiTextField fld_meta;
	private GuiButton btn_unbreakable;
	private String meta;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0:
			if(!Numbers.isInteger(this.meta)) break;
			
			stack.setItemDamage(Numbers.parse(this.meta));
			
			break;
		case 1:
			NBTTagCompound tagCompound = NBTUtil.getOrCreateTagCompound(stack);
			if(!tagCompound.hasKey("Unbreakable", NBTUtil.ID_BOOLEAN) || !tagCompound.getBoolean("Unbreakable"))
				stack.getTagCompound().setBoolean("Unbreakable", true);
			else {
				tagCompound.removeTag("Unbreakable");
				
				NBTUtil.hasEmptyTagCompound(stack, true);
			}
				
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.metadata.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			this.fld_meta.setText(String.valueOf(stack.getItemDamage()));
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_meta = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		this.fld_meta.setMaxStringLength(5);
		this.btn_unbreakable = new GuiSmallButton(0, x + 2, y + 30, 120, 20, "Toggle Unbreakable");
		itemEditor.getTextBoxList().add(this.fld_meta);
		itemEditor.getButtonList().add(this.btn_unbreakable);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_meta) {
			this.meta = this.fld_meta.getText();
			itemEditor.sendUpdateToServer(0);
		}
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(1);
		}
	}
}
