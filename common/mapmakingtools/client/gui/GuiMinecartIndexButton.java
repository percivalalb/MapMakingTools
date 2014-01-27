package mapmakingtools.client.gui;

import net.minecraft.client.Minecraft;

/**
 * @author ProPercivalalb
 */
public class GuiMinecartIndexButton extends GuiSmallButton {

	public GuiMinecartIndexButton(int id, int field_146128_h, int field_146129_i, int field_146120_f, int field_146121_g, String text) {
		super(id, field_146128_h, field_146129_i, field_146120_f, field_146121_g, text);
	}
	
	@Override
	public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_) {
	    return this.field_146125_m && p_146116_2_ >= this.field_146128_h && p_146116_3_ >= this.field_146129_i && p_146116_2_ < this.field_146128_h + this.field_146120_f && p_146116_3_ < this.field_146129_i + this.field_146121_g;
	}

}
