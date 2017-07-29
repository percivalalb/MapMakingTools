package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;
import net.minecraft.client.gui.GuiButton;
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
	private GuiTextField fld_generation;
	private GuiButton btn_convertback;
	private String name;
	private String author;
	private String generation;
	
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
		
		else if(this.author != null && data == 1) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			stack.getTagCompound().setString("author", this.author);
		}
		else if(this.generation != null && data == 2) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			stack.getTagCompound().setInteger("generation", NumberParse.getInteger(this.generation));
		}
		else if(data == 3) {
			ItemStack book = new ItemStack(Items.WRITABLE_BOOK, stack.getCount(), stack.getItemDamage());
			
			if(stack.getTagCompound().hasNoTags())
				stack.setTagCompound(null);
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
				if(stack.getTagCompound().hasKey("generation", 99)) {
					this.fld_generation.setText("" + stack.getTagCompound().getInteger("generation"));
				}
			}
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		itemEditor.getFontRenderer().drawString("Title", x + 2, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Author", x + 86, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Generation", x + 170, y + 17, -1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 28, 80, 13);
		this.fld_author = new GuiTextField(1, itemEditor.getFontRenderer(), x + 86, y + 28, 80, 13);
		this.fld_generation = new GuiTextField(1, itemEditor.getFontRenderer(), x + 170, y + 28, 80, 13);
		this.btn_convertback = new GuiButton(0, x + 2, y + 48, 200, 20, "Convert back to writable book");
		itemEditor.getTextBoxList().add(this.fld_name);
		itemEditor.getTextBoxList().add(this.fld_author);
		itemEditor.getTextBoxList().add(this.fld_generation);
		//itemEditor.getButtonList().add(this.btn_convertback);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(3);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(0);
		}
		else if(textbox == this.fld_author) {
			this.author = this.fld_author.getText();
			itemEditor.sendUpdateToServer(1);
		}
		else if(textbox == this.fld_generation) {
			this.generation = this.fld_generation.getText();
			itemEditor.sendUpdateToServer(2);
		}
	}
}
