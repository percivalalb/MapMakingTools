package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class PlayerHeadAttribute extends IItemAttribute {

	private GuiTextField fld_name;
	private GuiButton btn_remove;
	private String name;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.SKELETON_SKULL;//TODO && stack.getItemDamage() == 3;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		SkullNBT.setSkullName(stack, this.name);
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.playerhead.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			this.name = SkullNBT.getSkullName(stack);
			this.fld_name.setText(this.name);
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		this.btn_remove = new GuiButton(0, x + 2, y + 32, 80, 20, "Set Skin") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			itemEditor.sendUpdateToServer(-1);
    		}
    	};
		this.btn_remove.enabled = !Strings.isNullOrEmpty(this.name);
		itemEditor.addTextFieldToGui(this.fld_name);
		itemEditor.addButtonToGui(this.btn_remove);
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			this.btn_remove.enabled = !Strings.isNullOrEmpty(this.name);
		}
	}
}
