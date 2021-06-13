package mapmakingtools.worldeditor;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.worldeditor.ICachedArea;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.LinkedList;

public class EditHistory {

    public static short MAX_UNDO_HISTORY_SIZE = 10;

    public LinkedList<ICachedArea> historyBackwards = new LinkedList<>();
    public LinkedList<ICachedArea> historyForwards = new LinkedList<>();

    public void add(ICachedArea cachedArea) {
        this.historyBackwards.add(cachedArea);

        if (this.historyBackwards.size() > MAX_UNDO_HISTORY_SIZE) {
            this.historyBackwards.remove(0);
        }
    }

    private void addRedo(ICachedArea cachedArea) {
        this.historyForwards.add(cachedArea);

        if (this.historyForwards.size() > MAX_UNDO_HISTORY_SIZE) {
            this.historyForwards.remove(0);
        }
    }

    public ICachedArea undo(World world) {
        if (this.historyBackwards.isEmpty()) {
            return null;
        }

        ICachedArea cachedArea = this.historyBackwards.remove(this.historyBackwards.size() - 1);

        this.addRedo(cachedArea.cacheLive(world));
        cachedArea.restore(world);
        return cachedArea;
    }

    public ICachedArea redo(World world) {
        if (this.historyForwards.isEmpty()) {
            return null;
        }

        ICachedArea cachedArea = this.historyForwards.remove(this.historyForwards.size() - 1);

        this.add(cachedArea.cacheLive(world));
        cachedArea.restore(world);
        return cachedArea;
    }

    public static EditHistory read(CompoundNBT nbt) {
        EditHistory selection = new EditHistory();
        if (nbt.contains("history", Constants.NBT.TAG_LIST)) {
            ListNBT historyList = nbt.getList("history", Constants.NBT.TAG_COMPOUND);
            for (int i = Math.max(0, historyList.size() - MAX_UNDO_HISTORY_SIZE); i < historyList.size(); i++) {
                CompoundNBT cacheNBT = historyList.getCompound(i);
                selection.historyBackwards.add(CachedCuboidArea.read(cacheNBT));
            }
        }

        if (nbt.contains("future", Constants.NBT.TAG_LIST)) {
            ListNBT futureList = nbt.getList("future", Constants.NBT.TAG_COMPOUND);

            for (int i = Math.max(0, futureList.size() - MAX_UNDO_HISTORY_SIZE); i < futureList.size(); i++) {
                CompoundNBT cacheNBT = futureList.getCompound(i);
                selection.historyForwards.add(CachedCuboidArea.read(cacheNBT));
            }
        }

        return selection;
    }

    public CompoundNBT write(CompoundNBT nbt) {
        if (!this.historyBackwards.isEmpty()) {
            ListNBT historyList = new ListNBT();
            for (ICachedArea cachedArea : this.historyBackwards) {
                historyList.add(cachedArea.write(new CompoundNBT()));
            }
            nbt.put("history", historyList);
        }

        if (!this.historyForwards.isEmpty()) {
            ListNBT futureList = new ListNBT();
            for (ICachedArea cachedArea : this.historyForwards) {
                futureList.add(cachedArea.write(new CompoundNBT()));
            }
            nbt.put("future", futureList);
        }

        return nbt;
    }
}
