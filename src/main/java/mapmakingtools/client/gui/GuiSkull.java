package mapmakingtools.client.gui;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import mapmakingtools.MapMakingTools;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.packet.PacketSkullModify;
import mapmakingtools.tools.item.nbt.SkullNBT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;


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
    private static TileEntitySkullRenderer modelskeletonhead = new TileEntitySkullRenderer();

    public GuiSkull(EntityPlayer player) {
        this.entityPlayer = player;
        this.startText = SkullNBT.getSkullName(player.getHeldItem());
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
        this.txt_skullName = new GuiTextField(this.fontRendererObj, var1 + 20, var2 + 40, 200, 20);
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
        		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketSkullModify(this.txt_skullName.getText().trim()));
                this.mc.setIngameFocus();
        	}
        	else if(var1.id == 1) {
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
        this.fontRendererObj.drawString(this.TITLE, var4 + 120 - this.fontRendererObj.getStringWidth(this.TITLE) / 2, var5 + 10, 0);
        this.fontRendererObj.drawString("New name:", var4 + 20, var5 + 30, 4210752);
        this.txt_skullName.drawTextBox();
        super.drawScreen(xMouse, yMouse, particleTicks);
    }

    protected void drawGuiBackground(int xMouse, int yMouse, float particleTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ResourceReference.screenSmall);
        int var2 = (this.width - 240) / 2;
        int var3 = (this.height - 100) / 2;
        this.drawTexturedModalRect(var2, var3, 0, 0, 240, 100);
        /**
        String username = txt_skullName.getText();
        username = username.equals("") ? "steve" : username;
        GameProfile gameprofile = new GameProfile((UUID)null, username);
        TileEntitySkull skull = new TileEntitySkull();
        skull.blockMetadata = 1;
        skull.func_145903_a(1);
        skull.func_152106_a(gameprofile);
        modelskeletonhead.func_147497_a(TileEntityRendererDispatcher.instance);
        modelskeletonhead.renderTileEntityAt(skull, 0.0D, 0.0D, 0.0D, 1.0F);
        **/
    }
    
    private void func_152109_d(GameProfile gameprofile2)
    {
        if (gameprofile2 != null && !StringUtils.isNullOrEmpty(gameprofile2.getName()))
        {
            if (!gameprofile2.isComplete() || gameprofile2.getProperties().containsKey("textures"))
            {
                GameProfile gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(gameprofile2.getName());

                if (gameprofile != null)
                {
                    Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);

                    if (property == null)
                    {
                        gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
                    }

                    gameprofile2 = gameprofile;
                }
            }
        }
    }
    
    private static Map<String, EntityOtherPlayerMP> playerList = new Hashtable<String, EntityOtherPlayerMP>();
	
	public EntityOtherPlayerMP getPlayer(String username) {
		EntityOtherPlayerMP player = playerList.get(username);
		if(player == null) {
			username = username.equals("") ? "steve" : username;
			//player = new EntityOtherPlayerMP(mc.theWorld, );
			playerList.put(username, player);
		}
		return player;
	}
}
