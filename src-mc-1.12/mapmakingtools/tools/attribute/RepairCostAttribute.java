package mapmakingtools.tools.attribute;

import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class RepairCostAttribute extends IItemAttribute {

	private GuiTextField fld_cost;
	private String cost;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {
		if(this.cost == null)
			return;
		
		if(NumberParse.isInteger(this.cost))
			stack.setRepairCost(NumberParse.getInteger(this.cost));
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.repaircost.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			this.fld_cost.setText(String.valueOf(stack.getRepairCost()));
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, ItemStack stack, int x, int y, int width, int height) {
		this.fld_cost = new GuiTextField(0, itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		itemEditor.getTextBoxList().add(this.fld_cost);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_cost) {
			this.cost = this.fld_cost.getText();
			itemEditor.sendUpdateToServer(-1);
		}
	}
}
