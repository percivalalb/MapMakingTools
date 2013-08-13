package mapmakingtools.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.src.ModLoader;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Old extends GuiScreen
{
    protected int xSize = 176;
    protected int ySize = 166;
    private int selected = -1;
    private int listHeight = 0;
    private int scrollY = 0;
    private int scrollHeight = 0;
    private boolean isScrolling = false;
    private List potions = new ArrayList();
    private static Minecraft game = ModLoader.getMinecraftInstance();
    private static ScaledResolution scaling = null;
    public int mobID;
    private HashMap<String, Integer> stringToIDList = new HashMap<String, Integer>();
    private HashMap<Integer, String> idToStringList = new HashMap<Integer, String>();

    public void addEnchanmentToList(int par1, String par2)
    {
        this.idToStringList.put(Integer.valueOf(par1), String.valueOf(par2));
        this.stringToIDList.put(String.valueOf(par2), Integer.valueOf(par1));
    } 
    public int getIDFromString(String ench) 
    {
        if (ench == "")
        {
            return -1;
        }
        Integer ret = (Integer)stringToIDList.get(ench);
        if (ret != 0) 
        {
            return ret;
        }
        return 1;
    }
    
    
    public Old()
    {
        if (potions.size() == 0)
        {
            for(int var2 = 0; var2 < Potion.potionTypes.length; ++var2)
            {
            	if(Potion.potionTypes[var2] != null)
            	{
            		potions.add(Potion.potionTypes[var2].getName());
            		this.addEnchanmentToList(var2, Potion.potionTypes[var2].getName());
            	}
            }
            Collections.sort(potions);
            this.selected = potions.indexOf("potion.moveSpeed");
        }

        if (potions.size() > 0)
        {
            this.listHeight = 14 * ((potions.size())) - 139;
            this.scrollHeight = (int)(139.0D / (double)(this.listHeight + 139) * 139.0D);

            if (this.scrollHeight <= 0 || this.scrollHeight >= 139)
            {
                this.scrollHeight = 139;
            }
        }
    }
    
    private GuiTextField txt_potionLevel;
    private GuiTextField txt_potionDuration;
    private GuiButton btn_ok;
    private GuiButton btn_cancel;
    private String startText = "1";
    private String startText1 = "15";
    private boolean isSplash = false;
    
    public void updateScreen()
    {
        this.txt_potionLevel.updateCursorCounter();
        this.txt_potionDuration.updateCursorCounter();
        
        boolean enabled = this.txt_potionLevel.getText().trim().length() > 0 && this.txt_potionDuration.getText().trim().length() > 0;
        ((GuiButton)this.buttonList.get(0)).enabled = enabled;
    }
    
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int var1 = this.width / 2 + 100 - 80;
        int var2 = this.height / 2 + 50 - 24;
        this.btn_ok = new GuiButton(0, var1, var2 + 65, 60, 20, "Set");
        this.btn_ok.enabled = false;
        var1 = this.width / 2 - 100 + 20;
        var2 = this.height / 2 + 50 - 24;
        this.btn_cancel = new GuiButton(1, var1, var2 + 65, 60, 20, "Cancel");
        this.buttonList.add(this.btn_ok);
        this.buttonList.add(this.btn_cancel);
        var1 = this.width / 2 - 100;
        var2 = this.height / 2 - 50 + 47;
        this.txt_potionLevel = new GuiTextField(this.fontRenderer, var1 + 205, var2, 90, 20);
        this.txt_potionLevel.setMaxStringLength(2);
        this.txt_potionLevel.setText(startText);
        this.txt_potionDuration = new GuiTextField(this.fontRenderer, var1 + 205, var2 + 45, 90, 20);
        this.txt_potionDuration.setMaxStringLength(6);
        this.txt_potionDuration.setText(startText1);
    }
    
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
    
    protected void actionPerformed(GuiButton var1)
    {
        if (var1.enabled)
        {
            switch (var1.id)
            {
                case 0:
                    this.sendNewNameToServer();

                case 1:
                    this.mc.displayGuiScreen((GuiScreen)null);
                    this.mc.setIngameFocus();
                default:
            }
        }
    }
    
    public void sendNewNameToServer()
    {
    	try
        {
            ByteArrayOutputStream var7 = new ByteArrayOutputStream();
            DataOutputStream var8 = new DataOutputStream(var7);
            int enchantmentID = this.getIDFromString((String)potions.get(this.selected));
            
            var8.writeInt(enchantmentID);
            var8.writeInt(new Integer(this.txt_potionLevel.getText().trim()));
            var8.writeInt(new Integer(this.txt_potionDuration.getText().trim()));
            ModLoader.clientSendPacket(new Packet250CustomPayload("MM|CustomPot", var7.toByteArray()));
        }
        catch (IOException var9)
        {
            var9.printStackTrace();
        }
    }
    
    protected void keyTyped(char var1, int var2)
    {
    	this.txt_potionLevel.textboxKeyTyped(var1, var2);
    	this.txt_potionDuration.textboxKeyTyped(var1, var2);

        if (var1 == 10)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }

        if (Integer.valueOf(var1).intValue() == 27)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(1));
        }
    }
    
    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int var1, int var2, int var3)
    {
        this.txt_potionLevel.mouseClicked(var1, var2, var3);
        this.txt_potionDuration.mouseClicked(var1, var2, var3);
        super.mouseClicked(var1, var2, var3);
        int var4 = this.width - this.xSize >> 1;
        int var5 = this.height - this.ySize >> 1;
        var1 -= var4;
        var2 -= var5;

        if (var3 == 0 && var1 >= 10 && var1 < 165 && var2 >= 20 && var2 < 159)
        {
            for (int var6 = 0; var6 < potions.size(); ++var6)
            {
                if (this.mouseInRadioButton(var1, var2, var6))
                {
                    this.selected = var6;
                    
                    break;
                }
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int var1, int var2, float var3)
    {
    	super.drawScreen(var1, var2, var3);
        this.handleInput();
        this.txt_potionLevel.drawTextBox();
        this.txt_potionDuration.drawTextBox();
        int var44 = this.width / 2 - 100;
        int var54 = this.height / 2 - 50 + 35;
        this.fontRenderer.drawString("Potion Level", var44 + 205, var54, 4210752);
        this.fontRenderer.drawString("Potion Duration (Seconds)", var44 + 205, var54 + 45, 4210752);
        int var4 = this.width - this.xSize >> 1;
        int var5 = this.height - this.ySize >> 1;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.func_110434_K().func_110577_a("/mapmakingtools/spawnerNamer.png");
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);

        if (this.scrollHeight != 139)
        {
            this.drawScrollBar();
        }

        scaling = new ScaledResolution(game.gameSettings, game.displayWidth, game.displayHeight);
        this.clip(var4, var5);
        int var9;

        for (var9 = 0; var9 < potions.size(); ++var9)
        {
            int var10 = var4 + 10 + ((var9) != 0 ? 0 : 0);
            int var11 = var5 + 14 * (var9) + 20 - this.scrollY;
            this.drawTexturedModalRect(var10, var11, 176 + (this.selected != var9 ? 0 : 8), 0, 8, 9);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var4, (float)var5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.mouseClicked();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();

        if (this.scrollHeight != 139)
        {
            var1 -= var4;
            var2 -= var5;

            if (Mouse.isButtonDown(0))
            {
                if (var1 >= 155 && var1 < 170 && var2 >= 20 && var2 < 159)
                {
                    this.isScrolling = true;
                }
            }
            else
            {
                this.isScrolling = false;
            }

            if (this.isScrolling)
            {
                this.scrollY = (var2 - 20) * this.listHeight / (139 - (this.scrollHeight >> 1));

                if (this.scrollY < 0)
                {
                    this.scrollY = 0;
                }

                if (this.scrollY > this.listHeight)
                {
                    this.scrollY = this.listHeight;
                }
            }

            var9 = Mouse.getDWheel();

            if (var9 < 0)
            {
                this.scrollY += 14;

                if (this.scrollY > this.listHeight)
                {
                    this.scrollY = this.listHeight;
                }
            }
            else if (var9 > 0)
            {
                this.scrollY -= 14;

                if (this.scrollY < 0)
                {
                    this.scrollY = 0;
                }
            }
        }
    }

    private void clip(int var1, int var2)
    {
        int var3 = (var1 + 10) * scaling.getScaleFactor();
        int var4 = (var2 + 20 - 14 + 2) * scaling.getScaleFactor();
        int var5 = 153 * scaling.getScaleFactor();
        int var6 = 139 * scaling.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(var3, var4, var5, var6);
    }

    public boolean mouseInRadioButton(int var1, int var2, int var3)
    {
        int var4 = 10 + ((var3 & 1) != 0 ? 0 : 0);
        int var5 = 14 * (var3) + 20 - this.scrollY;
        return var1 >= var4 - 1 && var1 < var4 + 9 && var2 >= var5 - 1 && var2 < var5 + 10;
    }

    protected void mouseClicked()
    {
        this.fontRenderer.drawString("Potion Settings", 8, 8, 4210752);
        int var1 = this.width - this.xSize >> 1;
        int var2 = this.height - this.ySize >> 1;
        this.clip(var1, var2);

        for (int var3 = 0; var3 < potions.size(); ++var3)
        {
            int var4 = ((var3 & 1) != 0 ? 00 : 0) + 19;
            int var5 = 14 * (var3) + 20 - this.scrollY;
            String var6 = "" + (String)potions.get(var3);
            String var7 = StatCollector.translateToLocal(var6);

            if (var7 == var6)
            {
                var7 = (String)potions.get(var3);
            }
            
            this.fontRenderer.drawString(var7, var4, var5, 16777215);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawScrollBar()
    {
        int var1 = (this.width - this.xSize >> 1) + 163;
        int var2 = (this.height - this.ySize >> 1) + 19 + this.scrollY * (139 - this.scrollHeight) / this.listHeight;
        this.drawTexturedModalRect(var1 - 10, var2, 176, 9, 15, 1);
        int var3;

        for (var3 = var2 + 1; var3 < var2 + this.scrollHeight - 1; ++var3)
        {
            this.drawTexturedModalRect(var1 - 10, var3, 176, 10, 15, 1);
        }

        this.drawTexturedModalRect(var1 - 10, var3, 176, 11, 15, 1);
    }  

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}
