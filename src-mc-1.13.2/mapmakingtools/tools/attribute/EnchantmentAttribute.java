package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.datareader.EnchantmentList;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class EnchantmentAttribute extends IItemAttribute {

	private ScrollMenu<String> scrollMenuAdd;
	private ScrollMenu<String> scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private GuiTextField fld_lvl;
	private String level;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0:
			if(!this.scrollMenuAdd.hasSelection() || !Numbers.isInteger(this.level)) break;
			
			Enchantment enchantment = Enchantment.getEnchantmentByID(EnchantmentList.getEnchantmentId(EnchantmentList.getCustomId(this.selected)));
			
			if(enchantment == null) break;
			
			stack.addEnchantment(enchantment, Numbers.parse(this.level));
			break;
		case 1:
			if(!this.scrollMenuRemove.hasSelection()) break;
			
			NBTUtil.removeFromSubList(stack, "ench", NBTUtil.ID_COMPOUND, this.selectedDelete);
			break;
		case 2:
			NBTUtil.removeSubList(stack, "ench");
			NBTUtil.hasEmptyTagCompound(stack, true);
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.enchantment.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.clearSelected();
		this.selectedDelete = -1;
		
		List<String> list = new ArrayList<String>();
		if(NBTUtil.hasTag(stack, "ench", NBTUtil.ID_LIST)) {
			NBTTagList enchantmentList = stack.getTag().getList("ench", 10);
			for(int i = 0; i < enchantmentList.size(); ++i) {
				NBTTagCompound t = enchantmentList.getCompound(i);
				list.add(String.format("%d ~~~ %d", t.getShort("id"), t.getShort("lvl")));
			}
		}
		this.scrollMenuRemove.setElements(list);
		this.scrollMenuRemove.initGui();
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
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
		this.scrollMenuAdd = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, EnchantmentList.getEnchantments()) {

			@Override
			public void onSetButton() {
				EnchantmentAttribute.selected = this.getRecentIndex();
			}

			@Override
			public String getDisplayString(String listStr) {
				Enchantment enchantment = Enchantment.getEnchantmentByID(EnchantmentList.getEnchantmentId(listStr));
				
				if(enchantment == null)
					return listStr;
				
				String unlocalised = enchantment.getName();
				String localised = I18n.format(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}
			
		};
		
		this.scrollMenuRemove = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1) {

			@Override
			public void onSetButton() {
				EnchantmentAttribute.selectedDelete = this.getRecentIndex();
			}

			@Override
			public String getDisplayString(String listStr) {
				String[] split = listStr.split(" ~~~ ");
				
				Enchantment enchantment = Enchantment.getEnchantmentByID(Numbers.parse(split[0]));
				
				if(enchantment == null)
					return listStr;
				
				String localised = enchantment.getDisplayName(Numbers.parse(split[1])).getUnformattedComponentText();
				
				return localised;
			}
			
		};
		
		this.fld_lvl = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiButton(0, x + 60, y + height / 2 - 23, 50, 20, "Add") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(0);
	    	}
		};
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(1);
	    	}
		};
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Enchantments") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(2);
	    	}
		};
		
		this.btn_remove.enabled = false;
		
		itemEditor.addButtonToGui(this.btn_add);
		itemEditor.addButtonToGui(this.btn_remove);
		itemEditor.addButtonToGui(this.btn_remove_all);
		itemEditor.addTextFieldToGui(this.fld_lvl);
		itemEditor.addListenerToGui(this.scrollMenuAdd);
		itemEditor.addListenerToGui(this.scrollMenuRemove);
		this.scrollMenuAdd.initGui();
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void mouseClicked(IGuiItemEditor itemEditor, double mouseX, double mouseY, int mouseButton) {
		this.scrollMenuAdd.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuRemove.mouseClicked(mouseX, mouseY, mouseButton);
		
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(this.fld_lvl == textbox)
			this.level = this.fld_lvl.getText();
	}
}
