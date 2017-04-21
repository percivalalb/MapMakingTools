package mapmakingtools.tools.filter;

import java.util.List;

import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.IFilterClientSpawner;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketMobVelocity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;

/**
 * @author ProPercivalalb
 */
public class MobVelocityClientFilter extends IFilterClientSpawner {

	private GuiTextField txt_xMotion;
	private GuiTextField txt_yMotion;
	private GuiTextField txt_zMotion;
	private GuiButton btn_ok;
	private GuiButton btn_cancel;
	
	public static String xText = "0.0";
	public static String yText = "0.0";
	public static String zText = "0.0";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobvelocity.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/velocity.png";
	}
	
	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
        this.btn_ok = new GuiButton(0, topX + 140, topY + 101, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 101, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_xMotion = new GuiTextField(0, gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_xMotion.setMaxStringLength(7);
        this.txt_xMotion.setText(xText);
        this.txt_yMotion = new GuiTextField(1, gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_yMotion.setMaxStringLength(7);
        this.txt_yMotion.setText(yText);
        this.txt_zMotion = new GuiTextField(2, gui.getFont(), topX + 60, topY + 72, 90, 20);
        this.txt_zMotion.setMaxStringLength(7);
        this.txt_zMotion.setText(zText);
        gui.getTextBoxList().add(this.txt_xMotion);
        gui.getTextBoxList().add(this.txt_yMotion);
        gui.getTextBoxList().add(this.txt_zMotion);
        
        this.addMinecartButtons(gui, topX, topY);
        this.onMinecartIndexChange(gui);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 135) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
        gui.getFont().drawString("X Motion (East)", topX + 20, topY + 27, 4210752);
        gui.getFont().drawString("Y Motion (Up)", topX + 120, topY + 27, 4210752);
        gui.getFont().drawString("Z Motion (South)", topX + 20, topY + 62, 4210752);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketMobVelocity(gui.getBlockPos(), txt_xMotion.getText(), txt_yMotion.getText(), txt_zMotion.getText(), this.minecartIndex));
            		ClientHelper.mc.thePlayer.closeScreen();
                    break;
            }
        }
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		xText = this.txt_xMotion.getText();
		yText = this.txt_yMotion.getText();
		zText = this.txt_zMotion.getText();
		this.btn_ok.enabled = NumberParse.areDoubles(xText, yText, zText);
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.mobvelocity.info"));
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenMedium);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}
