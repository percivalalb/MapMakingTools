package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.api.IFilterClientSpawner;
import mapmakingtools.api.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.tools.filter.packet.PacketMobPosition;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class MobPositionClientFilter extends IFilterClientSpawner {

	private GuiTextField txt_xPosition;
	private GuiTextField txt_yPosition;
	private GuiTextField txt_zPosition;
	private GuiButton btn_ok;
	private GuiButton btn_cancel;
	private GuiButton btn_type;
	
	public static boolean isRelative;
	public static String xText = "0";
	public static String yText = "0";
	public static String zText = "0";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobposition.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:location";
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
        this.btn_type = new GuiButton(2, topX + 130, topY + 72, 70, 20, isRelative ? "Relative" : "Exact");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getButtonList().add(this.btn_type);
        this.txt_xPosition = new GuiTextField(gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_xPosition.setMaxStringLength(7);
        this.txt_xPosition.setText(xText);
        this.txt_yPosition = new GuiTextField(gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_yPosition.setMaxStringLength(7);
        this.txt_yPosition.setText(yText);
        this.txt_zPosition = new GuiTextField(gui.getFont(), topX + 20, topY + 72, 90, 20);
        this.txt_zPosition.setMaxStringLength(7);
        this.txt_zPosition.setText(zText);
        gui.getTextBoxList().add(this.txt_xPosition);
        gui.getTextBoxList().add(this.txt_yPosition);
        gui.getTextBoxList().add(this.txt_zPosition);
        
        this.addMinecartButtons(gui, topX, topY);
        this.onMinecartIndexChange(gui);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 135) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketMobPosition(gui.getX(), gui.getY(), gui.getZ(), txt_xPosition.getText(), txt_yPosition.getText(), txt_zPosition.getText(), (btn_type.displayString.equals("Relative") ? true : false)));
            		ClientHelper.mc.setIngameFocus();
                    break;
                case 2:
                	if(isRelative)
                		this.btn_type.displayString = "Exact";
                	else
                		this.btn_type.displayString = "Relative";
                	
                  	isRelative = !isRelative;
                  	
                	this.redoTextOnBoxs(gui);
                    break;
            }
        }
	}
	
	public void redoTextOnBoxs(IGuiFilter gui) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			
			if(isRelative) {
				if(NumberParse.isDouble(this.txt_xPosition.getText()))
					this.txt_xPosition.setText("" + (NumberParse.getDouble(this.txt_xPosition.getText()) - gui.getX()));
				if(NumberParse.isDouble(this.txt_yPosition.getText()))
					this.txt_yPosition.setText("" + (NumberParse.getDouble(this.txt_yPosition.getText()) - gui.getY()));
				if(NumberParse.isDouble(this.txt_zPosition.getText()))
					this.txt_zPosition.setText("" + (NumberParse.getDouble(this.txt_zPosition.getText()) - gui.getZ()));
			}
			else {
				if(NumberParse.isDouble(this.txt_xPosition.getText()))
					this.txt_xPosition.setText("" + (NumberParse.getDouble(this.txt_xPosition.getText()) + gui.getX()));
				if(NumberParse.isDouble(this.txt_yPosition.getText()))
					this.txt_yPosition.setText("" + (NumberParse.getDouble(this.txt_yPosition.getText()) + gui.getY()));
				if(NumberParse.isDouble(this.txt_zPosition.getText()))
					this.txt_zPosition.setText("" + (NumberParse.getDouble(this.txt_zPosition.getText()) + gui.getZ()));
			}
		}
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		xText = this.txt_xPosition.getText();
		yText = this.txt_yPosition.getText();
		zText = this.txt_zPosition.getText();
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.mobposition.info"));
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return EnumChatFormatting.RED + StatCollector.translateToLocal("mapmakingtools.filter.mobposition.error");
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
