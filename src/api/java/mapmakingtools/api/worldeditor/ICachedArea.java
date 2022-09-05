package mapmakingtools.api.worldeditor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public interface ICachedArea {

    /**
     * Restores the blocks & tile entity data to their original position in the world
     *
     * @param world The world to restore to
     */
    void restore(Level world);

    /**
     * Returns the number blocks represented by this cached area
     */
    int getSize();

    /**
     * Caches the blocks currently at the positions this cache describes
     *
     * @param world The world
     * @return The live cached area of the area represented by this cache
     */
    ICachedArea cacheLive(LevelReader world);

    CompoundTag write(CompoundTag nbt);
}
