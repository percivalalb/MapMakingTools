package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.filter.FilterMobSpawnerBase;
import mapmakingtools.api.filter.IFilterGui;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceLib;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.datareader.SpawnerEntitiesList;
import mapmakingtools.tools.filter.packet.PacketMobType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class MobTypeClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	public ScrollMenu<String> menu;
	public static int selected = SpawnerEntitiesList.getEntities().indexOf("minecraft:pig");
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobType.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/mob_type.png";
	}

	@Override
	public void initGui(final IFilterGui gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
        this.menu = new ScrollMenu<String>((GuiScreen)gui, topX + 8, topY + 19, 227, 108, 2, SpawnerEntitiesList.getEntities()) {

			@Override
			public String getDisplayString(String listStr) {
				ResourceLocation location = new ResourceLocation(listStr);
				if(location.getNamespace().equals("minecraft"))
					listStr = location.getPath();
				String unlocalised = String.format("entity.%s.name", listStr);
				String localised = I18n.format(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}

			@Override
			public void onSetButton() {
				MobTypeClientFilter.selected = this.getRecentIndex();
				PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketMobType(this.getRecentSelection(), FilterMobSpawnerBase.potentialSpawnIndex));
        		ClientHelper.getClient().player.closeScreen();
			}
        	
        };
        this.menu.initGui();
        this.menu.setSelected(selected);
        
        this.btnOk = new GuiButton(0, topX + 12, topY + 63, 20, 20, "OK") {
    		@Override
			public void onClick(double mouseX, double mouseY) {
    	    	//PacketTypeHandler.populatePacketAndSendToServer(new PacketMobArmor(gui.x, gui.y, gui.z));
        		ClientHelper.getClient().player.closeScreen();
    		}
    	};
        //gui.addButtonToGui(this.btnOk);
		gui.addListenerToGui(this.menu);
        this.addPotentialSpawnButtons(gui, topX, topY);
	}

	@Override
	public List<String> getFilterInfo(IFilterGui gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.format("mapmakingtools.filter.mobType.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IFilterGui gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
        this.menu.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void mouseClicked(IFilterGui gui, double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(gui, mouseX, mouseY, mouseButton);
		this.menu.mouseClicked(mouseX, mouseY, mouseButton);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		this.removePotentialSpawnButtons(gui, mouseX, mouseY, mouseButton, topX, topY);
	}
	
	@Override
	public boolean drawBackground(IFilterGui gui) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceLib.SCREEN_SCROLL);
		int topX = (gui.getScreenWidth() - gui.xFakeSize()) / 2;
        int topY = gui.getGuiY();
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}
