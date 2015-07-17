package mapmakingtools.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiMinecartIndexButton;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.PacketDispatcher;
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
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
		
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
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
		
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
				PacketDispatcher.sendToServer(new PacketMobArmorRemoveIndex(gui.getBlockPos(), IFilterClientSpawner.minecartIndex));
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
				WeightedRandomMinecart randomMinecart = spawner.getSpawnerBaseLogic().new WeightedRandomMinecart(data);
				minecarts.add(randomMinecart);
				
				this.addMinecartButtons(gui, topX, topY);
				PacketDispatcher.sendToServer(new PacketMobArmorAddIndex(gui.getBlockPos(), IFilterClientSpawner.minecartIndex));
			}
		}
	}
	
	@Override
	public void drawToolTips(IGuiFilter gui, int xMouse, int yMouse) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
		
		for(GuiButton tempButton : gui.getButtonList()) {
			if(tempButton.id >= 200 && tempButton.id <= 200 + minecartsCount) {
				if(!tempButton.mousePressed(ClientHelper.mc, xMouse, yMouse))
					continue;
				List<String> list = new ArrayList<String>();
    			list.add(SpawnerUtil.getMinecartType(minecarts.get(tempButton.id - 200)));
    			//list.add("NBT: ");
    			//list.add(SpawnerUtil.getMinecartProperties(minecarts.get(tempButton.id - 200)).toString());
    			
    			gui.drawHoveringText2(list, xMouse, yMouse);
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
