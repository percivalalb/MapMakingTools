package mapmakingtools.tools;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

/**
 * @author ProPercivalalb
 */
public class ActionStorage {

	public PlayerData playerData;
	private ArrayList<ArrayList<BlockCache>> cachedUndo = new ArrayList<ArrayList<BlockCache>>();
	private ArrayList<ArrayList<BlockCache>> cachedRedo = new ArrayList<ArrayList<BlockCache>>();
	private ArrayList<BlockCache> cachedCopy = new ArrayList<BlockCache>();
	private Rotation rotationValue = Rotation.NONE;
	private Mirror flippingValue = Mirror.NONE;
	
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
	
	public int addCopy(ArrayList<BlockCache> list) {
		this.cachedCopy = list;
		this.rotationValue = Rotation.NONE;
		return list.size();
	}
	
	public void addRedo(ArrayList<BlockCache> list) {
		this.cachedRedo.add(list);
		while(cachedRedo.size() > 10)
			this.cachedRedo.remove(0);
	}
	
	public void addUndo(ArrayList<BlockCache> list) {
		this.cachedUndo.add(list);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
	}
	
	public ArrayList<BlockCache> getLastUndo() {
		if(!this.hasSomethingToUndo())
			return null;
		return this.cachedUndo.get(this.cachedUndo.size() - 1);
	}
	
	public boolean setRotation(Rotation rotation) {
		if(rotation != null)
			this.rotationValue = rotation;
		return this.rotationValue == rotation;
	}
	
	public boolean setFlipping(Mirror flipping) {
		if(flipping != null)
			this.flippingValue = flipping;
		return this.flippingValue == flipping;
	}
	
	public int paste() {
		if(!this.hasSomethingToPaste())
			return 0;
		
		ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
		
		for(BlockCache bse : this.cachedCopy)
			newUndo.add(bse.restoreRelativeToRotated(this.playerData, this.rotationValue));
		
		this.cachedUndo.add(newUndo);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
		return newUndo.size();
	}
	
	public int flip(ArrayList<BlockCache> list) {
		if(!this.playerData.hasSelectedPoints())
			return 0;
		
		ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
		
		for(BlockCache bse : list)
			newUndo.add(bse.restoreRelativeToFlipped(this.playerData, this.flippingValue));
			
		this.cachedUndo.add(newUndo);
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
		return newUndo.size();
	}
	
	public int undo() {
		if(!this.hasSomethingToUndo())
			return 0;
		
		ArrayList<BlockCache> lastUndo = this.cachedUndo.get(this.cachedUndo.size() - 1);
		ArrayList<BlockCache> newRedo = new ArrayList<BlockCache>();
		
		for(BlockCache bse : lastUndo) {
			newRedo.add(BlockCache.createCache(this.playerData.getPlayer(), bse.getWorld(), bse.pos));
			bse.restore(true);
		}
		
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
		
		ArrayList<BlockCache> lastRedo = this.cachedRedo.get(this.cachedRedo.size() - 1);
		ArrayList<BlockCache> newUndo = new ArrayList<BlockCache>();
		
		for(BlockCache bse : lastRedo) {
			newUndo.add(BlockCache.createCache(this.playerData.getPlayer(), bse.getWorld(), bse.pos));
			bse.restore(true);
		}
		
		this.cachedRedo.remove(this.cachedRedo.size() - 1);
		this.cachedUndo.add(newUndo);
		//Removes items if there are 10 or more items
		while(cachedUndo.size() > 10)
			this.cachedUndo.remove(0);
		return lastRedo.size();
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		//tag.putString("rotationValue", this.rotationValue.getMarker());
		//tag.putString("flippingValue", this.flippingValue.getMarker());
		
		//Cached Undo list
		NBTTagList undoList = new NBTTagList();
		for(ArrayList<BlockCache> undos : this.cachedUndo) {
			NBTTagCompound undoData = new NBTTagCompound();
			
			undoData.putInt("count", undos.size());
			for(int i = 0; i < undos.size(); ++i) {
				NBTTagCompound compound = new NBTTagCompound();
				undos.get(i).writeToNBT(compound);
				undoData.put("" + i, compound);
			}
			undoList.add(undoData);
		}
		tag.put("cachedUndo", undoList);
		
		//Cached Redo list
		NBTTagList redoList = new NBTTagList();
		for(ArrayList<BlockCache> redos : this.cachedRedo) {
			NBTTagCompound redoData = new NBTTagCompound();
			
			redoData.putInt("count", redos.size());
			for(int i = 0; i < redos.size(); ++i) {
				NBTTagCompound compound = new NBTTagCompound();
				redos.get(i).writeToNBT(compound);
				redoData.put("" + i, compound);
			}
			
			redoList.add(redoData);
		}
		tag.put("cachedRedo", redoList);
		
		//Cached Copy
		NBTTagCompound copyData = new NBTTagCompound();
		
		copyData.putInt("count", this.cachedCopy.size());
		for(int i = 0; i < this.cachedCopy.size(); ++i) {
			NBTTagCompound compound = new NBTTagCompound();
			this.cachedCopy.get(i).writeToNBT(compound);
			copyData.put("" + i, compound);
		}
		
		tag.put("cachedCopy", copyData);
		
		return tag;
	}
	
	public ActionStorage readFromNBT(NBTTagCompound tag) {
		//this.rotationValue = MovementType.getRotation(tag.getString("rotationValue"));
		//this.flippingValue = MovementType.getRotation(tag.getString("flippingValue"));
		
		if(tag.contains("cachedUndo")) {
			NBTTagList list1 = (NBTTagList)tag.get("cachedUndo");
			for(int i = 0; i < list1.size(); ++i) {
				ArrayList<BlockCache> list = new ArrayList<BlockCache>();
				NBTTagCompound tag1 = list1.getCompound(i);
				for(int j = 0; j < tag1.getInt("count"); ++j)
					list.add(BlockCache.readFromNBT(tag1.getCompound("" + j)));
				this.cachedUndo.add(list);
			}
		}
		
		if(tag.contains("cachedRedo")) {
			NBTTagList list1 = (NBTTagList)tag.get("cachedRedo");
			for(int i = 0; i < list1.size(); ++i) {
				ArrayList<BlockCache> list = new ArrayList<BlockCache>();
				NBTTagCompound tag1 = list1.getCompound(i);
				for(int j = 0; j < tag1.getInt("count"); ++j)
					list.add(BlockCache.readFromNBT(tag1.getCompound("" + j)));
				this.cachedRedo.add(list);
			}
		}
		
		if(tag.contains("cachedCopy")) {
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			NBTTagCompound copyData = tag.getCompound("cachedCopy");
			
			for(int i = 0; i < copyData.getInt("count"); ++i)
				list.add(BlockCache.readFromNBT(copyData.getCompound("" + i)));
		}
		
		return this;
	}
}
