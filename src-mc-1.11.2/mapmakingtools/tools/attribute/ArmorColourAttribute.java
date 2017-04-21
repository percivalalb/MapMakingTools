package mapmakingtools.tools.attribute;

import java.util.Arrays;
import java.util.List;

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
public class ArmorColourAttribute extends IItemAttribute {

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
		if(!Strings.isNullOrEmpty(this.colourint) && data == 0) {
		
			if(NumberParse.isInteger(this.colourint)) {
				if(!stack.hasTagCompound())
					stack.setTagCompound(new NBTTagCompound());
			
				if(!stack.getTagCompound().hasKey("display", 10))
					stack.getTagCompound().setTag("display", new NBTTagCompound());
			
				NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");
				nbttagcompound.setInteger("color", NumberParse.getInteger(this.colourint));
			}
		}
		
		if(data == 1) {
			if(stack.hasTagCompound())
				if(stack.getTagCompound().hasKey("display", 10))
					if(stack.getTagCompound().getCompoundTag("display").hasKey("color", 3)) {
						stack.getTagCompound().getCompoundTag("display").removeTag("color");
						if(stack.getTagCompound().getCompoundTag("display").hasNoTags()) {
							stack.getTagCompound().removeTag("display");
							if(stack.getTagCompound().hasNoTags())
								stack.setTagCompound(null);
						}
						
					}
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
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		itemEditor.getFontRenderer().drawString("Integer", x + 2, y + 17, -1);
		itemEditor.getFontRenderer().drawString("Hexadecimal", x + 86, y + 17, -1);
	}

	private List<Character> list = Arrays.asList(new Character[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F'});
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_colourint = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 28, 80, 13);
		this.fld_colourhex = new GuiTextField(1, itemEditor.getFontRenderer(), x + 86, y + 28, 80, 13) {
		    @Override
			public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
		    	if(list.contains(p_146201_1_) || 14 == p_146201_2_ || 203 == p_146201_2_ || 205 == p_146201_2_)
		    		return super.textboxKeyTyped(p_146201_1_, p_146201_2_);
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
			
			if(!Strings.isNullOrEmpty(this.colourint) && NumberParse.isInteger(this.colourint)) {
				int integer = NumberParse.getInteger(this.colourint);
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
