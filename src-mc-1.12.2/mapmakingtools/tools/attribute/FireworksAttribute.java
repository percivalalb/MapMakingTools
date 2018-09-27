package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonSmall;
import mapmakingtools.client.gui.button.GuiButtonSmallData;
import mapmakingtools.helper.Numbers;
import mapmakingtools.tools.datareader.PotionList;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import scala.actors.threadpool.Arrays;

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
		return stack.getItem() == Items.FIREWORKS || stack.getItem() == Items.FIREWORK_CHARGE;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data == 0) {
			if(Numbers.isByte(this.fld_flight.getText())) {
				NBTTagCompound fireworkNBT = NBTUtil.getOrCreateSubCompound(stack, "Fireworks");
				fireworkNBT.setByte("Flight", (byte)Numbers.parse(this.fld_flight.getText()));
			}
		}
		else if (data == 1) {
			if(!this.scrollMenuType.hasSelection()) return;
			
			NBTTagCompound fireworkNBT = NBTUtil.getOrCreateSubCompound(stack, "Fireworks");
			
			if(!fireworkNBT.hasKey("Explosions", NBTUtil.ID_LIST))
				fireworkNBT.setTag("Explosions", new NBTTagList());
			
			NBTTagList explosionsNBT = fireworkNBT.getTagList("Explosions", NBTUtil.ID_COMPOUND);
			
			NBTTagCompound newExplosion = new NBTTagCompound();
			newExplosion.setByte("Type", this.scrollMenuType.getRecentSelection());
			newExplosion.setIntArray("Colors", toArray(this.scrollMenuColours.getSelected()));
			newExplosion.setIntArray("FadeColors", toArray(this.scrollMenuColoursFade.getSelected()));
			newExplosion.setBoolean("Trail", this.btn_trail.getData() == 1);
			newExplosion.setBoolean("Flicker", this.btn_flicker.getData() == 1);
			explosionsNBT.appendTag(newExplosion);
		}
		else if (data == 2) {
			if(!this.scrollMenuRemove.hasSelection()) return;
			
			if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_LIST)) {
				NBTTagCompound fireworkNBT = NBTUtil.getOrCreateSubCompound(stack, "Fireworks");
				NBTTagList explosionsNBT = fireworkNBT.getTagList("Explosions", NBTUtil.ID_COMPOUND);
				explosionsNBT.removeTag(this.scrollMenuRemove.getRecentIndex());
				
				//Remove empty NBT data
				if(explosionsNBT.hasNoTags())
					NBTUtil.removeTagFromSubCompound(stack, "Fireworks", NBTUtil.ID_LIST, "Explosions");
				NBTUtil.hasEmptyTagCompound(stack, true);
			}
		}
		else if (data == 3) {
			if(NBTUtil.hasTagInSubCompound(stack, "Fireworks", "Explosions", NBTUtil.ID_LIST)) {
				NBTUtil.removeTagFromSubCompound(stack, "Fireworks", NBTUtil.ID_LIST, "Explosions");
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
			
			if(!explosionNBT.hasNoTags()) {
                for(int i = 0; i < explosionNBT.tagCount(); ++i) {
                    explosions.add(explosionNBT.getCompoundTagAt(i));
                }
            }
		}
		
		this.scrollMenuRemove.setElements(explosions);
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(I18n.translateToLocal("item.fireworks.flight"), x + 6, y + 17, 16777120);
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
			        return I18n.translateToLocal("item.fireworksCharge.type." + type).trim();
				}
			    else{
			         return I18n.translateToLocal("item.fireworksCharge.type").trim();
			    } 	
			}
		};
		this.scrollMenuRemove = new ScrollMenu<NBTTagCompound>((GuiScreen)itemEditor, x + 2, y + 25 + height / 2, width - 4, height / 2 - 40, 1) {

			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(NBTTagCompound explosion) {
				 List<String> list = Lists.<String>newArrayList();
                 ItemFireworkCharge.addExplosionInfo(explosion, list);
                 
                 return String.join(" | ", list);
			}
			
		};
		
		List<Integer> colours = new ArrayList<>();
		for (int j = 0; j < ItemDye.DYE_COLORS.length; ++j) {
			colours.add(ItemDye.DYE_COLORS[j]);
        }
		
		int colourMenuWidth = Math.max((width - 105) / 2, 82);
		//int colourMenuColumns = Math.max(MathHelper.floor(colourMenuWidth / 90D), 1);
		
		this.scrollMenuColours = new ScrollMenu<Integer>((GuiScreen)itemEditor, x + 105, y + 42, colourMenuWidth, 14 * 5, 1, colours) {
			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(Integer colour) {
				for(int j = 0; j < ItemDye.DYE_COLORS.length; ++j){
                    if(colour == ItemDye.DYE_COLORS[j]) {
                        return I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                    }
                }
				
				return "" + colour;
			}
		};
		this.scrollMenuColoursFade = new ScrollMenu<Integer>((GuiScreen)itemEditor, x + 106 + colourMenuWidth, y + 42, colourMenuWidth, 14 * 5, 1, colours) {

			@Override
			public void onSetButton() {
				
			}

			@Override
			public String getDisplayString(Integer colour) {
				for(int j = 0; j < ItemDye.DYE_COLORS.length; ++j){
                    if(colour == ItemDye.DYE_COLORS[j]) {
                        return I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                    }
                }
				
				return "" + colour;
			}
		};
		
		this.scrollMenuColours.canHaveNoneSelected = true;
		this.scrollMenuColoursFade.canHaveNoneSelected = true;
		this.scrollMenuColours.maxSelected = Integer.MAX_VALUE;
		this.scrollMenuColoursFade.maxSelected = Integer.MAX_VALUE;
		this.scrollMenuColours.setDynamicColumns(itemEditor.getFontRenderer());
		this.scrollMenuColoursFade.setDynamicColumns(itemEditor.getFontRenderer());
		
		this.fld_flight = new GuiTextField(0, itemEditor.getFontRenderer(), x + 84, y + 15, 80, 13);
		
		this.btn_add = new GuiButtonSmall(0, x + width - 90, y + 14 * 5 + 43, 80, 16, "Add Explosion");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Explosions");
		this.btn_trail = new GuiButtonSmallData(3, x + 34, y + 14 * 5 + 43, 15, 17, "F");
		this.btn_flicker = new GuiButtonSmallData(4, x + 101, y + 14 * 5 + 43, 15, 17, "F");
		
		this.btn_add.enabled = false;
		this.btn_remove.enabled = false;
		
		itemEditor.getTextBoxList().add(this.fld_flight);
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_remove_all);
		itemEditor.getButtonList().add(this.btn_trail);
		itemEditor.getButtonList().add(this.btn_flicker);
		
		this.scrollMenuType.initGui();
		this.scrollMenuRemove.initGui();
		this.scrollMenuColours.initGui();
		this.scrollMenuColoursFade.initGui();
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id == 0) {
			itemEditor.sendUpdateToServer(1);
		}
		else if(button.id == 1) {
			itemEditor.sendUpdateToServer(2);
		}
		else if(button.id == 2) {
			itemEditor.sendUpdateToServer(3);
		}
		else if(button.id == 3) {
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
	public void mouseClicked(IGuiItemEditor itemEditor, int xMouse, int yMouse, int mouseButton) {
		this.scrollMenuType.mouseClicked(xMouse, yMouse, mouseButton);
		this.scrollMenuRemove.mouseClicked(xMouse, yMouse, mouseButton);
		this.scrollMenuColours.mouseClicked(xMouse, yMouse, mouseButton);
		this.scrollMenuColoursFade.mouseClicked(xMouse, yMouse, mouseButton);
		
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
