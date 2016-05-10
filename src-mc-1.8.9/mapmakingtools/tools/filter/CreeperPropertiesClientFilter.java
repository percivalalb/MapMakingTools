package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClientSpawner;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketCreeperProperties;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public class CreeperPropertiesClientFilter extends IFilterClientSpawner {

	public GuiTextField txt_radius;
	public GuiTextField txt_fuse;
	public GuiButton btn_ok;
	public GuiButton btn_cancel;
	
	public static String fuseText = "30";
	public static String radiusText = "3";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.creeperproperties.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/explosion.png";
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.btn_ok = new GuiButton(0, topX + 140, topY + 66, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, topX + 40, topY + 66, 60, 20, "Cancel");
        this.txt_radius = new GuiTextField(0, gui.getFont(), topX + 120, topY + 37, 90, 20);
        this.txt_radius.setMaxStringLength(7);
        this.txt_radius.setText(radiusText);
        this.txt_fuse = new GuiTextField(1, gui.getFont(), topX + 20, topY + 37, 90, 20);
        this.txt_fuse.setMaxStringLength(7);
        this.txt_fuse.setText(fuseText);
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        gui.getTextBoxList().add(this.txt_radius);
        gui.getTextBoxList().add(this.txt_fuse);
        
        TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			//if(SpawnerUtil.isBabyMonster(spawner.getSpawnerBaseLogic(), this.minecartIndex)) {
			//	this.btn_covert.displayString = "Turn into Adult";
			//	this.btn_covert.setData(1);
			//}
		}
				
        this.addMinecartButtons(gui, topX, topY);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketCreeperProperties(gui.getBlockPos(), this.txt_fuse.getText(), this.txt_radius.getText(), this.minecartIndex));
            		ClientHelper.mc.thePlayer.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.creeperproperties.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(this.getFilterName(), topX - gui.getFont().getStringWidth(this.getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
        gui.getFont().drawString("Fuse Time", topX + 20, topY + 25, 4210752);
        gui.getFont().drawString("Explosion Radius", topX + 120, topY + 25, 4210752);
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		fuseText = this.txt_fuse.getText();
		radiusText = this.txt_radius.getText();
		this.btn_ok.enabled = NumberParse.areIntegers(fuseText, radiusText);
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return true;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
		WeightedRandomMinecart randomMinecart = minecarts.get(minecartIndex);
		String mobId = SpawnerUtil.getMinecartType(randomMinecart);
		if(mobId.equals("Creeper"))
			return false;
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return EnumChatFormatting.RED + StatCollector.translateToLocal("mapmakingtools.filter.creeperproperties.error");
	}
}
