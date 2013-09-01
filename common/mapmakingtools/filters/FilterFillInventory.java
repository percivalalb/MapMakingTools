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
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import mapmakingtools.api.IFilter;
import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.ClientHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketFillInventory;

/**
 * @author ProPercivalalb
 */
public class FilterFillInventory implements IFilter {
	
	public static Icon icon;
	
	@Override
	public Icon getDisplayIcon() {
		return icon;
	}

	@Override
	public String getFilterName() {
		return StatCollector.translateToLocal("filter.fillInventory");
	}

	@Override
	public void registerIcons(IconRegister iconRegistry) {
		icon = iconRegistry.registerIcon("mapmakingtools:fill_Inventory");
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if(tile != null && tile instanceof IInventory) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return false;
	}

    private GuiTextField txt_skullName;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private String item = "Invalid item Id";
    private boolean isValid = false;
    
	@Override
	public void initGui(GuiFilterMenu gui) {
		
        int k = (gui.width - gui.xSize()) / 2;
        int l = (gui.height - gui.ySize()) / 2;
        this.btn_ok = new GuiButton(0, k + 140, l + 66, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, k + 40, l + 66, 60, 20, "Cancel");
        gui.getButtonList().add(this.btn_ok);
        gui.getButtonList().add(this.btn_cancel);
        this.txt_skullName = new GuiTextField(gui.getFont(), k + 20, l + 37, 200, 20);
        this.txt_skullName.setFocused(true);
        this.txt_skullName.setMaxStringLength(7);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j) {
		int k = (gui.width - gui.xSize()) / 2;
	    int l = (gui.height - gui.ySize()) / 2;
        gui.getFont().drawString(getFilterName(), k - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xSize() / 2, l + 10, 0);
        gui.getFont().drawString("Item ID: " + item, k + 20, l + 25, 4210752);
        this.txt_skullName.drawTextBox();
	}

	@Override
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2) {
	    
		
	}
	
	@Override
	public void updateScreen(GuiFilterMenu gui) {
        this.txt_skullName.updateCursorCounter();
        
    	if(QuickBuildHelper.isValidIds(txt_skullName.getText())) {
    		int[] values = QuickBuildHelper.convertIdString(txt_skullName.getText());
    		int blockId = values[0];
    		int blockMeta = values[1];
    		if(blockId > 0 && blockId <= Item.itemsList.length && Item.itemsList[blockId] != null) {
    			item = Item.itemsList[blockId].getItemDisplayName(new ItemStack(blockId, 1, blockMeta));
        		isValid = true;
    		}
    		else {
    			item = "Invalid item Id";
        		isValid = false;
    		}
    	}
        else {
        	item = "Invalid item Id";
    		isValid = false;
        }
        	
        boolean isenabled = this.txt_skullName.getText().trim().length() > 0 && isValid;
        btn_ok.enabled = isenabled;
	}

	@Override
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3) {
		 this.txt_skullName.mouseClicked(var1, var2, var3);
	}

	@Override
	public void keyTyped(GuiFilterMenu gui, char var1, int var2) {
        this.txt_skullName.textboxKeyTyped(var1, var2);

        if (var2 == Keyboard.KEY_RETURN) {
            gui.actionPerformed(btn_ok);
        }

        if (var2 == Keyboard.KEY_ESCAPE) {
            gui.actionPerformed(btn_cancel);
        }
	}

	@Override
	public void actionPerformed(GuiFilterMenu gui, GuiButton var1) {
		if (var1.enabled)
        {
            switch (var1.id)
            {
                case 0:
                	PacketTypeHandler.populatePacketAndSendToServer(new PacketFillInventory(gui.x, gui.y, gui.z, txt_skullName.getText()));
                    
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
