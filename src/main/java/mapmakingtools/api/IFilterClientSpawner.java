package mapmakingtools.api;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.client.gui.GuiMinecartIndexButton;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.tools.filter.packet.PacketMobArmorAddIndex;
import mapmakingtools.tools.filter.packet.PacketMobArmorRemoveIndex;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

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
				button.enabled = false;
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
				if(tempButton.id >= 200 && tempButton.id <= 200 + minecartsCount) {
					if(!tempButton.mousePressed(ClientHelper.mc, xMouse, yMouse))
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
				minecarts.remove(button.id - 200);
				this.addMinecartButtons(gui, topX, topY);
				MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketMobArmorRemoveIndex(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
			}
			
		}
		else if(mouseButton == 1) {
			GuiButton button = null;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.id >= 200 && tempButton.id <= 200 + minecartsCount) {
					if(!tempButton.mousePressed(ClientHelper.mc, xMouse, yMouse))
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
				MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketMobArmorAddIndex(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
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
			if(tempButton.id >= 200 && tempButton.id <= 200 + minecartsCount) {
				if(!tempButton.mousePressed(ClientHelper.mc, xMouse, yMouse))
					continue;
				List<String> list = new ArrayList<String>();
    			list.add(minecarts.get(tempButton.id - 200).entityTypeName);

    			gui.drawHoveringText(list, xMouse, yMouse);
			}
		}
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		if(button.id >= 200 && button.id <= 200 + minecartsCount) {
			minecartIndex = button.id - 200;
			for(GuiButton tempButton : gui.getButtonList()) {
				if(tempButton.id >= 200 && tempButton.id <= 200 + minecartsCount) {
					if(tempButton.id - 200 != minecartIndex)
						tempButton.enabled = false;
					else {
						tempButton.enabled = true;
						this.onMinecartIndexChange(gui);
					}
				}
			}
		}
	}
	
	public void onMinecartIndexChange(IGuiFilter gui) {}
}