package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.helper.ArrayUtil;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class ArmourColourAttribute extends IItemAttribute {

	private GuiTextField fld_colourint;
	private GuiTextField fld_colourhex;
	private GuiButton btn_remove;
	private String colourint;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.LEATHER_BOOTS || stack.getItem() == Items.LEATHER_CHESTPLATE || stack.getItem() == Items.LEATHER_HELMET || stack.getItem() == Items.LEATHER_LEGGINGS;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0:
			if(!Numbers.isInteger(this.colourint)) break;
			
			NBTTagCompound nbttagcompound = stack.getOrCreateSubCompound("display");
			nbttagcompound.setInteger("color", Numbers.parse(this.colourint));
			
			break;
		case 1:
			NBTUtil.removeTagFromSubCompound(stack, "display", NBTUtil.ID_INTEGER, "color");
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.armorcolour.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			if(stack.hasTagCompound())
				if(stack.getTagCompound().hasKey("display", 10))
					if(stack.getTagCompound().getCompoundTag("display").hasKey("color", 3)) {
						int integer = stack.getTagCompound().getCompoundTag("display").getInteger("color");
						this.fld_colourint.setText(String.valueOf(integer));
						this.fld_colourhex.setText(Integer.toHexString(integer));
					}
		if(!stack.hasTagCompound() || (!stack.getTagCompound().hasKey("display", 10) || !stack.getTagCompound().getCompoundTag("display").hasKey("color", 3))) {
			this.fld_colourint.setText("");
			this.fld_colourhex.setText("");
	    }
		
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString("Integer", x + 2, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Hexadecimal", x + 86, y + 17, -1);
	}

	private char[] HEX_CHARACTERS = "0123456789abcdefABCDEF".toCharArray();
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_colourint = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 28, 80, 13);
		this.fld_colourhex = new GuiTextField(1, itemEditor.getFontRenderer(), x + 86, y + 28, 80, 13) {
		    @Override
			public boolean textboxKeyTyped(char typedChar, int keyCode) {
		    	
		    	if(ArrayUtil.contains(HEX_CHARACTERS, typedChar) || 14 == keyCode || 203 == keyCode || 205 == keyCode)
		    		return super.textboxKeyTyped(typedChar, keyCode);
		    	return false;
		    }
		};
		this.btn_remove = new GuiButton(0, x + 2, y + 45, 80, 20, "Remove Colour");
		this.fld_colourhex.setMaxStringLength(6);
		this.fld_colourint.setMaxStringLength(8);
		itemEditor.getTextBoxList().add(this.fld_colourint);
		itemEditor.getTextBoxList().add(this.fld_colourhex);
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
		if(textbox == this.fld_colourint) {
			this.colourint = this.fld_colourint.getText();
			
			if(!Strings.isNullOrEmpty(this.colourint) && Numbers.isInteger(this.colourint)) {
				int integer = Numbers.parse(this.colourint);
				if(integer > 16777215) {
					integer = 16777215;
					this.fld_colourint.setText(String.valueOf(integer));
					this.colourint = this.fld_colourint.getText();
				}
				else if(integer < 0) {
					integer = 0;
					this.fld_colourint.setText(String.valueOf(integer));
					this.colourint = this.fld_colourint.getText();
				}
				
				
				this.fld_colourhex.setText(Integer.toHexString(integer));
			}
			
			itemEditor.sendUpdateToServer(0);
		}
		if(textbox == this.fld_colourhex) {
			String colourhex = this.fld_colourhex.getText();
			if(!Strings.isNullOrEmpty(colourhex)) {
				this.colourint = String.valueOf(Integer.valueOf(colourhex, 16));
				this.fld_colourint.setText(this.colourint);
			}
			itemEditor.sendUpdateToServer(0);
		}
	}
}
