package mapmakingtools.helper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class PacketHelper {

	public static void writeNBTTagCompound(NBTTagCompound tagCompound, ByteBuf bytes) throws IOException {
        if (tagCompound == null)
        	bytes.writeShort(-1);
        else {
            byte[] data = CompressedStreamTools.compress(tagCompound);
            bytes.writeShort((short)data.length);
            bytes.writeBytes(data);
        }
    }
	
	public static void writeString(String string, ByteBuf bytes) throws IOException {
        byte[] abyte = string.getBytes(Charsets.UTF_8);

        if (abyte.length > 32767)
            throw new IOException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        else {
        	writeSpecialBytes(abyte.length, bytes);
        	bytes.writeBytes(abyte);
        }
    }
	
	public static void writeSpecialBytes(int p_150787_1_, ByteBuf bytes) {
		while ((p_150787_1_ & -128) != 0) {
			bytes.writeByte(p_150787_1_ & 127 | 128);
	        p_150787_1_ >>>= 7;
		}

		bytes.writeByte(p_150787_1_);
	}
	
	public static int readSpecialByte(ByteBuf bytes) {
        int i = 0;
        int j = 0;
        byte b0;

        do {
            b0 = bytes.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5)
                throw new RuntimeException("VarInt too big");
        }
        while ((b0 & 128) == 128);

        return i;
    }
	
	public static String readString(int maxLength, ByteBuf bytes) throws IOException {
        int j = readSpecialByte(bytes);

        if (j > maxLength * 4)
            throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + maxLength * 4 + ")");
        else if (j < 0)
            throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
        else {
            String s = new String(bytes.readBytes(j).array(), Charsets.UTF_8);

            if (s.length() > maxLength)
                throw new IOException("The received string length is longer than maximum allowed (" + j + " > " + maxLength + ")");
            else
                return s;
        }
    }
	
	public static NBTTagCompound readNBTTagCompound(ByteBuf bytes) throws IOException {
	    short length = bytes.readShort();

	    if (length < 0)
	        return null;
	    else {
	        byte[] data = new byte[length];
	        bytes.readBytes(data);
	        return CompressedStreamTools.func_152457_a(data, NBTSizeTracker.field_152451_a);
	    }
	}
	
	public static ItemStack readItemStack(ByteBuf bytes) throws IOException {
        ItemStack itemstack = null;
        short short1 = bytes.readShort();

        if (short1 >= 0) {
            byte b0 = bytes.readByte();
            short short2 = bytes.readShort();
            itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
            itemstack.stackTagCompound = readNBTTagCompound(bytes);
        }

        return itemstack;
    }

    public static void writeItemStack(ItemStack item, ByteBuf bytes) throws IOException {
        if (item == null) {
        	bytes.writeShort(-1);
        }
        else {
        	bytes.writeShort(Item.getIdFromItem(item.getItem()));
        	bytes.writeByte(item.stackSize);
        	bytes.writeShort(item.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (item.getItem().isDamageable() || item.getItem().getShareTag()) {
                nbttagcompound = item.stackTagCompound;
            }

            writeNBTTagCompound(nbttagcompound, bytes);
        }
    }
    
    
    
    
    
    
    
    
    
    public static ItemStack readItemStack(DataInput dataInput) throws IOException {
        ItemStack itemstack = null;
        short short1 = dataInput.readShort();

        if (short1 >= 0) {
            byte b0 = dataInput.readByte();
            short short2 = dataInput.readShort();
            itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
            itemstack.stackTagCompound = readNBTTagCompound(dataInput);
        }

        return itemstack;
    }

    public static void writeItemStack(ItemStack item, DataOutput dataOutput) throws IOException {
        if (item == null) {
        	dataOutput.writeShort(-1);
        }
        else {
        	dataOutput.writeShort(Item.getIdFromItem(item.getItem()));
        	dataOutput.writeByte(item.stackSize);
        	dataOutput.writeShort(item.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (item.getItem().isDamageable() || item.getItem().getShareTag()) {
                nbttagcompound = item.stackTagCompound;
            }

            writeNBTTagCompound(nbttagcompound, dataOutput);
        }
    }
    
	public static NBTTagCompound readNBTTagCompound(DataInput dataInput) throws IOException {
	    short length = dataInput.readShort();

	    if (length < 0)
	        return null;
	    else {
	        byte[] data = new byte[length];
	        dataInput.readFully(data);
	        return CompressedStreamTools.func_152457_a(data, NBTSizeTracker.field_152451_a);
	    }
	}
    
    public static void writeNBTTagCompound(NBTTagCompound tagCompound, DataOutput dataOutput) throws IOException {
        if (tagCompound == null)
        	dataOutput.writeShort(-1);
        else {
            byte[] data = CompressedStreamTools.compress(tagCompound);
            dataOutput.writeShort((short)data.length);
            dataOutput.write(data);
        }
    }
}
