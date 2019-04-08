package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.Numbers;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.filter.packet.PacketMobPosition;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class MobPositionClientFilter extends FilterMobSpawnerBase {

	private GuiTextField txt_xPosition;
	private GuiTextField txt_yPosition;
	private GuiTextField txt_zPosition;
	private GuiButton btn_ok;
	private GuiButton btn_cancel;
	private GuiButtonData btn_type;
	
	public static boolean isRelative = true;
	public static String xText = "0.5";
	public static String yText = "0.5";
	public static String zText = "0.5";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobposition.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/location.png";
	}
	
	@Override
	public void initGui(IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.btn_ok = new GuiButton(0, topX + 140, topY + 101, 60, 20, "OK") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    			PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketMobPosition(txt_xPosition.getText(), txt_yPosition.getText(), txt_zPosition.getText(), btn_type.getData() == 0, potentialSpawnIndex));
        		ClientHelper.getClient().player.closeScreen();
    		}
    	};
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 101, 60, 20, "Cancel") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
            	ClientHelper.getClient().player.closeScreen();
    		}
    	};
        this.btn_type = new GuiButtonData(2, topX + 130, topY + 72, 70, 20, isRelative ? "Relative" : "Exact");
        if(!isRelative)
        	this.btn_type.setData(1);
        gui.addButtonToGui(this.btn_ok);
        gui.addButtonToGui(this.btn_cancel);
        gui.addButtonToGui(this.btn_type);
        this.txt_xPosition = new GuiTextField(0, gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_xPosition.setMaxStringLength(7);
        this.txt_xPosition.setText(xText);
        this.txt_yPosition = new GuiTextField(1, gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_yPosition.setMaxStringLength(7);
        this.txt_yPosition.setText(yText);
        this.txt_zPosition = new GuiTextField(2, gui.getFont(), topX + 20, topY + 72, 90, 20);
        this.txt_zPosition.setMaxStringLength(7);
        this.txt_zPosition.setText(zText);
        gui.addTextFieldToGui(this.txt_xPosition);
        gui.addTextFieldToGui(this.txt_yPosition);
        gui.addTextFieldToGui(this.txt_zPosition);
        
        this.addPotentialSpawnButtons(gui, topX, topY);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IFilterGui gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
	    int topY = gui.getGuiY();
        gui.getFont().drawString("X Coordinate", topX + 20, topY + 27, 4210752);
        gui.getFont().drawString("Y Coordinate", topX + 120, topY + 27, 4210752);
        gui.getFont().drawString("Z Coordinate", topX + 20, topY + 62, 4210752);
	}
	
	@Override
	public void actionPerformed(IFilterGui gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 2:
                	if(isRelative) {
                		this.btn_type.displayString = "Exact";
                		this.btn_type.setData(1);
                	}
                	else {
                		this.btn_type.displayString = "Relative";
                		this.btn_type.setData(0);
                	}
                		
                  	isRelative = !isRelative;
                  	
                	this.redoTextOnBoxs(gui);
                    break;
            }
        }
	}
	
	public void redoTextOnBoxs(IFilterGui gui) {
		if(SpawnerUtil.isSpawner(gui)) {
			MobSpawnerBaseLogic spawnerLogic = SpawnerUtil.getSpawnerLogic(gui);
			BlockPos spawnerPos = spawnerLogic.getSpawnerPosition();
			
			if(isRelative) {
				if(Numbers.isDouble(this.txt_xPosition.getText()))
					this.txt_xPosition.setText("" + (Numbers.getDouble(this.txt_xPosition.getText()) - spawnerPos.getX()));
				if(Numbers.isDouble(this.txt_yPosition.getText()))
					this.txt_yPosition.setText("" + (Numbers.getDouble(this.txt_yPosition.getText()) - spawnerPos.getY()));
				if(Numbers.isDouble(this.txt_zPosition.getText()))
					this.txt_zPosition.setText("" + (Numbers.getDouble(this.txt_zPosition.getText()) - spawnerPos.getZ()));
			}
			else {
				if(Numbers.isDouble(this.txt_xPosition.getText()))
					this.txt_xPosition.setText("" + (Numbers.getDouble(this.txt_xPosition.getText()) + spawnerPos.getX()));
				if(Numbers.isDouble(this.txt_yPosition.getText()))
					this.txt_yPosition.setText("" + (Numbers.getDouble(this.txt_yPosition.getText()) + spawnerPos.getY()));
				if(Numbers.isDouble(this.txt_zPosition.getText()))
					this.txt_zPosition.setText("" + (Numbers.getDouble(this.txt_zPosition.getText()) + spawnerPos.getZ()));
			}
		}
	}
	
	@Override
	public void updateScreen(IFilterGui gui) {
		xText = this.txt_xPosition.getText();
		yText = this.txt_yPosition.getText();
		zText = this.txt_zPosition.getText();
		this.btn_ok.enabled = Numbers.areDoubles(xText, yText, zText);
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, double mouseX, double mouseY, int mouseButton) {
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		this.removePotentialSpawnButtons(gui, mouseX, mouseY, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.mobposition.info"));
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_MEDIUM);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}
