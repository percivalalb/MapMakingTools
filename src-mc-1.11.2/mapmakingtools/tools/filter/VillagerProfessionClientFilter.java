package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.client.gui.button.GuiSmallButton;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketVillagerProfession;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

/**
 * @author ProPercivalalb
 */
public class VillagerProfessionClientFilter extends IFilterClient {

	public int professionId = 0;
	public GuiButton btn_covert;
	private GuiButton btn_add;
	private GuiButton btn_remove;
	private GuiLabel lbl_profession;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.villagerprofession.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/villager_profession.png";
	}
	
	@Override
	public boolean isApplicable(EntityPlayer player, Entity entity) {
		if(entity instanceof EntityVillager)
			return true;
		return false;
	}
	
	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.btn_covert = new GuiButton(0, topX + 20, topY + 50, 170, 20, "Set Villager Profession");
        this.btn_add = new GuiSmallButton(1, topX + 200, topY + 58, 13, 12, "+");
	    this.btn_remove = new GuiSmallButton(2, topX + 200, topY + 44, 13, 12, "-");
        this.lbl_profession = new GuiLabel(gui.getFont(), 0, topX + 20, topY + 37, 200, 20, 0);
	    gui.getLabelList().add(this.lbl_profession);
	    gui.getButtonList().add(this.btn_add);
	    gui.getButtonList().add(this.btn_remove);
        gui.getButtonList().add(this.btn_covert);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketVillagerProfession(this.professionId));
            		ClientHelper.mc.thePlayer.closeScreen();
                	break;
                case 1:
                	this.professionId += 1;
                	if(this.professionId >= 5)
                		this.professionId = 0;
                	break;
                case 2:
                	this.professionId -= 1;
                	if(this.professionId <= 0)
                		this.professionId = 4;
                	break;
            }
        }
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.villagerprofession.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(this.getFilterName(), topX - gui.getFont().getStringWidth(this.getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
        
        gui.getFont().drawString("ID: " + this.professionId, topX + 30, topY + 35, 0);
	}
}
