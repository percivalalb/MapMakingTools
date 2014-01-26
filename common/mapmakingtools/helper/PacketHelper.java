package mapmakingtools.helper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class PacketHelper {

	public static void writeNBTTagCompound(NBTTagCompound tagCompound, DataOutput outputStream) throws IOException {
        if (tagCompound == null) {
        	outputStream.writeShort(-1);
        }
        else {
            byte[] data = CompressedStreamTools.compress(tagCompound);
            outputStream.writeShort((short)data.length);
            outputStream.write(data);
        }
    }
	
	public static NBTTagCompound readNBTTagCompound(DataInput inputStream) throws IOException {
	    short length = inputStream.readShort();

	    if (length < 0) {
	        return null;
	    }
	    else {
	        byte[] data = new byte[length];
	        inputStream.readFully(data);
	        return CompressedStreamTools.decompress(data);
	    }
	}
	
	public static ItemStack readItemStack(DataInput dataInput) throws IOException {
        ItemStack itemstack = null;
        short short1 = dataInput.readShort();

        if (short1 >= 0) {
            byte b0 = dataInput.readByte();
            short short2 = dataInput.readShort();
            itemstack = new ItemStack(Item.func_150899_d(short1), b0, short2);
            itemstack.stackTagCompound = readNBTTagCompound(dataInput);
        }

        return itemstack;
    }

    /**
     * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
     */
    public static void writeItemStack(ItemStack item, DataOutput dataOutput) throws IOException {
        if (item == null) {
            dataOutput.writeShort(-1);
        }
        else {
            dataOutput.writeShort(Item.func_150891_b(item.getItem()));
            dataOutput.writeByte(item.stackSize);
            dataOutput.writeShort(item.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (item.getItem().isDamageable() || item.getItem().getShareTag()) {
                nbttagcompound = item.stackTagCompound;
            }

            writeNBTTagCompound(nbttagcompound, dataOutput);
        }
    }
}
