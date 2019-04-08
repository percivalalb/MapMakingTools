package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.button.GuiButtonSmallData;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFireworkStar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author ProPercivalalb
 */
public class FireworksAttribute extends IItemAttribute {
	
	private GuiTextField fld_flight;
	private ScrollMenu<Byte> scrollMenuType;
	private ScrollMenu<NBTTagCompound> scrollMenuRemove;
	private ScrollMenu<Integer> scrollMenuColours;
	private ScrollMenu<Integer> scrollMenuColoursFade;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private GuiButtonSmallData btn_trail;
	private GuiButtonSmallData btn_flicker;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.FIREWORK_ROCKET || stack.getItem() == Items.FIREWORK_STAR;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data == 0) {
			if(Numbers.isByte(this.fld_flight.getText())) {
				NBTTagCompound fireworkNBT = NBTUtil.getOrCreateChildTag(stack, "Fireworks");
				fireworkNBT.putByte("Flight", (byte)Numbers.parse(this.fld_flight.getText()));
			}
		}
		else if (data == 1) {
			if(!this.scrollMenuType.hasSelection()) return;
			
			NBTTagCompound fireworkNBT = NBTUtil.getOrCreateChildTag(stack, "Fireworks");
			
			if(!fireworkNBT.contains("Explosions", NBTUtil.ID_LIST))
				fireworkNBT.put("Explosions", new NBTTagList());
			
			NBTTagList explosionsNBT = fireworkNBT.getList("Explosions", NBTUtil.ID_COMPOUND);
			
			NBTTagCompound newExplosion = new NBTTagCompound();
			newExplosion.putByte("Type", this.scrollMenuType.getRecentSelection());
			newExplosion.putIntArray("Colors", toArray(this.scrollMenuColours.getSelected()));
			newExplosion.putIntArray("FadeColors", toArray(this.scrollMenuColoursFade.getSelected()));
			newExplosion.putBoolean("Trail", this.btn_trail.getData() == 1);
			newExplosion.putBoolean("Flicker", this.btn_flicker.getData() == 1);
			explosionsNBT.add(newExplosion);
		}
		else if (data == 2) {
			if(!this.scrollMenuRemove.hasSelection()) return;
			
			if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_LIST)) {
				NBTTagCompound fireworkNBT = NBTUtil.getOrCreateChildTag(stack, "Fireworks");
				NBTTagList explosionsNBT = fireworkNBT.getList("Explosions", NBTUtil.ID_COMPOUND);
				explosionsNBT.remove(this.scrollMenuRemove.getRecentIndex());
				
				//Remove empty NBT data
				if(explosionsNBT.isEmpty())
					NBTUtil.removeFromSubCompound(stack, "Fireworks", NBTUtil.ID_LIST, "Explosions");
				NBTUtil.hasEmptyTagCompound(stack, true);
			}
		}
		else if (data == 3) {
			if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_LIST)) {
				NBTUtil.removeFromSubCompound(stack, "Fireworks", NBTUtil.ID_LIST, "Explosions");
				NBTUtil.hasEmptyTagCompound(stack, true);
			}
		}
	}
	
	public int[] toArray(List<Integer> list) {
		int[] array = new int[list.size()];
		for(int i = 0; i < list.size(); ++i)
			array[i] = list.get(i);
		return array;
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.fireworks.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.clearSelected();
		
		if(first) {
			if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Flight", NBTUtil.ID_BYTE)) {
				this.fld_flight.setText("" + NBTUtil.getByteInSubCompound(stack, "Fireworks", "Flight"));
			}
		}
		
		List<NBTTagCompound> explosions = new ArrayList<>();
		
		if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_LIST)) {
			NBTTagList explosionNBT = NBTUtil.getListInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_COMPOUND);
			
			if(!explosionNBT.isEmpty()) {
                for(int i = 0; i < explosionNBT.size(); ++i) {
                    explosions.add(explosionNBT.getCompound(i));
                }
            }
		}
		
		this.scrollMenuRemove.setElements(explosions);
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(I18n.format("item.fireworks.flight"), x + 6, y + 17, 16777120);
		itemEditor.getFontRenderer().drawString("Shape", x + 4, y + 32, 16777120);
		itemEditor.getFontRenderer().drawString("Colours", x + 108, y + 32, 16777120);
		itemEditor.getFontRenderer().drawString("Fade Colours", x + 109 + Math.max((width - 105) / 2, 82), y + 32, 16777120);
		itemEditor.getFontRenderer().drawString("Trail:", x + 6, y + 14 * 5 + 47, 16777120);
		itemEditor.getFontRenderer().drawString("Flicker:", x + 63, y + 14 * 5 + 47, 16777120);
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {
		this.scrollMenuType.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuRemove.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuColours.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuColoursFade.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.scrollMenuType = new ScrollMenu<Byte>((GuiScreen)itemEditor, x + 2, y + 42, 100, 14 * 5, 1, Arrays.asList(new Byte[] {0,1,2,3,4})) {

			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(Byte type) {
				if(type >= 0 && type <= 4) {
			        return I18n.format("item.fireworksCharge.type." + type).trim();
				}
			    else{
			         return I18n.format("item.fireworksCharge.type").trim();
			    } 	
			}
		};
		this.scrollMenuRemove = new ScrollMenu<NBTTagCompound>((GuiScreen)itemEditor, x + 2, y + 25 + height / 2, width - 4, height / 2 - 40, 1) {

			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(NBTTagCompound explosion) {
				 List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
                 ItemFireworkStar.func_195967_a(explosion, list);
                 
                //TODO return String.join(" | ", list);
                 return "TEST";
			}
			
		};
		
		List<Integer> colours = new ArrayList<>();
		for(EnumDyeColor dye : EnumDyeColor.values()) {
			colours.add(dye.getFireworkColor());
        }
		
		int colourMenuWidth = Math.max((width - 105) / 2, 82);
		//int colourMenuColumns = Math.max(MathHelper.floor(colourMenuWidth / 90D), 1);
		
		this.scrollMenuColours = new ScrollMenu<Integer>((GuiScreen)itemEditor, x + 105, y + 42, colourMenuWidth, 14 * 5, 1, colours) {
			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(Integer colour) {
				EnumDyeColor enumdyecolor = EnumDyeColor.byFireworkColor(colour);
				return enumdyecolor == null ? new TextComponentTranslation("item.minecraft.firework_star.custom_color").getUnformattedComponentText() : new TextComponentTranslation("item.minecraft.firework_star." + enumdyecolor.getTranslationKey()).getFormattedText();
			}
		};
		this.scrollMenuColoursFade = new ScrollMenu<Integer>((GuiScreen)itemEditor, x + 106 + colourMenuWidth, y + 42, colourMenuWidth, 14 * 5, 1, colours) {

			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(Integer colour) {
				EnumDyeColor enumdyecolor = EnumDyeColor.byFireworkColor(colour);
				return enumdyecolor == null ? new TextComponentTranslation("item.minecraft.firework_star.custom_color").getUnformattedComponentText() : new TextComponentTranslation("item.minecraft.firework_star." + enumdyecolor.getTranslationKey()).getFormattedText();
			}
		};
		
		this.scrollMenuColours.canHaveNoneSelected = true;
		this.scrollMenuColoursFade.canHaveNoneSelected = true;
		this.scrollMenuColours.maxSelected = Integer.MAX_VALUE;
		this.scrollMenuColoursFade.maxSelected = Integer.MAX_VALUE;
		this.scrollMenuColours.setDynamicColumns(itemEditor.getFontRenderer());
		this.scrollMenuColoursFade.setDynamicColumns(itemEditor.getFontRenderer());
		
		this.fld_flight = new GuiTextField(0, itemEditor.getFontRenderer(), x + 84, y + 15, 80, 13);
		
		this.btn_add = new GuiButtonSmall(0, x + width - 90, y + 14 * 5 + 43, 80, 16, "Add Explosion") {
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
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Explosions") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(2);
	    	}
		};
		this.btn_trail = new GuiButtonSmallData(3, x + 34, y + 14 * 5 + 43, 15, 17, "F");
		this.btn_flicker = new GuiButtonSmallData(4, x + 101, y + 14 * 5 + 43, 15, 17, "F");
		
		this.btn_add.enabled = false;
		this.btn_remove.enabled = false;
		
		itemEditor.addTextFieldToGui(this.fld_flight);
		itemEditor.addButtonToGui(this.btn_add);
		itemEditor.addButtonToGui(this.btn_remove);
		itemEditor.addButtonToGui(this.btn_remove_all);
		itemEditor.addButtonToGui(this.btn_trail);
		itemEditor.addButtonToGui(this.btn_flicker);
		itemEditor.addListenerToGui(this.scrollMenuType);
		itemEditor.addListenerToGui(this.scrollMenuRemove);
		itemEditor.addListenerToGui(this.scrollMenuColours);
		itemEditor.addListenerToGui(this.scrollMenuColoursFade);
		this.scrollMenuType.initGui();
		this.scrollMenuRemove.initGui();
		this.scrollMenuColours.initGui();
		this.scrollMenuColoursFade.initGui();
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 3) {
			if(this.btn_trail.getData() == 1) {
				this.btn_trail.displayString = "F";
				this.btn_trail.setData(0);
			}
			else {
				this.btn_trail.displayString = "T";
				this.btn_trail.setData(1);
			}
		}
		else if(button.id == 4) {
			if(this.btn_flicker.getData() == 1) {
				this.btn_flicker.displayString = "F";
				this.btn_flicker.setData(0);
			}
			else {
				this.btn_flicker.displayString = "T";
				this.btn_flicker.setData(1);
			}
		}
	}
	
	@Override
	public void mouseClicked(IGuiItemEditor itemEditor, double mouseX, double mouseY, int mouseButton) {
		this.scrollMenuType.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuRemove.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuColours.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuColoursFade.mouseClicked(mouseX, mouseY, mouseButton);
		
		this.btn_add.enabled = this.scrollMenuType.hasSelection();
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
	}
	
	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_flight) {
			itemEditor.sendUpdateToServer(0);
		}
	}
}
