package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NBTUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class CanPlaceOnAttribute extends IItemAttribute {

	private ScrollMenu<String> scrollMenuAdd;
	private ScrollMenu<String> scrollMenuRemove;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiButton btn_remove_all;
	private static int selected = -1;
	private static int selectedDelete = -1;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		switch(data) {
		case 0: //Add selected value to NBTTagList
			if(!this.scrollMenuAdd.hasSelection()) break;

			NBTTagList tagList = NBTUtil.getOrCreateSubList(stack, "CanPlaceOn", NBTUtil.ID_STRING);
			
			String possibleBlock = this.scrollMenuAdd.getRecentSelection();
			
			if(!NBTUtil.contains(tagList, possibleBlock))
				tagList.appendTag(new NBTTagString(possibleBlock));
			
			break;
		case 1: //Remove selected value to NBTTagList
			if(!this.scrollMenuRemove.hasSelection()) break;
			
			NBTUtil.removeTagFromSubList(stack, "CanPlaceOn", NBTUtil.ID_STRING, this.selectedDelete);
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		case 2:
			NBTUtil.removeSubList(stack, "CanPlaceOn");
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.canplaceon.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.clearSelected();
		this.selectedDelete = -1;
		
		//TODO
		this.scrollMenuAdd.elements = new ArrayList<String>();
		for(Object key : Block.REGISTRY.getKeys())
			this.scrollMenuAdd.elements.add(((ResourceLocation)key).toString());
		Collections.sort(this.scrollMenuAdd.elements);
		
		this.scrollMenuAdd.initGui();
		
		List<String> list = new ArrayList<String>();
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("CanPlaceOn", NBTUtil.ID_LIST)) {
			NBTTagList destoryList = stack.getTagCompound().getTagList("CanPlaceOn", NBTUtil.ID_STRING);
			for(int i = 0; i < destoryList.tagCount(); ++i) {
				list.add(destoryList.getStringTagAt(i));
			}
		}
		this.scrollMenuRemove.elements = list;
		this.scrollMenuRemove.initGui();
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {
		this.scrollMenuAdd.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuRemove.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.scrollMenuAdd = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2) {
			@Override
			public void onSetButton() {
				CanPlaceOnAttribute.selected = this.getRecentIndex();
			}
		};
		
		this.scrollMenuRemove = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1) {
			@Override
			public void onSetButton() {
				CanPlaceOnAttribute.selectedDelete = this.getRecentIndex();
			}
		};
		
		this.btn_add = new GuiButton(0, x + 2, y + height / 2 - 23, 50, 20, "Add");
		this.btn_remove = new GuiButton(1, x + 60, y + height - 23, 60, 20, "Remove");
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Blocks");
		
		this.btn_add.enabled = false;
		this.btn_remove.enabled = false;
		
		itemEditor.getButtonList().add(this.btn_add);
		itemEditor.getButtonList().add(this.btn_remove);
		itemEditor.getButtonList().add(this.btn_remove_all);
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
		
		this.btn_add.enabled = this.scrollMenuAdd.hasSelection();
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		
	}
}
