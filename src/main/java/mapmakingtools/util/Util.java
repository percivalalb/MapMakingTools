package mapmakingtools.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

import com.google.common.base.Strings;

import io.netty.buffer.Unpooled;
import mapmakingtools.lib.Constants;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class Util {

    public static Predicate<String> NUMBER_INPUT_PREDICATE = (str) -> {
        if (Strings.isNullOrEmpty(str) || "-".equals(str)) {
            return true;
        }

        try {
            Integer.valueOf(str);
            return true;
        } catch (IllegalArgumentException var3) {
            return false;
        }
    };

    // Numbers greater or equal to 0
    public static Predicate<String> NON_NEGATIVE_NUMBER_INPUT_PREDICATE = (str) -> {
        if (Strings.isNullOrEmpty(str)) {
            return true;
        }

        try {
            Integer s = Integer.valueOf(str);
            return s >= 0;
        } catch (IllegalArgumentException var3) {
            return false;
        }
    };

    // Numbers greater or equal to 0
    public static Predicate<String> POSITIVE_NUMBER_INPUT_PREDICATE = (str) -> {
        if (Strings.isNullOrEmpty(str)) {
            return true;
        }

        try {
            Integer s = Integer.valueOf(str);
            return s > 0;
        } catch (IllegalArgumentException var3) {
            return false;
        }
    };

    public static Predicate<String> BYTE_INPUT_PREDICATE = (str) -> {
        if (Strings.isNullOrEmpty(str)) {
            return true;
        }

        try {
            Integer s = Integer.valueOf(str);
            return s < 128 && s > 0;
        } catch (IllegalArgumentException var3) {
            return false;
        }
    };

    public static Predicate<String> IS_NULL_OR_EMPTY = Strings::isNullOrEmpty;

    public static Direction[] HORIZONTAL_DIRECTIONS = net.minecraft.util.Util.make(() -> Arrays.stream(Direction.values()).filter((dir) -> {
        return dir.getAxis().isHorizontal();
    }).sorted(Comparator.comparingInt(Direction::getHorizontalIndex)).toArray(Direction[]::new));

    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    /**
     * @return
     */
    public static PacketBuffer createBuf() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static PacketBuffer writeVarIntArray(PacketBuffer buf, Integer[] array) {
        buf.writeVarInt(array.length);

        for (int i : array) {
            buf.writeVarInt(i);
        }

        return buf;
    }

    /**
     * Converts byte array to hex pads hex numbers so they
     * are always two characters long
     * @param arr
     * @return
     */
    public static String toHex(int[] arr) {
        StringBuilder bd = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            bd.append(Integer.toHexString(arr[i]));
            while (bd.length() < i * 2 + 2) {
                bd.append('0');
            }
        }
        return bd.toString();
    }

    public static boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    public static String tryToWriteAsInt(double value) {
        if ((int) value == value) { //Is int
            return String.valueOf((int) value);
        }
        else {
            return String.valueOf(value);
        }
    }
}
