package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.button.GuiButtonSmallData;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.datareader.PotionList;
import mapmakingtools.tools.item.nbt.NBTUtil;
import mapmakingtools.tools.item.nbt.PotionNBT;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

/**
 * @author ProPercivalalb
 */
public class PotionAttribute extends IItemAttribute {

	private ScrollMenu<Potion> scrollMenuAdd;
	private ScrollMenu<PotionEffect> scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private GuiTextField fld_lvl;
	private GuiTextField fld_duration;
	private GuiButtonSmallData btn_particles;
	private String level;
	private String duration;
	private boolean showParticles = true;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(!Strings.isNullOrEmpty(this.level) && !Strings.isNullOrEmpty(this.duration) && this.scrollMenuAdd.hasSelection() && data == 0) {
			if(Numbers.isInteger(this.level)) {
				Potion potion = scrollMenuAdd.getRecentSelection();
				
				if(potion == null)
					return;
				
				int lvl = Numbers.parse(this.level);
				int dur = Numbers.parse(this.duration);
				PotionNBT.addPotionEffects(stack, Potion.getIdFromPotion(potion), lvl, dur, false, this.showParticles);
			}
		}
		
		if(this.scrollMenuRemove.hasSelection() && data == 1) {
			boolean hasDefault = NBTUtil.hasTag(stack, "Potion", NBTUtil.ID_STRING);
			if(hasDefault && this.scrollMenuRemove.getRecentIndex() == 0)
				NBTUtil.remove(stack, "Potion", NBTUtil.ID_STRING);
			else
				NBTUtil.removeFromSubList(stack, PotionNBT.POTION_TAG, NBTUtil.ID_COMPOUND, this.scrollMenuRemove.getRecentIndex() - (hasDefault ? 1 : 0));

		    NBTUtil.hasEmptyTagCompound(stack, true);
		}
		
		if(data == 2) {
			NBTUtil.remove(stack, "Potion", NBTUtil.ID_STRING);
			NBTUtil.removeSubList(stack, PotionNBT.POTION_TAG);
			NBTUtil.hasEmptyTagCompound(stack, true);
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.potion.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.clearSelected();
		this.selectedDelete = -1;
		
		this.scrollMenuRemove.setElements(PotionUtils.getEffectsFromStack(stack));
		this.scrollMenuRemove.initGui();
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
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
		
		this.scrollMenuAdd = new ScrollMenu<Potion>((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, PotionList.getPotions()) {

			@Override
			public void onSetButton() {
				PotionAttribute.selected = this.getRecentIndex();
			}

			@Override
			public String getDisplayString(Potion potion) {
				String unlocalised = potion.getName();
				String localised = I18n.format(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? unlocalised : localised;
			}
			
		};
		this.scrollMenuRemove = new ScrollMenu<PotionEffect>((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1) {

			@Override
			public void onSetButton() {
				PotionAttribute.selectedDelete = this.getRecentIndex();
			}

			@Override
			public String getDisplayString(PotionEffect potionEffect) {
				Potion potion = potionEffect.getPotion();
				
				if(potion == null) return potionEffect.toString();
				
				String localised = I18n.format(potion.getName());
				int lvl = potionEffect.getAmplifier();
				int dur = potionEffect.getDuration();
				boolean vis = potionEffect.doesShowParticles();
				
				
				return localised + " " + (lvl + 1) + " (" + dur + " ticks,  Visable: " + vis + ")";
			}
			
		};
		
		this.fld_lvl = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + height / 2 - 20, 50, 14);
		this.fld_duration = new GuiTextField(1, itemEditor.getFontRenderer(), x + 59, y + height / 2 - 20, 50, 14);
		this.btn_particles = new GuiButtonSmallData(3, x + 115, y + height / 2 - 21, 80, 17, "Has Particles");
		if(this.showParticles)
			this.btn_particles.displayString = "Has Particles";
		else 
			this.btn_particles.displayString = "No Particles";

		this.fld_lvl.setMaxStringLength(5);
		this.btn_add = new GuiButtonSmall(0, x + width - 20, y + height / 2 - 20, 16, 16, "+");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			itemEditor.sendUpdateToServer(1);
    		}
    	};
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Effects") {
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
		itemEditor.addTextFieldToGui(this.fld_duration);
		itemEditor.addButtonToGui(this.btn_particles);
		itemEditor.addListenerToGui(this.scrollMenuAdd);
		itemEditor.addListenerToGui(this.scrollMenuRemove);
		this.scrollMenuAdd.initGui();
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(0);
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
	public void mouseClicked(IGuiItemEditor itemEditor, double mouseX, double mouseY, int mouseButton) {
		this.scrollMenuAdd.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuRemove.mouseClicked(mouseX, mouseY, mouseButton);
		
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(this.fld_lvl == textbox)
			this.level = this.fld_lvl.getText();
		if(this.fld_duration == textbox)
			this.duration = this.fld_duration.getText();
	}
}
