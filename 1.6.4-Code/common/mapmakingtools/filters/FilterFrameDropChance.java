package mapmakingtools.filters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.client.gui.GuiValueSlider;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.ReflectionHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketFrameDropChance;

/**
 * @author ProPercivalalb
 */
public class FilterFrameDropChance implements IFilter {

	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.frameDropChance");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:convert");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		if(entity instanceof EntityItemFrame) {
			return true;
		}
		return false;
	}

    private GuiValueSlider slider_chance;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 70, 60, 20, "OK");
        this.btn_cancel = new GuiButton(1, k + 40, l + 70, 60, 20, "Cancel");
        this.slider_chance = new GuiValueSlider(2, k + 20, l + 37, 200, 20, "100%", 1.0D, 0.0D, 1.0D) {
			@Override
			public void onValueChange(double value) {
				this.displayString = Math.round(value * 100) + "%";
			}
        };
        gui.getButtonList().add(this.slider_chance);
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        
		Entity entity = gui.entityPlayer.worldObj.getEntityByID(gui.entityId);
		if(entity instanceof EntityItemFrame) {
			EntityItemFrame frame = (EntityItemFrame)entity;
			this.slider_chance.sliderValue = ReflectionHelper.getField(EntityItemFrame.class, Float.TYPE, frame, 0);
			this.slider_chance.onValueChange(this.slider_chance.getValue());
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
		int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - gui.ySize()) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {
	    
	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
       
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
	
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {

        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(slider_chance);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
        	 ClientHelper.mc.displayGuiScreen(null);
             ClientHelper.mc.setIngameFocus();
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketFrameDropChance(gui.entityId, (int)Math.round(this.slider_chance.getValue() * 100)));
                case 1:
                	ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
            }
        }
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		return false;
	}
}
