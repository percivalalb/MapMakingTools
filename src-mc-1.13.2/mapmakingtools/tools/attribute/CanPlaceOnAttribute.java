package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.itemeditor.IGuiItemEditor;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.tools.item.nbt.NBTUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.registry.IRegistry;

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
				tagList.add(new NBTTagString(possibleBlock));
			
			break;
		case 1: //Remove selected value to NBTTagList
			if(!this.scrollMenuRemove.hasSelection()) break;
			
			NBTUtil.removeFromSubList(stack, "CanPlaceOn", NBTUtil.ID_STRING, this.selectedDelete);
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
		
		List<String> list = new ArrayList<String>();
		if(NBTUtil.hasTag(stack, "CanPlaceOn", NBTUtil.ID_LIST)) {
			NBTTagList destoryList = stack.getTag().getList("CanPlaceOn", NBTUtil.ID_STRING);
			for(int i = 0; i < destoryList.size(); ++i) {
				list.add(destoryList.getString(i));
			}
		}
		this.scrollMenuRemove.setElements(list);
		this.scrollMenuRemove.initGui();
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiItemEditor itemEditor, float partialTicks, int xMouse, int yMouse) {
		this.scrollMenuAdd.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
		this.scrollMenuRemove.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		List<String> blocks = new ArrayList<String>();
		Iterator<Block> iterator = IRegistry.BLOCK.iterator();
		
		while(iterator.hasNext())
			blocks.add(iterator.next().getRegistryName().toString());
		
		Collections.sort(blocks);
		
		this.scrollMenuAdd = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, blocks) {
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
		
		this.btn_add = new GuiButton(0, x + 2, y + height / 2 - 23, 50, 20, "Add") {
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
		this.btn_remove_all = new GuiButton(2, x + 130, y + height - 23, 130, 20, "Remove all Blocks") {
			@Override
	    	public void onClick(double mouseX, double mouseY) {
				itemEditor.sendUpdateToServer(2);
	    	}
		};
		
		this.btn_add.enabled = false;
		this.btn_remove.enabled = false;
		
		itemEditor.addButtonToGui(this.btn_add);
		itemEditor.addButtonToGui(this.btn_remove);
		itemEditor.addButtonToGui(this.btn_remove_all);
		itemEditor.addListenerToGui(this.scrollMenuAdd);
		itemEditor.addListenerToGui(this.scrollMenuRemove);
		this.scrollMenuAdd.initGui();
		this.scrollMenuRemove.initGui();
	}
	
	@Override
	public void mouseClicked(IGuiItemEditor itemEditor, double mouseX, double mouseY, int mouseButton) {
		this.scrollMenuAdd.mouseClicked(mouseX, mouseY, mouseButton);
		this.scrollMenuRemove.mouseClicked(mouseX, mouseY, mouseButton);
		
		this.btn_add.enabled = this.scrollMenuAdd.hasSelection();
		this.btn_remove.enabled = this.scrollMenuRemove.hasSelection();
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		
	}
}
