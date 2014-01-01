package mapmakingtools.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mapmakingtools.inventory.ContainerFilter;
import mapmakingtools.inventory.ContainerWatchPlayer;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketChangeWatchPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class GuiWatchPlayer extends GuiContainer {

	private GuiTextField targetPlayer;
	
    public GuiWatchPlayer(EntityPlayer player) {
        super(new ContainerWatchPlayer(player));
        this.xSize = 176;
        this.ySize = 192;
    }

    @Override
    public void initGui() {
    	super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.targetPlayer = new GuiTextField(this.mc.fontRenderer, 0, 0, 200, 20);
        this.targetPlayer.setText("Player");
    }
    
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void updateScreen() {
    	this.targetPlayer.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked(int xMouse, int yMouse, int mouseButton) {
    	super.mouseClicked(xMouse, yMouse, mouseButton);
    	this.targetPlayer.mouseClicked(xMouse, yMouse, mouseButton);
    }
    
    @Override
    protected void keyTyped(char var1, int var2) {
    	super.keyTyped(var1, var2);
        this.targetPlayer.textboxKeyTyped(var1, var2);
        PacketTypeHandler.populatePacketAndSendToServer(new PacketChangeWatchPlayer(this.targetPlayer.getText()));
        this.getContainer().setWatchedPlayer(this.targetPlayer.getText());
    }
    
    public ContainerWatchPlayer getContainer() {
    	return ((ContainerWatchPlayer)inventorySlots);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
         String s = "Your Inventory";
    	 this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 7, 4210752);
       // this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.watchPlayer);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        
        this.targetPlayer.drawTextBox();
    }
}
