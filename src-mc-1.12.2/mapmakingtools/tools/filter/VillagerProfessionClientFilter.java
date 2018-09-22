package mapmakingtools.tools.filter;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.ScrollMenu;
import mapmakingtools.api.interfaces.FilterClient;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketVillagerProfession;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.registries.GameData;

/**
 * @author ProPercivalalb
 */
public class VillagerProfessionClientFilter extends FilterClient {

	public ScrollMenu<ResourceLocation> menu;
	public RegistryNamespaced<ResourceLocation, VillagerProfession> REGISTRY = GameData.getWrapper(VillagerProfession.class);
	
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
		gui.setYSize(135);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
        this.menu = new ScrollMenu<ResourceLocation>((GuiScreen)gui, topX + 8, topY + 19, 227, 108, 2, this.getProfesionList()) {

			@Override
			public void onSetButton() {
				PacketDispatcher.sendToServer(new PacketVillagerProfession(REGISTRY.getObject(this.getRecentSelection())));
        		ClientHelper.getClient().player.closeScreen();
			}
        	
        };
        this.menu.initGui();
        this.menu.setSelected(0);
	}
	
	private List<ResourceLocation> getProfesionList() {
		List<ResourceLocation> list = new ArrayList<ResourceLocation>();
		for(ResourceLocation location : REGISTRY.getKeys())
			list.add(location);
		return list;
	}

	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.villagerprofession.info"));
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter baseIn, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(baseIn, partialTicks, xMouse, yMouse);
		int topX = (baseIn.getWidth() - baseIn.xFakeSize()) / 2;
	    int topY = (baseIn.getHeight() - 135) / 2;
        baseIn.getFont().drawString(getFilterName(), topX - baseIn.getFont().getStringWidth(getFilterName()) / 2 + baseIn.xFakeSize() / 2, topY + 7, 0);
        this.menu.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		super.mouseClicked(gui, xMouse, yMouse, mouseButton);
		this.menu.mouseClicked(xMouse, yMouse, mouseButton);
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
