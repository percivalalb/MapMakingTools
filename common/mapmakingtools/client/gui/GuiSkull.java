package mapmakingtools.client.gui;

import java.util.Hashtable;
import java.util.Map;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.NBTData;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketSkullModify;

/**
 * @author ProPercivalalb
 */
public class GuiSkull extends GuiScreen {
	
    private String TITLE = "Name your Skull with a Player Name";
    EntityPlayer entityPlayer;
    private GuiTextField txt_skullName;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private String startText = "";
    private boolean hasPerson = false;
    private static ModelSkeletonHead modelskeletonhead = new ModelSkeletonHead(0, 0, 64, 32);

    public GuiSkull(EntityPlayer var1) {
        this.entityPlayer = var1;

        if (ItemStackHelper.hasTag(var1.getHeldItem(), NBTData.SKULL_NAME)) {
        	this.startText = ItemStackHelper.getString(var1.getHeldItem(), NBTData.SKULL_NAME);
        }
    }

    @Override
    public void updateScreen() {
        this.txt_skullName.updateCursorCounter();
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int var1 = (this.width - 240) / 2;
        int var2 = (this.height - 100) / 2;
        this.btn_ok = new GuiButton(0, var1 + 140, var2 + 70, 60, 20, "OK");
        this.btn_ok.enabled = false;
        this.btn_cancel = new GuiButton(1, var1 + 40, var2 + 70, 60, 20, "Cancel");
        this.buttonList.add(this.btn_ok);
        this.buttonList.add(this.btn_cancel);
        this.txt_skullName = new GuiTextField(this.fontRenderer, var1 + 20, var2 + 40, 200, 20);
        this.txt_skullName.setFocused(true);
        this.txt_skullName.setMaxStringLength(32);
        this.txt_skullName.setText(startText);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton var1){
        if (var1.enabled) {
        	if(var1.id == 0) {
        		PacketTypeHandler.populatePacketAndSendToServer(new PacketSkullModify(txt_skullName.getText().trim()));
        		this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        	}
        	else if(var1.id == 1) {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    protected void keyTyped(char var1, int var2) {
        this.txt_skullName.textboxKeyTyped(var1, var2);
        ((GuiButton)this.buttonList.get(0)).enabled = this.txt_skullName.getText().trim().length() > 0;

        if (var2 == Keyboard.KEY_ESCAPE) { //Exit Screen
            this.actionPerformed((GuiButton)this.buttonList.get(1));
        }
    }

    @Override
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        this.txt_skullName.mouseClicked(var1, var2, var3);
    }

    @Override
    public void drawScreen(int xMouse, int yMouse, float particleTicks) {
        this.drawDefaultBackground();
        this.drawGuiBackground(xMouse, yMouse, particleTicks);
        int var4 = (this.width - 240) / 2;
        int var5 = (this.height - 100) / 2;
        this.fontRenderer.drawString(this.TITLE, var4 + 120 - this.fontRenderer.getStringWidth(this.TITLE) / 2, var5 + 10, 0);
        this.fontRenderer.drawString("New name:", var4 + 20, var5 + 30, 4210752);
        this.txt_skullName.drawTextBox();
        super.drawScreen(xMouse, yMouse, particleTicks);
    }

    protected void drawGuiBackground(int xMouse, int yMouse, float particleTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(ResourceReference.screenSmall);
        int var2 = (this.width - 240) / 2;
        int var3 = (this.height - 100) / 2;
        this.drawTexturedModalRect(var2, var3, 0, 0, 240, 100);
       // String url = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(txt_skullName.getText().trim()) + ".png";

       // TextureHelper.bindPlayerTexture(txt_skullName.getText().trim());
        //TileEntitySkullRenderer.skullRenderer.func_82393_a(3, 3, 0, ForgeDirection.UP.ordinal(), 3, 3, txt_skullName.getText().trim());
        String username = txt_skullName.getText();
        TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, 3, username);
        EntityOtherPlayerMP player = getPlayer(username);
        byte b0 = player.getDataWatcher().getWatchableObjectByte(16);
        player.getDataWatcher().updateObject(16, Byte.valueOf((byte)(b0 | 1 << 1)));
        //player.sho
		GuiInventory.func_110423_a(var2 - 35, var3 + 90, 40, (float)(var2 - 35 - xMouse), (float)(var3 + 90 - 30 - yMouse), player);
       
		/** 
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(var2 - 140 + 30, var3 - 50 + 45 + 5, 32, 64, 32, 64);
        GL11.glPopMatrix();
        **/
    }
    
    private static Map<String, EntityOtherPlayerMP> playerList = new Hashtable<String, EntityOtherPlayerMP>();
	
	public EntityOtherPlayerMP getPlayer(String username) {
		EntityOtherPlayerMP player = playerList.get(username);
		if(player == null) {
			player = new EntityOtherPlayerMP(mc.theWorld, username.equals("") ? "steve" : username);
			playerList.put(username, player);
		}
		return player;
	}
}
