package mapmakingtools.api;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.client.gui.GuiMinecartIndexButton;
import mapmakingtools.client.gui.GuiSmallButton;
import mapmakingtools.client.gui.GuiTabSelect;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.ChannelOutBoundHandler;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketMobArmorAddIndex;
import mapmakingtools.tools.filter.packet.PacketMobArmorRemoveIndex;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;

/**
 * @author ProPercivalalb
 */
public abstract class IFilterClientSpawner extends IFilterClient {

	public static int minecartIndex = 0;
	public int minecartsCount = 1;
	public List<GuiButton> minecartButtons = new ArrayList<GuiButton>();
	
	public void addMinecartButtons(IGuiFilter gui, int topX, int topY) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
		
		if(minecarts == null)
			return;

		this.minecartsCount = minecarts.size();
		this.minecartButtons.clear();
		
		if(minecartsCount <= minecartIndex) {
			minecartIndex = minecarts.size() - 1;
			this.onMinecartIndexChange(gui);
		}
			
		
		int i = 0;
		for(WeightedRandomMinecart randomMinecart : minecarts) {
			GuiMinecartIndexButton button = new GuiMinecartIndexButton(200 + i, topX + 14 * i + 2, topY - 13, 13, 12, "" + i);
			if(i != minecartIndex)
				button.field_146124_l = false;
			minecartButtons.add(button);
			gui.getButtonList().add(button);
			++i;
		}
	}
	
	public void removeMinecartButtons(IGuiFilter gui, int xMouse, int yMouse, int mouseButton, int topX, int topY) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
		
		if(mouseButton == 2) {
			GuiButton button = null;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.field_146127_k >= 200 && tempButton.field_146127_k <= 200 + minecartsCount) {
					if(!tempButton.func_146116_c(ClientHelper.mc, xMouse, yMouse))
						continue;
					if(minecarts.size() <= 1)
						break;
					button = tempButton;
				}
			}
			if(button != null) {
				for(GuiButton tempButton2 : this.minecartButtons) {
					gui.getButtonList().remove(tempButton2);
				}
				minecarts.remove(button.field_146127_k - 200);
				this.addMinecartButtons(gui, topX, topY);
				ChannelOutBoundHandler.sendPacketToServer(new PacketMobArmorRemoveIndex(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
			}
			
		}
		else if(mouseButton == 1) {
			GuiButton button = null;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.field_146127_k >= 200 && tempButton.field_146127_k <= 200 + minecartsCount) {
					if(!tempButton.func_146116_c(ClientHelper.mc, xMouse, yMouse))
						continue;
					if(minecarts.size() >= 17)
						break;
					button = tempButton;
				}
			}
			if(button != null) {
				for(GuiButton tempButton2 : this.minecartButtons) {
					gui.getButtonList().remove(tempButton2);
				}
				NBTTagCompound data = new NBTTagCompound();
				data.setInteger("Weight", 1);
				data.setString("Type", "Pig");
				data.setTag("Properties", new NBTTagCompound());
				WeightedRandomMinecart randomMinecart = spawner.func_145881_a().new WeightedRandomMinecart(data);
				minecarts.add(randomMinecart);
				
				this.addMinecartButtons(gui, topX, topY);
				ChannelOutBoundHandler.sendPacketToServer(new PacketMobArmorAddIndex(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
			}
		}
	}
	
	@Override
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
		
		for(GuiButton tempButton : gui.getButtonList()) {
			if(tempButton.field_146127_k >= 200 && tempButton.field_146127_k <= 200 + minecartsCount) {
				if(!tempButton.func_146116_c(ClientHelper.mc, xMouse, yMouse))
					continue;
				List<String> list = new ArrayList<String>();
    			list.add(minecarts.get(tempButton.field_146127_k - 200).minecartName);

    			gui.drawHoveringText(list, xMouse, yMouse);
			}
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if(button.field_146127_k >= 200 && button.field_146127_k <= 200 + minecartsCount) {
			minecartIndex = button.field_146127_k - 200;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.field_146127_k >= 200 && tempButton.field_146127_k <= 200 + minecartsCount) {
					if(tempButton.field_146127_k - 200 != minecartIndex)
						tempButton.field_146124_l = false;
					else {
						tempButton.field_146124_l = true;
						this.onMinecartIndexChange(gui);
					}
				}
			}
		}
	}
	
	public void onMinecartIndexChange(IGuiFilter gui) {}
}
