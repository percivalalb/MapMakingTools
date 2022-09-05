package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.ICachedArea;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

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

    public ICachedArea undo(Level world) {
        if (this.historyBackwards.isEmpty()) {
            return null;
        }

        ICachedArea cachedArea = this.historyBackwards.remove(this.historyBackwards.size() - 1);

        this.addRedo(cachedArea.cacheLive(world));
        cachedArea.restore(world);
        return cachedArea;
    }

    public ICachedArea redo(Level world) {
        if (this.historyForwards.isEmpty()) {
            return null;
        }

        ICachedArea cachedArea = this.historyForwards.remove(this.historyForwards.size() - 1);

        this.add(cachedArea.cacheLive(world));
        cachedArea.restore(world);
        return cachedArea;
    }

    public static EditHistory read(CompoundTag nbt) {
        EditHistory selection = new EditHistory();
        if (nbt.contains("history", Tag.TAG_LIST)) {
            ListTag historyList = nbt.getList("history", Tag.TAG_COMPOUND);
            for (int i = Math.max(0, historyList.size() - MAX_UNDO_HISTORY_SIZE); i < historyList.size(); i++) {
                CompoundTag cacheNBT = historyList.getCompound(i);
                selection.historyBackwards.add(CachedCuboidArea.read(cacheNBT));
            }
        }

        if (nbt.contains("future", Tag.TAG_LIST)) {
            ListTag futureList = nbt.getList("future", Tag.TAG_COMPOUND);

            for (int i = Math.max(0, futureList.size() - MAX_UNDO_HISTORY_SIZE); i < futureList.size(); i++) {
                CompoundTag cacheNBT = futureList.getCompound(i);
                selection.historyForwards.add(CachedCuboidArea.read(cacheNBT));
            }
        }

        return selection;
    }

    public CompoundTag write(CompoundTag nbt) {
        if (!this.historyBackwards.isEmpty()) {
            ListTag historyList = new ListTag();
            for (ICachedArea cachedArea : this.historyBackwards) {
                historyList.add(cachedArea.write(new CompoundTag()));
            }
            nbt.put("history", historyList);
        }

        if (!this.historyForwards.isEmpty()) {
            ListTag futureList = new ListTag();
            for (ICachedArea cachedArea : this.historyForwards) {
                futureList.add(cachedArea.write(new CompoundTag()));
            }
            nbt.put("future", futureList);
        }

        return nbt;
    }
}
