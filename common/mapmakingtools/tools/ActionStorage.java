package mapmakingtools.tools;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class ActionStorage {

	public EntityPlayer player;
	public PlayerData playerData;
	private ArrayList<ArrayList<CachedBlock>> cachedUndo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<ArrayList<CachedBlock>> cachedRedo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<CachedBlock> cachedCopy = new ArrayList<CachedBlock>();
	private int rotationValue = 0;
	private int flippingValue = 0;
	
	public ActionStorage(PlayerData playerData) { this.playerData = playerData; }
	public ActionStorage(PlayerData playerData, EntityPlayer player) {
		this(playerData);
		this.player = player;
	}
	
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	public boolean hasSomethingToPaste() {
		return this.cachedCopy.size() > 0;
	}
	
	public boolean hasSomethingToUndo() {
		return this.cachedUndo.size() > 0;
	}
	
	public boolean hasSomethingToRedo() {
		return this.cachedRedo.size() > 0;
	}
	
	public void addCopy(ArrayList<CachedBlock> list) {
		this.cachedCopy = list;
		this.rotationValue = 0;
	}
	
	public void addRedo(ArrayList<CachedBlock> list) {
		this.cachedRedo.add(list);
	}
	
	public void addUndo(ArrayList<CachedBlock> list) {
		this.cachedUndo.add(list);
	}
	
	public boolean setRotation(int rotation) {
		if(rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270)
			this.rotationValue = rotation;
		return this.rotationValue == rotation;
	}
	
	public boolean setFlipping(int flipping) {
		if(flipping == 0 || flipping == 1 || flipping == 2)
			this.flippingValue = flipping;
		return this.flippingValue == flipping;
	}
	
	public void paste() {
		
	}
	
	public int flip(ArrayList<CachedBlock> list) {
		if(!this.playerData.hasSelectedPoints())
			return 0;
		
		ArrayList<CachedBlock> newUndo = new ArrayList<CachedBlock>();
		
		for(CachedBlock cachedBlock : list)
			newUndo.add(cachedBlock.setCachedBlockReletiveToFlipped(this.playerData, this.flippingValue));
		
		this.cachedUndo.add(newUndo);
		return newUndo.size();
	}
	
	public int undo() {
		if(!this.hasSomethingToUndo())
			return 0;
		
		ArrayList<CachedBlock> lastUndo = this.cachedUndo.get(this.cachedUndo.size() - 1);
		ArrayList<CachedBlock> newRedo = new ArrayList<CachedBlock>();
		
		for(CachedBlock cachedBlock : lastUndo)
			newRedo.add(cachedBlock.setCachedBlock());
		
		this.cachedUndo.remove(this.cachedUndo.size() - 1);
		this.cachedRedo.add(newRedo);
		return lastUndo.size();
	}
	
	public int redo() {
		if(!this.hasSomethingToRedo())
			return 0;
		
		ArrayList<CachedBlock> lastRedo = this.cachedRedo.get(this.cachedRedo.size() - 1);
		ArrayList<CachedBlock> newUndo = new ArrayList<CachedBlock>();
		
		for(CachedBlock cachedBlock : lastRedo)
			newUndo.add(cachedBlock.setCachedBlock());
		
		this.cachedRedo.remove(this.cachedRedo.size() - 1);
		this.cachedUndo.add(newUndo);
		return lastRedo.size();
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("rotationValue", this.rotationValue);
		tag.setInteger("flippingValue", this.flippingValue);
		
		//Cached Undo list
		NBTTagList undoList = new NBTTagList();
		for(ArrayList<CachedBlock> undos : this.cachedUndo) {
			NBTTagCompound undoData = new NBTTagCompound();
			
			undoData.setInteger("count", undos.size());
			for(int i = 0; i < undos.size(); ++i)
				undoData.setTag("" + i, undos.get(i).writeToNBT(new NBTTagCompound()));
			
			undoList.appendTag(undoData);
		}
		tag.setTag("cachedUndo", undoList);
		
		//Cached Redo list
		NBTTagList redoList = new NBTTagList();
		for(ArrayList<CachedBlock> redos : this.cachedRedo) {
			NBTTagCompound redoData = new NBTTagCompound();
			
			redoData.setInteger("count", redos.size());
			for(int i = 0; i < redos.size(); ++i)
				redoData.setTag("" + i, redos.get(i).writeToNBT(new NBTTagCompound()));
			
			redoList.appendTag(redoData);
		}
		tag.setTag("cachedRedo", redoList);
		
		//Cached Copy
		NBTTagCompound copyData = new NBTTagCompound();
		
		copyData.setInteger("count", this.cachedCopy.size());
		for(int i = 0; i < this.cachedCopy.size(); ++i)
			copyData.setTag("" + i, this.cachedCopy.get(i).writeToNBT(new NBTTagCompound()));
		
		tag.setTag("cachedCopy", copyData);
		
		return tag;
	}
	
	public ActionStorage readFromNBT(NBTTagCompound tag) {
		this.rotationValue = tag.getInteger("rotationValue");
		this.flippingValue = tag.getInteger("flippingValue");
		
		if(tag.hasKey("cachedUndo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedUndo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.getCompoundTagAt(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j)));
				this.cachedUndo.add(list);
			}
		}
		
		if(tag.hasKey("cachedRedo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedRedo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.getCompoundTagAt(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j)));
				this.cachedRedo.add(list);
			}
		}
		
		if(tag.hasKey("cachedCopy")) {
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			NBTTagCompound copyData = tag.getCompoundTag("cachedCopy");
			
			for(int i = 0; i < copyData.getInteger("count"); ++i)
				list.add(new CachedBlock(copyData.getCompoundTag("" + i)));
		}
		
		return this;
	}
}
