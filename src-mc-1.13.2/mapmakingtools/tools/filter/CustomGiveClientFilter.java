package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterClient;
import mapmakingtools.api.filter.IFilterContainer;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketCustomGive;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CustomGiveClientFilter extends FilterClient {

	public GuiButton btnOk;
	public GuiTextField tempCommand;
	public ItemStack lastStack = ItemStack.EMPTY;
	public String lastText = "";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.customgive.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/custom_give.png";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, pos);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, pos);
	}
	
	@Override
	public boolean isApplicable(EntityPlayer playerIn, Entity entityIn) { 
		return entityIn instanceof EntityMinecartCommandBlock; 
	}

	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        this.tempCommand = new GuiTextField(0, gui.getFont(), gui.getScreenWidth() / 2 - 150, topY + 110, 300, 20);
        this.tempCommand.setMaxStringLength(32767);
        this.tempCommand.setEnabled(false);
        this.tempCommand.setText(this.lastText);
        this.tempCommand.setCursorPositionZero();
        gui.getButtonList().add(this.btnOk);
        gui.getTextBoxList().add(this.tempCommand);
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketCustomGive());
            		ClientHelper.getClient().player.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		IFilterContainer container = gui.getFilterContainer();
		ItemStack stack = container.getInventorySlots().get(0).getStack();
		
		if(!(ItemStack.areItemStacksEqual(stack, this.lastStack) && ItemStack.areItemStackTagsEqual(stack, this.lastStack))) {
			
			if(!stack.isEmpty()) {
				String command = "/give @p";
				command += " " + Item.REGISTRY.getNameForObject(stack.getItem());
				command += " " + stack.getCount();
				command += " " + stack.getItemDamage();
				
				if(stack.hasTagCompound())
					command += " " + String.valueOf(stack.getTagCompound());
				this.tempCommand.setText(command);
				this.lastText = command;
				this.lastStack = stack.copy();
				this.tempCommand.setCursorPositionZero();
			}
			else {
				this.tempCommand.setText("");
				this.lastText = "";
				this.lastStack = ItemStack.EMPTY;
				this.tempCommand.setCursorPositionZero();
			}
			
		}
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.customgive.info"));
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_ONE_SLOT);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
