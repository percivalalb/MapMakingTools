package mapmakingtools.tools.attribute;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonSmall;
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
		case 1:
			NBTTagCompound tagCompound = NBTUtil.getOrCreateTagCompound(stack);
			if(!tagCompound.contains("Unbreakable", NBTUtil.ID_BYTE) || !tagCompound.getBoolean("Unbreakable"))
				stack.getTag().putBoolean("Unbreakable", true);
			else {
				tagCompound.remove("Unbreakable");
				
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
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_meta = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		this.fld_meta.setMaxStringLength(5);
		this.btn_unbreakable = new GuiButtonSmall(0, x + 2, y + 30, 120, 20, "Toggle Unbreakable");
		itemEditor.addTextFieldToGui(this.fld_meta);
		itemEditor.addButtonToGui(this.btn_unbreakable);
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

	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack itemstackIn, boolean first) {
		// TODO Auto-generated method stub
		
	}
}
