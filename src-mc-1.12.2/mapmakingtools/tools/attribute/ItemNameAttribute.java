package mapmakingtools.tools.attribute;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * @author ProPercivalalb
 */
public class ItemNameAttribute extends IItemAttribute {

	private GuiTextField fld_name;
	private GuiTextField fld_translatable;
	private GuiButton btn_remove;
	private String name;
	private String translatable;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0:
			if(this.name == null) break;
			
			stack.setStackDisplayName(TextFormatting.RESET + this.name);
			break;
		case 1:
			if(this.translatable == null) break;
			
			stack.setTranslatableName(this.translatable);
			break;
		case 2:
			stack.clearCustomName();
			
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.displayname.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		
		String displayname = stack.getDisplayName();
		if(displayname.startsWith(TextFormatting.RESET.toString())) 
			displayname = displayname.substring(2, displayname.length());
		this.fld_name.setText(displayname);
		
		this.btn_remove.enabled = stack.hasDisplayName();// || ;
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, width - 4, 13);
		this.fld_name.setMaxStringLength(64);
		this.fld_translatable = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 30, width - 4, 13);
		this.fld_translatable.setMaxStringLength(64);
		this.fld_translatable.setEnabled(false);
		this.btn_remove = new GuiButton(0, x + width / 2 - 100, y + 65, 200, 20, "Remove custom display name tag");
		
		itemEditor.getTextBoxList().add(this.fld_name);
		itemEditor.getTextBoxList().add(this.fld_translatable);
		itemEditor.getButtonList().add(this.btn_remove);
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(2);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(0);
		}
		else if(textbox == this.fld_translatable) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(1);
		}
			
	}
}
