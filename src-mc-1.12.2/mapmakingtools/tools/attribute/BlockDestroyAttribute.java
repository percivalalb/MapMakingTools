package mapmakingtools.tools.attribute;

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.util.ResourceLocation;

/**
 * @author ProPercivalalb
 */
public class BlockDestroyAttribute extends IItemAttribute {

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

			NBTTagList tagList = NBTUtil.getOrCreateSubList(stack, "CanDestroy", NBTUtil.ID_STRING);
			
			String possibleBlock = this.scrollMenuAdd.getRecentSelection();
			
			if(!NBTUtil.contains(tagList, possibleBlock))
				tagList.appendTag(new NBTTagString(possibleBlock));
			
			break;
		case 1: //Remove selected value to NBTTagList
			if(!this.scrollMenuRemove.hasSelection()) break;
			
			NBTUtil.removeTagFromSubList(stack, "CanDestroy", NBTUtil.ID_STRING, this.selectedDelete);
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		case 2:
			NBTUtil.removeSubList(stack, "CanDestroy");
			NBTUtil.hasEmptyTagCompound(stack, true);
			
			break;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.blockdestroy.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		this.scrollMenuRemove.clearSelected();
		this.selectedDelete = -1;
		
		List<String> list = new ArrayList<String>();
		if(NBTUtil.hasTag(stack, "CanDestroy", NBTUtil.ID_LIST)) {
			NBTTagList destoryList = stack.getTagCompound().getTagList("CanDestroy", NBTUtil.ID_STRING);
			for(int i = 0; i < destoryList.tagCount(); ++i) {
				list.add(destoryList.getStringTagAt(i));
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
		for(Object key : Block.REGISTRY.getKeys())
			blocks.add(((ResourceLocation)key).toString());
		Collections.sort(blocks);
		
		this.scrollMenuAdd = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15, width - 4, height / 2 - 40, 2, blocks) {
			@Override
			public void onSetButton() {
				BlockDestroyAttribute.selected = this.getRecentIndex();
			}
		};
		
		this.scrollMenuRemove = new ScrollMenu<String>((GuiScreen)itemEditor, x + 2, y + 15 + height / 2, width - 4, height / 2 - 40, 1) {
			@Override
			public void onSetButton() {
				BlockDestroyAttribute.selectedDelete = this.getRecentIndex();
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
