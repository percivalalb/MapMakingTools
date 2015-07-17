package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.NumberParse;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class LoreAttribute extends IItemAttribute {

	private GuiButton btn_add;
	private GuiButton btn_remove;
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
		if(first) {
		//	this.fld_meta.setText(String.valueOf(stack.getItemDamage()));
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
	    this.btn_add = new GuiSmallButton(2, x + 2, y + 30, 13, 12, "+");
	    this.btn_remove = new GuiSmallButton(3, x + 2, y + 16, 13, 12, "-");
	    itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		
		for(int i = 0; i < 2; ++i) {
			GuiTextField textField = new GuiTextField(i, itemEditor.getFontRenderer(), x + 17, y + 54  + i * 21, 100, 15);
			itemEditor.getTextBoxList().add(textField);
		}
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox.func_175206_d() == 0) {
			
			itemEditor.sendUpdateToServer(-1);
		}
	}
}
