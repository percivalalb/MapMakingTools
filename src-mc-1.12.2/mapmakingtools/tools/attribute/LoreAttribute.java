package mapmakingtools.tools.attribute;

import java.util.ArrayList;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * @author ProPercivalalb
 */
public class LoreAttribute extends IItemAttribute {

	private int x;
	private int y;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private String meta;
	private ArrayList<GuiTextField> loreFields = new ArrayList<GuiTextField>();
	private ArrayList<GuiButton> minusButtons = new ArrayList<GuiButton>();
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}
	
	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data == 0) {
			if(this.loreFields.size() <= 0) {
				if(stack.hasTagCompound()) {
					if(stack.getTagCompound().hasKey("display", 10)) {
						NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
						display.removeTag("Lore");
						if(display.hasNoTags())
							stack.getTagCompound().removeTag("display");
						if(stack.getTagCompound().hasNoTags())
							stack.setTagCompound(null);
					}
				}
			}
			else {
				if(!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
				
				if(!stack.getTagCompound().hasKey("display", 10))
					stack.getTagCompound().setTag("display", new NBTTagCompound());
				
				NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
				NBTTagList list = new NBTTagList();
				for(GuiTextField field : this.loreFields)
					list.appendTag(new NBTTagString(field.getText()));
				
				display.setTag("Lore", list);
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.lore.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			this.loreFields.clear();
			this.minusButtons.clear();
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
				if(stack.getTagCompound().getCompoundTag("display").getTagId("Lore") == 9) {
					NBTTagList list = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
					for(int l = 0; l < list.tagCount(); ++l) {
						GuiTextField textField = new GuiTextField(l, itemEditor.getFontRenderer(), this.x + 17, this.y + 54 + l * 21, 100, 15);
						textField.setMaxStringLength(1000);
						textField.setText(list.getStringTagAt(l));
						this.loreFields.add(textField);
						itemEditor.getTextBoxList().add(textField);
						
						GuiButtonSmall minusButton = new GuiButtonSmall(l, this.x + 2, this.y + 54 + l * 21, 13, 12, "-");
						this.minusButtons.add(minusButton);
						itemEditor.getButtonList().add(minusButton);
                    }
				}
			}
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
	    this.btn_add = new GuiButtonSmall(-1, x + 2, y + 30, 13, 12, "+");
	    this.btn_remove = new GuiButtonSmall(3, x + 2, y + 16, 13, 12, "-");
	    itemEditor.getButtonList().add(this.btn_add);
		//itemEditor.getButtonList().add(this.btn_remove);
		
		this.x = x;
		this.y = y;
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id >= 0 && button.id <= this.minusButtons.size()) {
			if(itemEditor.getButtonList().contains(button))
				itemEditor.getButtonList().remove(button);
			if(itemEditor.getTextBoxList().contains(this.loreFields.get(button.id)))
				itemEditor.getTextBoxList().remove(this.loreFields.get(button.id));
			this.minusButtons.remove(button.id);
			this.loreFields.remove(button.id);
			for(int i = button.id; i < this.minusButtons.size(); i++) {
				this.minusButtons.get(i).y -= 21;
				this.minusButtons.get(i).id -= 1;
				this.loreFields.get(i).y -= 21;
			}
			itemEditor.sendUpdateToServer(0);
		}
		else if(button.id == -1) {
			GuiTextField textField = new GuiTextField(this.minusButtons.size(), itemEditor.getFontRenderer(), this.x + 17, this.y + 54 + this.minusButtons.size() * 21, 100, 15);
			textField.setMaxStringLength(1000);
			this.loreFields.add(textField);
			itemEditor.getTextBoxList().add(textField);
			
			GuiButtonSmall minusButton = new GuiButtonSmall(this.minusButtons.size(), this.x + 2, this.y + 54 + this.minusButtons.size() * 21, 13, 12, "-");
			this.minusButtons.add(minusButton);
			itemEditor.getButtonList().add(minusButton);
			
			itemEditor.sendUpdateToServer(0);
		}
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox.getId() >= 0 && textbox.getId() <= this.loreFields.size()) {
			
			itemEditor.sendUpdateToServer(0);
		}
	}
}
