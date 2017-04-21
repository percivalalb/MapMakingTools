package mapmakingtools.tools.attribute;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class BookAttribute extends IItemAttribute {

	private GuiTextField fld_name;
	private GuiTextField fld_author;
	private String name;
	private String author;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.WRITTEN_BOOK;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(this.name != null && data == 0) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			stack.getTagCompound().setString("title", this.name);
		}
		
		if(this.author != null && data == 1) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			stack.getTagCompound().setString("author", this.author);
		}
		
		
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.book.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			if(stack.hasTagCompound()) {
				if(stack.getTagCompound().hasKey("title", 8)) {
					this.fld_name.setText(stack.getTagCompound().getString("title"));
				}
				if(stack.getTagCompound().hasKey("author", 8)) {
					this.fld_author.setText(stack.getTagCompound().getString("author"));
				}
			}
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		itemEditor.getFontRenderer().drawString("Title", x + 2, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Author", x + 86, y + 17, -1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 28, 80, 13);
		this.fld_author = new GuiTextField(1, itemEditor.getFontRenderer(), x + 86, y + 28, 80, 13);
		itemEditor.getTextBoxList().add(this.fld_name);
		itemEditor.getTextBoxList().add(this.fld_author);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(0);
		}
		if(textbox == this.fld_author) {
			this.author = this.fld_author.getText();
			itemEditor.sendUpdateToServer(1);
		}
	}
}
