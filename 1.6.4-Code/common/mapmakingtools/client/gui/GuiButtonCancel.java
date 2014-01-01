package mapmakingtools.client.gui;

import java.util.Arrays;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiButtonCancel extends GuiButton {
    
	private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/container/beacon.png");
	private Minecraft mc = Minecraft.getMinecraft();
	private final int field_82257_l;
    private final int field_82258_m;
    private boolean field_82256_n;
    private GuiFilterMenu parent;

    protected GuiButtonCancel(GuiFilterMenu parent, int par1, int par2, int par3, int par5, int par6)
    {
        super(par1, par2, par3, 22, 22, "");
        this.field_82257_l = par5;
        this.field_82258_m = par6;
        this.parent = parent;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            par1Minecraft.getTextureManager().bindTexture(resourceLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            short short1 = 219;
            int k = 0;

            if (!this.enabled)
            {
                k += this.width * 2;
            }
            else if (this.field_82256_n)
            {
                k += this.width * 1;
            }
            else if (this.field_82253_i)
            {
                k += this.width * 3;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, k, short1, this.width, this.height);

            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.field_82257_l, this.field_82258_m, 18, 18);
        }
    }

    public boolean func_82255_b()
    {
        return this.field_82256_n;
    }

    public void func_82254_b(boolean par1)
    {
        this.field_82256_n = par1;
    }
    
    public boolean isMouseAbove(int par2, int par3) {
    	return par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
    }
}
