package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import scala.actors.threadpool.Arrays;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.datareader.EnchantmentList;

/**
 * @author ProPercivalalb
 */
public class EnchantmentAttribute extends IItemAttribute {

	private ScrollMenu scrollMenuAdd;
	private ScrollMenu scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_add_effect;
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
		if(this.level != null && this.selected != -1 && data == 0) {
			if(NumberParse.isInteger(this.level)) {
				Enchantment enchantment = Enchantment.enchantmentsList[EnchantmentList.getEnchantmentId(EnchantmentList.getCustomId(this.selected))];
				
				if(enchantment == null)
					return;
				
				
				stack.addEnchantment(enchantment, NumberParse.getInteger(this.level));
			}
		}
		
		if(this.selectedDelete != -1 && data == 1) {
			if(stack.hasTagCompound() && stack.stackTagCompound.hasKey("ench", 9)) {
		        NBTTagList nbttaglist = stack.stackTagCompound.getTagList("ench", 10);
		        nbttaglist.removeTag(this.selectedDelete);
		        if(nbttaglist.tagCount() == 0)
		        	stack.stackTagCompound.removeTag("ench");
			}
		}
		
		if(data == 2) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			
			if(!stack.stackTagCompound.hasKey("ench", 9))
				stack.stackTagCompound.setTag("ench", new NBTTagList());
		}
		
		if(data == 3) {
			if(stack.hasTagCompound())
				if(stack.stackTagCompound.hasKey("ench", 9))
					stack.stackTagCompound.removeTag("ench");
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.enchantment.name";
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
		NBTTagList enchantmentList = stack.stackTagCompound.getTagList("ench", 10);
		for(int i = 0; i < enchantmentList.tagCount(); ++i) {
			NBTTagCompound t = enchantmentList.getCompoundTagAt(i);
			list.add(String.format("%d ~~~ %d", t.getShort("id"), t.getShort("lvl")));
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
	public void initGui(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		this.scrollMenuAdd = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, EnchantmentList.getEnchantments()) {

			@Override
			public void onSetButton() {
				EnchantmentAttribute.selected = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				Enchantment enchantment = Enchantment.enchantmentsList[EnchantmentList.getEnchantmentId(listStr)];
				
				if(enchantment == null)
					return listStr;
				
				String unlocalised = enchantment.getName();
				String localised = StatCollector.translateToLocal(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}
			
		};
		this.scrollMenuRemove = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1, new ArrayList<String>()) {

			@Override
			public void onSetButton() {
				EnchantmentAttribute.selectedDelete = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				String[] split = listStr.split(" ~~~ ");
				
				Enchantment enchantment = Enchantment.enchantmentsList[NumberParse.getInteger(split[0])];
				
				if(enchantment == null)
					return listStr;
				
				String localised = enchantment.getTranslatedName(NumberParse.getInteger(split[1]));
				
				return localised;
			}
			
		};
		
		this.fld_lvl = new GuiTextField(itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiButton(0, x + 60, y + height / 2 - 23, 50, 20, "Add");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_add_effect = new GuiButton(2, x + 130, y + height / 2 - 23, 120, 20, "Add Glimering Effect");
		this.btn_remove_all = new GuiButton(3, x + 130, y + height - 23, 130, 20, "Remove all Enchantments");
		
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_add_effect);
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
		if(button.id == 3) {
			itemEditor.sendUpdateToServer(3);
		}
	}
	
	@Override
	public void mouseClicked(IGuiItemEditor itemEditor, int xMouse, int yMouse, int mouseButton) {
		this.scrollMenuAdd.mouseClicked(xMouse, yMouse, mouseButton);
		this.scrollMenuRemove.mouseClicked(xMouse, yMouse, mouseButton);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(this.fld_lvl == textbox)
			this.level = this.fld_lvl.getText();
	}
}
