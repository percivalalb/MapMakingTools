package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.datareader.PotionList;
import mapmakingtools.tools.item.nbt.PotionNBT;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import com.google.common.base.Strings;

/**
 * @author ProPercivalalb
 */
public class PotionAttribute extends IItemAttribute {

	private ScrollMenu scrollMenuAdd;
	private ScrollMenu scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private GuiTextField fld_lvl;
	private GuiTextField fld_duration;
	private String level;
	private String duration;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.potionitem;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(!Strings.isNullOrEmpty(this.level) && !Strings.isNullOrEmpty(this.duration) && this.selected != -1 && data == 0) {
			if(NumberParse.isInteger(this.level)) {
				Potion potion = Potion.potionTypes[PotionList.getPotionId(PotionList.getCustomId(this.selected))];
				
				if(potion == null)
					return;
				
				if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
					List potionList = ((ItemPotion)Items.potionitem).getEffects(stack.getItemDamage());
					for(int i = 0; potionList != null && i < potionList.size(); ++i) {
						PotionEffect effect = (PotionEffect)potionList.get(i);
						PotionNBT.addPotionEffects(stack, effect.getPotionID(), effect.getAmplifier() + 1, effect.getDuration(), effect.getIsAmbient());
					}
				}
				int lvl = NumberParse.getInteger(this.level);
				int dur = NumberParse.getInteger(this.duration);
				PotionNBT.addPotionEffects(stack, potion.id, lvl, dur, false);
			}
		}
		
		if(this.selectedDelete != -1 && data == 1) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
		        NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);
		        nbttaglist.removeTag(this.selectedDelete);
			}
			else {
				PotionNBT.checkHasTag(stack);
			}
		}
		
		if(data == 2) {
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
				
			if(!stack.getTagCompound().hasKey("CustomPotionEffects", 9))
				stack.getTagCompound().setTag("CustomPotionEffects", new NBTTagList());
				
			NBTTagList list = stack.getTagCompound().getTagList("CustomPotionEffects", 10);
			while(list.tagCount() > 0)
				list.removeTag(0);
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.potion.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.selected = -1;
		this.selectedDelete = -1;
		
		List<String> list = new ArrayList<String>();
		List potionList = ((ItemPotion)Items.potionitem).getEffects(stack);
		for(int i = 0; potionList != null && i < potionList.size(); ++i) {
			PotionEffect effect = (PotionEffect)potionList.get(i);
			list.add(String.format("%d ~~~ %d", effect.getPotionID(), effect.getAmplifier()));
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
		
		if(Strings.isNullOrEmpty(this.fld_duration.getText()) && !this.fld_duration.isFocused()) {
			itemEditor.getFontRenderer().drawString("Duration", x + 63, y + height / 2 - 17, 13882323);
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {
		this.scrollMenuAdd.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuRemove.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.level = null;
		this.duration = null;
		this.selectedDelete = -1;
		
		this.scrollMenuAdd = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, PotionList.getPotions()) {

			@Override
			public void onSetButton() {
				PotionAttribute.selected = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				Potion potion = Potion.potionTypes[PotionList.getPotionId(listStr)];
				
				if(potion == null)
					return listStr;
				
				String unlocalised = potion.getName();
				String localised = StatCollector.translateToLocal(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}
			
		};
		this.scrollMenuRemove = new ScrollMenu((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1, new ArrayList<String>()) {

			@Override
			public void onSetButton() {
				PotionAttribute.selectedDelete = this.selected;
			}

			@Override
			public String getDisplayString(String listStr) {
				String[] split = listStr.split(" ~~~ ");
				
				Potion potion = Potion.potionTypes[NumberParse.getInteger(split[0])];
				
				
				
				if(potion == null)
					return listStr;
				
				String localised = StatCollector.translateToLocal(potion.getName());
				int lvl = NumberParse.getInteger(split[1]);
				
				
				return localised + " " + (lvl + 1);
			}
			
		};
		
		this.fld_lvl = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_duration = new GuiTextField(1, itemEditor.getFontRenderer(), x + 59, y + height / 2 - 20, 50, 14);
		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiButton(0, x + 115, y + height / 2 - 23, 50, 20, "Add");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Effects");
		
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_remove_all);
		itemEditor.getTextBoxList().add(this.fld_lvl);
		itemEditor.getTextBoxList().add(this.fld_duration);
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
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(this.fld_lvl == textbox)
			this.level = this.fld_lvl.getText();
		if(this.fld_duration == textbox)
			this.duration = this.fld_duration.getText();
	}
}
