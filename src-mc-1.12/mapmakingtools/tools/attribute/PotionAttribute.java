package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.datareader.PotionList;
import mapmakingtools.tools.item.nbt.PotionNBT;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

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
	private GuiButtonData btn_particles;
	private String level;
	private String duration;
	private boolean showParticles = true;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.POTIONITEM;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(!Strings.isNullOrEmpty(this.level) && !Strings.isNullOrEmpty(this.duration) && this.selected != -1 && data == 0) {
			if(NumberParse.isInteger(this.level)) {
				Potion potion = Potion.getPotionById(PotionList.getPotionId(PotionList.getCustomId(this.selected)));
				
				if(potion == null)
					return;
				
				if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
					List potionList = PotionUtils.getEffectsFromStack(stack);
					for(int i = 0; potionList != null && i < potionList.size(); ++i) {
						PotionEffect effect = (PotionEffect)potionList.get(i);
						PotionNBT.addPotionEffects(stack, Potion.getIdFromPotion(effect.getPotion()), effect.getAmplifier() + 1, effect.getDuration(), effect.getIsAmbient(), effect.doesShowParticles());
					}
				}
				int lvl = NumberParse.getInteger(this.level);
				int dur = NumberParse.getInteger(this.duration);
				PotionNBT.addPotionEffects(stack, Potion.getIdFromPotion(potion), lvl, dur, false, this.showParticles);
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
		List potionList = PotionUtils.getEffectsFromStack(stack);
		for(int i = 0; potionList != null && i < potionList.size(); ++i) {
			PotionEffect effect = (PotionEffect)potionList.get(i);
			list.add(String.format("%d ~~~ %d ~~~ %d ~~~ %b", Potion.getIdFromPotion(effect.getPotion()), effect.getAmplifier(), effect.getDuration(), effect.doesShowParticles()));
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
				Potion potion = Potion.getPotionById(PotionList.getPotionId(listStr));
				
				if(potion == null)
					return listStr;
				
				String unlocalised = potion.getName();
				String localised = I18n.translateToLocal(unlocalised);
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
				
				Potion potion = Potion.getPotionById(NumberParse.getInteger(split[0]));
				
				
				
				if(potion == null)
					return listStr;
				
				String localised = I18n.translateToLocal(potion.getName());
				int lvl = NumberParse.getInteger(split[1]);
				
				
				return localised + " " + (lvl + 1) + " (" + split[2] + " ticks,  Visable: " + split[3] + ")";
			}
			
		};
		
		this.fld_lvl = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_duration = new GuiTextField(1, itemEditor.getFontRenderer(), x + 59, y + height / 2 - 20, 50, 14);
		this.btn_particles = new GuiButtonData(3, x + 115, y + height / 2 - 23, 80, 20, "Has Particles");
		if(this.showParticles)
			this.btn_particles.displayString = "Has Particles";
		else 
			this.btn_particles.displayString = "No Particles";

		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiSmallButton(0, x + width - 20, y + height / 2 - 20, 16, 16, "+");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Effects");
		
		this.btn_remove.enabled = false;
		
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_remove_all);
		itemEditor.getTextBoxList().add(this.fld_lvl);
		itemEditor.getTextBoxList().add(this.fld_duration);
		itemEditor.getButtonList().add(this.btn_particles);
		this.scrollMenuAdd.initGui();
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(0);
		}
		else if(button.id == 1) {
			itemEditor.sendUpdateToServer(1);
		}
		else if(button.id == 2) {
			itemEditor.sendUpdateToServer(2);
		}
		else if(button.id == 3) {
			if(this.showParticles) {
				this.btn_particles.displayString = "No Particles";
				this.showParticles = false;
			}
			else {
				this.btn_particles.displayString = "Has Particles";
				this.showParticles = true;
			}
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
		if(this.fld_duration == textbox)
			this.duration = this.fld_duration.getText();
	}
}
