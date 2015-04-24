package mapmakingtools.tools;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLLog;

import mapmakingtools.api.enums.MovementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ProPercivalalb
 */
public class ActionStorage {

	public PlayerData playerData;
	private ArrayList<ArrayList<CachedBlock>> cachedUndo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<ArrayList<CachedBlock>> cachedRedo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<CachedBlock> cachedCopy = new ArrayList<CachedBlock>();
	private MovementType rotationValue = MovementType._000_;
	private MovementType flippingValue = MovementType._X_;
	
	public ActionStorage(PlayerData playerData) { 
		this.playerData = playerData;
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
	
	public int addCopy(ArrayList<CachedBlock> list) {
		this.cachedCopy = list;
		this.rotationValue = MovementType._000_;
		return list.size();
	}
	
	public void addRedo(ArrayList<CachedBlock> list) {
		this.cachedRedo.add(list);
		while(cachedRedo.size() > 10)
			this.cachedRedo.remove(0);
	}
	
	public void addUndo(ArrayList<CachedBlock> list) {
		this.cachedUndo.add(list);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
	}
	
	public boolean setRotation(MovementType rotation) {
		if(rotation != null)
			this.rotationValue = rotation;
		return this.rotationValue == rotation;
	}
	
	public boolean setFlipping(MovementType flipping) {
		if(flipping != null)
			this.flippingValue = flipping;
		return this.flippingValue == flipping;
	}
	
	public int paste() {
		if(!this.hasSomethingToPaste())
			return 0;
		
		ArrayList<CachedBlock> newUndo = new ArrayList<CachedBlock>();
		
		for(CachedBlock cachedBlock : this.cachedCopy)
			newUndo.add(cachedBlock.setCachedBlockReletiveToRotated(this.playerData, this.rotationValue));
		
		this.cachedUndo.add(newUndo);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
		return newUndo.size();
	}
	
	public int flip(ArrayList<CachedBlock> list) {
		if(!this.playerData.hasSelectedPoints())
			return 0;
		
		ArrayList<CachedBlock> newUndo = new ArrayList<CachedBlock>();
		
		for(CachedBlock cachedBlock : list)
			newUndo.add(cachedBlock.setCachedBlockReletiveToFlipped(this.playerData, this.flippingValue));
		
		this.cachedUndo.add(newUndo);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
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
		//Removes items if there are 10 or more items
		while(cachedRedo.size() > 10)
			this.cachedRedo.remove(0);
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
		//Removes items if there are 10 or more items
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
		return lastRedo.size();
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("rotationValue", this.rotationValue.getMarker());
		tag.setString("flippingValue", this.flippingValue.getMarker());
		
		//Cached Undo list
		NBTTagList undoList = new NBTTagList();
		for(ArrayList<CachedBlock> undos : this.cachedUndo) {
			NBTTagCompound undoData = new NBTTagCompound();
			
			undoData.setInteger("count", undos.size());
			for(int i = 0; i < undos.size(); ++i)
				undoData.setTag("" + i, undos.get(i).writeToNBT(new NBTTagCompound(), true));
			
			undoList.appendTag(undoData);
		}
		tag.setTag("cachedUndo", undoList);
		
		//Cached Redo list
		NBTTagList redoList = new NBTTagList();
		for(ArrayList<CachedBlock> redos : this.cachedRedo) {
			NBTTagCompound redoData = new NBTTagCompound();
			
			redoData.setInteger("count", redos.size());
			for(int i = 0; i < redos.size(); ++i)
				redoData.setTag("" + i, redos.get(i).writeToNBT(new NBTTagCompound(), true));
			
			redoList.appendTag(redoData);
		}
		tag.setTag("cachedRedo", redoList);
		
		//Cached Copy
		NBTTagCompound copyData = new NBTTagCompound();
		
		copyData.setInteger("count", this.cachedCopy.size());
		for(int i = 0; i < this.cachedCopy.size(); ++i)
			copyData.setTag("" + i, this.cachedCopy.get(i).writeToNBT(new NBTTagCompound(), true));
		
		tag.setTag("cachedCopy", copyData);
		
		return tag;
	}
	
	public ActionStorage readFromNBT(NBTTagCompound tag) {
		this.rotationValue = MovementType.getRotation(tag.getString("rotationValue"));
		this.flippingValue = MovementType.getRotation(tag.getString("flippingValue"));
		
		if(tag.hasKey("cachedUndo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedUndo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.getCompoundTagAt(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j), true));
				this.cachedUndo.add(list);
			}
		}
		
		if(tag.hasKey("cachedRedo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedRedo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.getCompoundTagAt(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j), true));
				this.cachedRedo.add(list);
			}
		}
		
		if(tag.hasKey("cachedCopy")) {
			ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
			NBTTagCompound copyData = tag.getCompoundTag("cachedCopy");
			
			for(int i = 0; i < copyData.getInteger("count"); ++i)
				list.add(new CachedBlock(copyData.getCompoundTag("" + i), true));
		}
		
		return this;
	}
}
