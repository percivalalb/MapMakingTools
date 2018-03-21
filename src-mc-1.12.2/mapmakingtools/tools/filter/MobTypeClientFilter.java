package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.FilterMobSpawnerBase;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.datareader.SpawnerEntitiesList;
import mapmakingtools.tools.filter.packet.PacketMobType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class MobTypeClientFilter extends FilterMobSpawnerBase {

	public GuiButton btnOk;
	public ScrollMenu menu;
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
	public void initGui(final IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
        this.menu = new ScrollMenu((GuiScreen)gui, topX + 8, topY + 19, 227, 108, 2, SpawnerEntitiesList.getEntities()) {

			@Override
			public String getDisplayString(String listStr) {
				ResourceLocation location = new ResourceLocation(listStr);
				if(location.getResourceDomain().equals("minecraft"))
					listStr = location.getResourcePath();
				String unlocalised = String.format("entity.%s.name", listStr);
				String localised = I18n.translateToLocal(unlocalised);
				return unlocalised.equalsIgnoreCase(localised) ? listStr : localised;
			}

			@Override
			public void onSetButton() {
				MobTypeClientFilter.selected = this.selected;
				PacketDispatcher.sendToServer(new PacketMobType(gui.getBlockPos(), this.strRefrence.get(this.selected), FilterMobSpawnerBase.minecartIndex));
        		ClientHelper.getClient().player.closeScreen();
			}
        	
        };
        this.menu.initGui();
        this.menu.selected = selected;
        
        this.btnOk = new GuiButton(0, topX + 12, topY + 63, 20, 20, "OK");
        //gui.getButtonList().add(this.btnOk);
        this.addMinecartButtons(gui, topX, topY);
	}

	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.mobType.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 135) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 6, 0);
        this.menu.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		super.mouseClicked(gui, xMouse, yMouse, mouseButton);
		this.menu.mouseClicked(xMouse, yMouse, mouseButton);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	//PacketTypeHandler.populatePacketAndSendToServer(new PacketMobArmor(gui.x, gui.y, gui.z));
            		ClientHelper.getClient().player.closeScreen();
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.getClient().getTextureManager().bindTexture(ResourceReference.SCREEN_SCROLL);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}
