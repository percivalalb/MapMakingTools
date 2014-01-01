package mapmakingtools.api;

import org.lwjgl.opengl.GL11;

import mapmakingtools.client.gui.GuiFilterMenu;
import mapmakingtools.core.helper.TextureHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IFilter {

	public Icon getDisplayIcon();
	
	public String getFilterName();
	
	public void registerIcons(IconRegister iconRegistry);
	
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z);
	
	public boolean isApplicable(Entity entity);
	
	public void initGui(GuiFilterMenu gui);
	
	public void drawGuiContainerBackgroundLayer(GuiFilterMenu gui, float f, int i, int j);
	
	public void drawGuiContainerForegroundLayer(GuiFilterMenu gui, int par1, int par2);
	
	public void updateScreen(GuiFilterMenu gui);
	
	public void mouseClicked(GuiFilterMenu gui, int var1, int var2, int var3);
	
	/**
	 * Called when a key is typed, useful for text boxes
	 * @param gui The current GUI
	 * @param cha The key that has been clicked
	 * @param charIndex The key index that has been clicked, (Keyboard.KEY_T) etc.
	 */
	public void keyTyped(GuiFilterMenu gui, char cha, int charIndex);

	/**
	 * 
	 * @param gui The GuiContainer that the Filters are based around
	 * @param button The button that the action has happen to
	 */
	public void actionPerformed(GuiFilterMenu gui, GuiButton button);
	
	/**
	 * Controls whether default GUI is drawn and can also hold the code for custom gui code
	 * @param gui The GuiContainer that the Filters are based around
	 * @return If returned true it will cancel the default drawing of the main GUI
	 */
	public boolean drawBackground(GuiFilterMenu gui);
}
