package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.client.gui.button.GuiTickButton;
import mapmakingtools.helper.NumberParse;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class TooltipFlagsAttribute extends IItemAttribute {

	private GuiTickButton btn_ench;
	private GuiTickButton btn_modif;
	private GuiTickButton btn_unbreak;
	private GuiTickButton btn_destroy;
	private GuiTickButton btn_place;
	private GuiTickButton btn_normal;
	private GuiTickButton btn_all;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}
	
	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(data == 0) {
			int flag = 0;
			
			if(!btn_ench.func_146141_c())
				flag |= 1 << 0; //Enchantment Info
			if(!btn_modif.func_146141_c())
				flag |= 1 << 1; //Modifier Info
			if(!btn_unbreak.func_146141_c())
				flag |= 1 << 2; //Unbreakable Info
			if(!btn_destroy.func_146141_c())
				flag |= 1 << 3; //Block Destroy Info
			if(!btn_place.func_146141_c())
				flag |= 1 << 4; //Can Place On Info
			if(!btn_normal.func_146141_c())
				flag |= 1 << 5; //Normal Info

			if(flag == 0) {
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
			        stack.getTagCompound().removeTag("HideFlags");
					this.btn_all.func_146140_b(true);
			        
			        if(stack.getTagCompound().hasNoTags())
			        	stack.setTagCompound(null);
				}
				return;
			}
			
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
				
			stack.getTagCompound().setInteger("HideFlags", flag);
			this.btn_all.func_146140_b(false);
			
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.tooltipflags.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first) {
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
				int flag = stack.getTagCompound().getInteger("HideFlags");
				
				if((flag & (1 << 0)) == (1 << 0))
					this.btn_ench.func_146140_b(false);
				if((flag & (1 << 1)) == (1 << 1))
					this.btn_modif.func_146140_b(false);
				if((flag & (1 << 2)) == (1 << 2))
					this.btn_unbreak.func_146140_b(false);
				if((flag & (1 << 3)) == (1 << 3))
					this.btn_destroy.func_146140_b(false);
				if((flag & (1 << 4)) == (1 << 4))
					this.btn_place.func_146140_b(false);
				if((flag & (1 << 5)) == (1 << 5))
					this.btn_normal.func_146140_b(false);
				
				if(flag != ~flag)
					this.btn_all.func_146140_b(false);
			}
		}
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
		itemEditor.getFontRenderer().drawString("Enchantment", x + 6, y + 25, 10526880);
		itemEditor.getFontRenderer().drawString("Attribute Modifiers", x + 6, y + 47, 10526880);
		itemEditor.getFontRenderer().drawString("Unbreakable", x + 6, y + 69, 10526880);
		itemEditor.getFontRenderer().drawString("Block Destroy", x + 6, y + 91, 10526880);
		itemEditor.getFontRenderer().drawString("Can Place On", x + 6, y + 113, 10526880);
		itemEditor.getFontRenderer().drawString("Normal Info", x + 6, y + 135, 10526880);
		itemEditor.getFontRenderer().drawString("Show all Information", x + 6, y + height - 22, 16777120);
	}
	
	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		//this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
	    this.btn_ench = new GuiTickButton(0, x + 102, y + 16, true);
	    this.btn_modif = new GuiTickButton(1, x + 102, y + 38, true);
	    this.btn_unbreak = new GuiTickButton(2, x + 102, y + 60, true);
	    this.btn_destroy = new GuiTickButton(3, x + 102, y + 82, true);
	    this.btn_place = new GuiTickButton(4, x + 102, y + 104, true);
	    this.btn_normal = new GuiTickButton(5, x + 102, y + 126, true);
	    this.btn_all = new GuiTickButton(6, x + 112, y + height - 29, true);
	    
		itemEditor.getButtonList().add(this.btn_ench);
		itemEditor.getButtonList().add(this.btn_modif);
		itemEditor.getButtonList().add(this.btn_unbreak);
		itemEditor.getButtonList().add(this.btn_destroy);
		itemEditor.getButtonList().add(this.btn_place);
		itemEditor.getButtonList().add(this.btn_normal);
		itemEditor.getButtonList().add(this.btn_all);
	}

	@Override
	public void actionPerformed(IGuiItemEditor itemEditor, GuiButton button) {
		if(button.id >= 0 && button.id <= 5) {
			((GuiTickButton)button).func_146140_b(!((GuiTickButton)button).func_146141_c());
			itemEditor.sendUpdateToServer(0);
		}
		if(button.id == 6) {
			((GuiTickButton)button).func_146140_b(!((GuiTickButton)button).func_146141_c());
		    this.btn_ench.func_146140_b(((GuiTickButton)button).func_146141_c());
		    this.btn_modif.func_146140_b(((GuiTickButton)button).func_146141_c());
		    this.btn_unbreak.func_146140_b(((GuiTickButton)button).func_146141_c());
		    this.btn_destroy.func_146140_b(((GuiTickButton)button).func_146141_c());
		    this.btn_place.func_146140_b(((GuiTickButton)button).func_146141_c());
		    this.btn_normal.func_146140_b(((GuiTickButton)button).func_146141_c());
			itemEditor.sendUpdateToServer(0);
		}
	}
}
