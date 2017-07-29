package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.datareader.EnchantmentList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;;

/**
 * @author ProPercivalalb
 */
public class BookEnchantmentAttribute extends IItemAttribute {

	private ScrollMenu scrollMenuAdd;
	private ScrollMenu scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private GuiTextField fld_lvl;
	private String level;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.ENCHANTED_BOOK;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(this.level != null && this.selected != -1 && data == 0) {
			if(NumberParse.isInteger(this.level)) {
				Enchantment enchantment = Enchantment.getEnchantmentByID(EnchantmentList.getEnchantmentId(EnchantmentList.getCustomId(this.selected)));
				
				if(enchantment == null)
					return;
				
				((ItemEnchantedBook)Items.ENCHANTED_BOOK).addEnchantment(stack, new EnchantmentData(enchantment, NumberParse.getInteger(this.level)));
			}
		}
		
		if(this.selectedDelete != -1 && data == 1) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEnchantments", 9)) {
		        NBTTagList nbttaglist = stack.getTagCompound().getTagList("StoredEnchantments", 10);
		        nbttaglist.removeTag(this.selectedDelete);
		        if(nbttaglist.hasNoTags()) {
		        	stack.getTagCompound().removeTag("StoredEnchantments");
		        	if(stack.getTagCompound().hasNoTags())
						stack.setTagCompound(null);
		        }
			}
		}
		
		if(data == 2) {
			if(stack.hasTagCompound()) {
				if(stack.getTagCompound().hasKey("StoredEnchantments", 9)) {
					stack.getTagCompound().removeTag("StoredEnchantments");
					if(stack.getTagCompound().hasNoTags())
						stack.setTagCompound(null);
				}
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.bookenchantment.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.selected = -1;
		this.selectedDelete = -1;
		
		if(!stack.hasTagCompound()) {
			this.scrollMenuRemove.strRefrence = new ArrayList<String>();
			this.scrollMenuRemove.initGui();
			return;
		}
		
		List<String> list = new ArrayList<String>();
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("StoredEnchantments")) {
			NBTTagList enchantmentList = stack.getTagCompound().getTagList("StoredEnchantments", 10);
			for(int i = 0; i < enchantmentList.tagCount(); ++i) {
				NBTTagCompound t = enchantmentList.getCompoundTagAt(i);
				list.add(String.format("%d ~~~ %d", t.getShort("id"), t.getShort("lvl")));
			}
		}
		this.scrollMenuRemove.strRefrence = list;
		this.scrollMenuRemove.initGui();
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		if(Strings.isNullOrEmpty(this.fld_lvl.getText()) && !this.fld_lvl.isFocused()) {
			itemEditor.getFontRenderer().drawString("Level", x + 6, y + height / 2 - 17, 13882323);
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {
		this.scrollMenuAdd.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuRemove.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.scrollMenuAdd = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, EnchantmentList.getEnchantments()) {

			@Override
			public void onSetButton() {
				BookEnchantmentAttribute.selected = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				Enchantment enchantment = Enchantment.getEnchantmentByID(EnchantmentList.getEnchantmentId(listStr));
				
				if(enchantment == null)
					return listStr;
				
				String unlocalised = enchantment.getName();
				String localised = I18n.translateToLocal(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}
			
		};
		this.scrollMenuRemove = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1, new ArrayList<String>()) {

			@Override
			public void onSetButton() {
				BookEnchantmentAttribute.selectedDelete = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				String[] split = listStr.split(" ~~~ ");
				
				Enchantment enchantment = Enchantment.getEnchantmentByID(NumberParse.getInteger(split[0]));
				
				if(enchantment == null)
					return listStr;
				
				String localised = enchantment.getTranslatedName(NumberParse.getInteger(split[1]));
				
				return localised;
			}
			
		};
		
		this.fld_lvl = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiButton(0, x + 60, y + height / 2 - 23, 50, 20, "Add");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Enchantments");
		
		this.btn_remove.enabled = false;
		
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_remove_all);
		itemEditor.getTextBoxList().add(this.fld_lvl);
		this.scrollMenuAdd.initGui();
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(0);
		}
		if(button.id == 1) {
			itemEditor.sendUpdateToServer(1);
		}
		if(button.id == 2) {
			itemEditor.sendUpdateToServer(2);
		}
	}
	
	@Override
	public void mouseClicked(IGuiItemEditor itemEditor, int xMouse, int yMouse, int mouseButton) {
		this.scrollMenuAdd.mouseClicked(xMouse, yMouse, mouseButton);
		this.scrollMenuRemove.mouseClicked(xMouse, yMouse, mouseButton);
		
		this.btn_remove.enabled = this.scrollMenuRemove.selected != -1;
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(this.fld_lvl == textbox)
			this.level = this.fld_lvl.getText();
	}
}
