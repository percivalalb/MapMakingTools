package mapmakingtools.api.worldeditor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface ICachedArea {

    /**
     * Restores the blocks & tile entity data to their original position in the world
     *
     * @param world The world to restore to
     */
    public void restore(World world);

    /**
     * Returns the number blocks represented by this cached area
     */
    public int getSize();

    /**
     * Caches the blocks currently at the positions this cache describes
     *
     * @param world The world
     * @return The live cached area of the area represented by this cache
     */
    public ICachedArea cacheLive(IWorldReader world);

    public CompoundNBT write(CompoundNBT nbt);
}
