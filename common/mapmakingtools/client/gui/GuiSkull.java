package mapmakingtools.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StringUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
    private Boolean hasPerson = false;

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
        int var1 = this.width / 2 + 100 - 80;
        int var2 = this.height / 2 + 50 - 24;
        this.btn_ok = new GuiButton(0, var1, var2, 60, 20, "OK");
        this.btn_ok.enabled = false;
        var1 = this.width / 2 - 100 + 20;
        var2 = this.height / 2 + 50 - 24;
        this.btn_cancel = new GuiButton(1, var1, var2, 60, 20, "Cancel");
        this.buttonList.add(this.btn_ok);
        this.buttonList.add(this.btn_cancel);
        var1 = this.width / 2 - 100;
        var2 = this.height / 2 - 50 + 47;
        this.txt_skullName = new GuiTextField(this.fontRenderer, var1, var2, 200, 20);
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
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        this.drawGuiBackground();
        int var4 = this.width / 2 - this.fontRenderer.getStringWidth(this.TITLE) / 2;
        int var5 = this.height / 2 - 50 + 20;
        this.fontRenderer.drawString(this.TITLE, var4, var5, 0);
        var4 = this.width / 2 - 100;
        var5 = this.height / 2 - 50 + 35;
        this.fontRenderer.drawString("New name:", var4, var5, 4210752);
        this.txt_skullName.drawTextBox();
        super.drawScreen(var1, var2, var3);
    }

    protected void drawGuiBackground() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(ResourceReference.screenSmall);
        int var2 = (this.width - 100) / 2;
        int var3 = (this.height - 50) / 2;
        this.drawTexturedModalRect(var2 - 100 + 30, var3 - 50 + 30 + 5, 0, 0, 240, 100);
       // String url = "http://skins.minecraft.net/MinecraftSkins/" + StringUtils.stripControlCodes(txt_skullName.getText().trim()) + ".png";

        TextureHelper.bindPlayerTexture(txt_skullName.getText().trim());
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(var2 - 140 + 30, var3 - 50 + 45 + 5, 32, 64, 32, 64);
        GL11.glPopMatrix();
    }
}
