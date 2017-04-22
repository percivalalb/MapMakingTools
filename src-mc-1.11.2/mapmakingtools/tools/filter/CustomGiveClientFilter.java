package mapmakingtools.tools.filter;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketCustomGive;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CustomGiveClientFilter extends IFilterClient {

	public GuiButton btnOk;
	public GuiTextField tempCommand;
	public ItemStack lastStack = null;
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
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        this.tempCommand = new GuiTextField(0, gui.getFont(), gui.getWidth() / 2 - 150, topY + 110, 300, 20);
        this.tempCommand.setMaxStringLength(32767);
        this.tempCommand.setEnabled(false);
        this.tempCommand.setText(this.lastText);
        this.tempCommand.setCursorPositionZero();
        gui.getButtonList().add(this.btnOk);
        gui.getTextBoxList().add(this.tempCommand);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketCustomGive(gui.getBlockPos()));
            		ClientHelper.mc.player.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		IContainerFilter container = gui.getFilterContainer();
		ItemStack stack = container.getInventorySlots().get(0).getStack();
		
		if(!(ItemStack.areItemStacksEqual(stack, this.lastStack) && ItemStack.areItemStackTagsEqual(stack, this.lastStack))) {
			
			if(stack != null) {
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
				this.lastStack = null;
				this.tempCommand.setCursorPositionZero();
			}
			
		}
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.customgive.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenOneSlot);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
