package mapmakingtools.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.core.helper.ItemStackHelper;
import mapmakingtools.core.helper.SpawnerHelper;
import mapmakingtools.core.helper.TextureHelper;
import mapmakingtools.lib.NBTData;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketSkullModify;

/**
 * @author ProPercivalalb
 */
public class GuiSpawnerSettings extends GuiScreenMMT {

	public int x, y, z;
    EntityPlayer entityPlayer;

    public GuiSpawnerSettings(EntityPlayer var1, int x, int y, int z) {
        this.entityPlayer = var1;
    	this.x = x;
		this.y = y;
		this.z = z;
    }
    
    @Override
    public void initGui() {
    	super.initGui();
        int imageX = 173;
        int imageY = 210;
        int screenCentre = (this.height - imageY) / 2;
        int[] values = new int[] {543, 423, 5266, 52234, 626, 642};
        for(int var1 = 0; var1 < 6; ++var1) {
        	this.textFieldList.add(new GuiNumberField(imageX / 2 + 20, 35 + screenCentre + (21 * var1) + var1, values[var1]));
        	//this.buttonList.add(btn_extraData = new GuiSmallButton(1 + var1, 77 + var2, 104 + var3 + (20 * (var1 + 1)) + (var1 * 2), 13, 12, "?", "TEST"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton var1){
        if (var1.enabled) {
        	if(var1.id == 0) {
        		
        	}
        }
    }
    
    @Override
    public void updateScreen() {
    	super.updateScreen();
    	
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        //this.drawDefaultBackground();
        this.drawGuiBackground();
        int imageX = 173;
        int imageY = 210;
        int screenCentre = (this.height - imageY) / 2;
        String title = EnumChatFormatting.UNDERLINE + "Spawner Properties";
        this.mc.fontRenderer.drawString(title, 35, screenCentre + 9, Color.YELLOW.getRGB());
        String[] values = new String[] {"Min Spawn Delay", "Max Spawn Delay", "Spawn Count", "Entity Cap", "Detection Range", "Spawn Radius"};
        for(int var1 = 0; var1 < 6; ++var1) {
        	this.mc.fontRenderer.drawString(values[var1], imageX / 2 + 12 - this.mc.fontRenderer.getStringWidth(values[var1]), 35 + screenCentre + (21 * var1) + var1, Color.WHITE.getRGB());
        }
        
        super.drawScreen(par1, par2, par3);
    }

    protected void drawGuiBackground() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(ResourceReference.transparentBackground);
        int imageX = 173;
        int imageY = 210;
        int screenCentre = (this.height - imageY) / 2;
        this.drawTexturedModalRect(0, screenCentre, 0, 0, imageX, imageY);
        for (int k = 0; k < this.textFieldList.size(); ++k) {
        	GuiTextField guibutton = (GuiTextField)this.textFieldList.get(k);
        	if(guibutton instanceof GuiNumberField) {
        		GuiNumberField fld = (GuiNumberField)guibutton;
        		this.drawTexturedModalRect(fld.xPos - 4, fld.yPos - 3, 0, 211, 51, 13);
        	}
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
