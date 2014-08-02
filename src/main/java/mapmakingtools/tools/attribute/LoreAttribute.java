package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;

/**
 * @author ProPercivalalb
 */
public class LoreAttribute extends IItemAttribute {

	private GuiTextField fld_meta;
	private String meta;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(Strings.isNullOrEmpty(this.meta))
			return;
		
		if(NumberParse.isInteger(this.meta))
			stack.setItemDamage(NumberParse.getInteger(this.meta));
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.lore.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			this.fld_meta.setText(String.valueOf(stack.getItemDamage()));
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		this.fld_meta = new GuiTextField(itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		this.fld_meta.setMaxStringLength(5);
		itemEditor.getTextBoxList().add(this.fld_meta);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_meta) {
			this.meta = this.fld_meta.getText();
			itemEditor.sendUpdateToServer(-1);
		}
	}
}
