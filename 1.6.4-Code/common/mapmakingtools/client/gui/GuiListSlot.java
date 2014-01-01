package mapmakingtools.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import mapmakingtools.core.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;

/**
 * @author ProPercivalalb
 */
public class GuiListSlot extends GuiSlot {

	private int validEnchantsSize = 0;
	private ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
	public int selected = 0;
	
	public GuiListSlot(GuiScreen parent) {
		super(Minecraft.getMinecraft(), parent.width, parent.height, 50, parent.height, 13);
		//this.left = 
		this.discoverEnchants();
	}

	public void discoverEnchants() {
		boolean[] valid = new boolean[Enchantment.enchantmentsList.length];
		int count = 0;
		for(int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
			Enchantment ench = Enchantment.enchantmentsList[i];
			
			if(ench == null)
				continue;
			
			enchantments.add(ench);
			valid[i] = true;
			count++;
		}
		
		this.validEnchantsSize = count;
	}
	
	@Override
	protected int getSize() {
		return this.validEnchantsSize;
	}
	
	@Override
	protected int getContentHeight() { 
		return getSize() * this.slotHeight;
	}

	@Override
	protected void elementClicked(int i, boolean flag) {
		LogHelper.logDebug("Click");
		this.selected = i;
	}

	@Override
	protected boolean isSelected(int i) {
		return i == this.selected;
	}

	@Override
	protected void drawBackground() {
		
	}

	@Override
	protected void drawSlot(int i, int x, int y, int l, Tessellator tessellator) {
		String s = StatCollector.translateToLocal(enchantments.get(i).getName());
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
                
        font.drawStringWithShadow(s, x + 3, y + 1, 0xFFFFFF);
       // font.drawStringWithShadow("" + SoundManager.soundConfig.get(s).get, x + 6, y + 12, 0x777777)
	}
	
	@Override
	protected void overlayBackground(int par1, int par2, int par3, int par4) {
		Tessellator tess = Tessellator.instance;
		tess.disableColor();
		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
    }
	
	@Override
	public int func_77209_d() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		return this.getContentHeight() - (this.bottom - this.top - 4);
	}

	@Override
    protected void drawContainerBackground(Tessellator tess) {
        super.drawContainerBackground(tess);
    }
}
