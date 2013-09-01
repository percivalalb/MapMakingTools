package mapmakingtools.filters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketConvertToDispenser;
import mapmakingtools.network.packet.PacketConvertToDropper;
import mapmakingtools.network.packet.PacketCreeperProperties;
import mapmakingtools.network.packet.PacketFillInventory;
import mapmakingtools.network.packet.PacketSpawnerTimings;

/**
 * @author ProPercivalalb
 */
public class FilterSpawnerTimings implements IFilter {

	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.spawnerTimings");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:spawnerTimings");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiTextField txt_minDelay;
    private GuiTextField txt_maxDelay;
    private GuiTextField txt_spawnRadius;
    private GuiTextField txt_spawnCount;
    private GuiTextField txt_entityCap;
    private GuiTextField txt_detectionRange;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		gui.setYSize(160);
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - 160) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 133, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, k + 40, l + 133, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_maxDelay = new GuiTextField(gui.getFont(), k + 120, l + 37, 90, 20);
        this.txt_maxDelay.setMaxStringLength(7);
        this.txt_minDelay = new GuiTextField(gui.getFont(), k + 20, l + 37, 90, 20);
        this.txt_minDelay.setMaxStringLength(7);
        this.txt_spawnRadius = new GuiTextField(gui.getFont(), k + 120, l + 72, 90, 20);
        this.txt_spawnRadius.setMaxStringLength(7);
        this.txt_spawnCount = new GuiTextField(gui.getFont(), k + 20, l + 72, 90, 20);
        this.txt_spawnCount.setMaxStringLength(7);
        this.txt_entityCap = new GuiTextField(gui.getFont(), k + 20, l + 107, 90, 20);
        this.txt_entityCap.setMaxStringLength(7);
        this.txt_detectionRange = new GuiTextField(gui.getFont(), k + 120, l + 107, 90, 20);
        this.txt_detectionRange.setMaxStringLength(7);
        TileEntity tile = gui.entityPlayer.worldObj.getBlockTileEntity(gui.x, gui.y, gui.z);
		if(tile != null && tile instanceof TileEntityMobSpawner) {
			this.txt_maxDelay.setText("" + SpawnerHelper.getMaxDelay(tile));
			this.txt_minDelay.setText("" + SpawnerHelper.getMinDelay(tile));
			this.txt_spawnRadius.setText("" + SpawnerHelper.getSpawnRadius(tile));
			this.txt_spawnCount.setText("" + SpawnerHelper.getSpawnCount(tile));
			this.txt_entityCap.setText("" + SpawnerHelper.getEntityCap(tile));
			this.txt_detectionRange.setText("" + SpawnerHelper.getDetectionRange(tile));
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
	    int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - 160) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        gui.getFont().drawString("Min Delay", k + 20, l + 27, 4210752);
        gui.getFont().drawString("Max Delay", k + 120, l + 27, 4210752);
        gui.getFont().drawString("Spawn Radius", k + 20, l + 62, 4210752);
        gui.getFont().drawString("Spawn Count", k + 120, l + 62, 4210752);
        gui.getFont().drawString("Entity Cap", k + 20, l + 97, 4210752);
        gui.getFont().drawString("Detection Range", k + 120, l + 97, 4210752);
        this.txt_minDelay.drawTextBox();
        this.txt_maxDelay.drawTextBox();
        this.txt_spawnRadius.drawTextBox();
        this.txt_spawnCount.drawTextBox();
        this.txt_entityCap.drawTextBox();
        this.txt_detectionRange.drawTextBox();
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {

	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        this.txt_minDelay.updateCursorCounter();
        this.txt_maxDelay.updateCursorCounter();
        this.txt_spawnRadius.updateCursorCounter();
        this.txt_spawnCount.updateCursorCounter();
        this.txt_entityCap.updateCursorCounter();
        this.txt_detectionRange.updateCursorCounter();
        boolean validFuse = false;
        boolean validExplosion = false;
		try {
			new Integer(txt_minDelay.getText());
			validFuse = true;
		}
		catch(Exception e) {}
		try {
			new Integer(txt_maxDelay.getText());
			validExplosion = true;
		}
		catch(Exception e) {}
        btn_ok.enabled = validFuse && validExplosion;
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 this.txt_minDelay.mouseClicked(var1, var2, var3);
		 this.txt_maxDelay.mouseClicked(var1, var2, var3);
		 this.txt_spawnRadius.mouseClicked(var1, var2, var3);
		 this.txt_spawnCount.mouseClicked(var1, var2, var3);
		 this.txt_entityCap.mouseClicked(var1, var2, var3);
		 this.txt_detectionRange.mouseClicked(var1, var2, var3);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        this.txt_minDelay.textboxKeyTyped(var1, var2);
        this.txt_maxDelay.textboxKeyTyped(var1, var2);
        this.txt_spawnRadius.textboxKeyTyped(var1, var2);
        this.txt_spawnCount.textboxKeyTyped(var1, var2);
        this.txt_entityCap.textboxKeyTyped(var1, var2);
        this.txt_detectionRange.textboxKeyTyped(var1, var2);
        
        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
            gui.actionPerformed(btn_cancel);
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled) {
            switch (var1.id) {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketSpawnerTimings(gui.x, gui.y, gui.z, txt_minDelay.getText(), txt_maxDelay.getText(), txt_spawnRadius.getText(), txt_spawnCount.getText(), txt_entityCap.getText(), txt_detectionRange.getText()));
                    
                case 1:
                    ClientHelper.mc.displayGuiScreen(null);
                    ClientHelper.mc.setIngameFocus();
                    break;
            }
        }
	}

	@Override
	public boolean drawBackground(GuiFilterMenu gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.func_110434_K().func_110577_a(ResourceReference.screenLarge);
		int k = (gui.width - gui.xSize()) / 2;
		int l = (gui.height - 160) / 2;
		gui.drawTexturedModalRect(k, l, 0, 0, gui.xSize(), 160);
		return true;
	}
}
