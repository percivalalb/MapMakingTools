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
	private ArrayList<ArrayList<CachedBlock>> cachedUndo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<ArrayList<CachedBlock>> cachedRedo = new ArrayList<ArrayList<CachedBlock>>();
	private ArrayList<CachedBlock> cachedCopy = new ArrayList<CachedBlock>();
	private int rotationValue = -1;
	private int flippingValue = -1;
	
	public ActionStorage() {}
	public ActionStorage(EntityPlayer player) {
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
		this.rotationValue = -1;
	}
	
	public void addRedo(ArrayList<CachedBlock> list) {
		this.cachedRedo.add(list);
	}
	
	public void addUndo(ArrayList<CachedBlock> list) {
		this.cachedUndo.add(list);
	}
	
	public void paste() {
		
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
		NBTTagList list1 = new NBTTagList();
		for(ArrayList<CachedBlock> undos : this.cachedUndo) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("count", undos.size());

			for(int i = 0; i < undos.size(); ++i)
				tag1.setTag("" + i, undos.get(i).writeToNBT(new NBTTagCompound()));
			
			list1.appendTag(tag1);
		}
		tag.setTag("cachedUndo", list1);
		
		NBTTagList list2 = new NBTTagList();
		for(ArrayList<CachedBlock> redos : this.cachedRedo) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("count", redos.size());
			
			for(int i = 0; i < redos.size(); ++i)
				tag1.setTag("" + i, redos.get(i).writeToNBT(new NBTTagCompound()));
			
			list2.appendTag(tag1);
		}
		tag.setTag("cachedRedo", list2);
		return tag;
	}
	
	public ActionStorage readFromNBT(NBTTagCompound tag) {
		if(tag.hasKey("cachedUndo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedUndo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.func_150305_b(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j)));
				this.cachedUndo.add(list);
			}
		}
		
		if(tag.hasKey("cachedRedo")) {
			NBTTagList list1 = (NBTTagList)tag.getTag("cachedRedo");
			for(int i = 0; i < list1.tagCount(); ++i) {
				ArrayList<CachedBlock> list = new ArrayList<CachedBlock>();
				NBTTagCompound tag1 = list1.func_150305_b(i);
				for(int j = 0; j < tag1.getInteger("count"); ++j)
					list.add(new CachedBlock(tag1.getCompoundTag("" + j)));
				this.cachedRedo.add(list);
			}
		}
		return this;
	}
}
