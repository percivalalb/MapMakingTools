package mapmakingtools.util;

import com.google.common.base.Strings;
import io.netty.buffer.Unpooled;
import mapmakingtools.lib.Constants;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Util {

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
    public static Direction[] HORIZONTAL_DIRECTIONS = net.minecraft.Util.make(() -> Arrays.stream(Direction.values()).filter((dir) -> {
        return dir.getAxis().isHorizontal();
    }).sorted(Comparator.comparingInt(Direction::get2DDataValue)).toArray(Direction[]::new));
    private static Pattern number = Pattern.compile("^-?\\d*\\.?\\d*$");
    public static Predicate<String> NUMBER_INPUT_PREDICATE = (str) -> {
        return number.matcher(str).matches();
    };
    private static DecimalFormat FORMAT_NUMBER_3DP = net.minecraft.Util.make(() -> {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        return df;
    });

    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    /**
     * @return
     */
    public static FriendlyByteBuf createBuf() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf writeVarIntArray(FriendlyByteBuf buf, Integer[] array) {
        buf.writeVarInt(array.length);

        for (int i : array) {
            buf.writeVarInt(i);
        }

        return buf;
    }

    /**
     * Converts byte array to hex pads hex numbers so they
     * are always two characters long
     *
     * @param arr
     * @return
     */
    public static String toHex(int[] arr) {
        StringBuilder bd = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            bd.append(Integer.toHexString(arr[i]).toUpperCase(Locale.ROOT));
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
        } else {
            return FORMAT_NUMBER_3DP.format(value);
        }
    }

    public static <T> List<Map.Entry<ResourceKey<T>, T>> getDelegates(Supplier<IForgeRegistry<T>> registry, Predicate<T> filter) {
        // Create an empty list for the delegates, initialise to
        // it's final size to avoid resizes down the road (improves efficiency)
        List<Map.Entry<ResourceKey<T>, T>> list = new ArrayList<>(25);
        for (Map.Entry<ResourceKey<T>, T> t : registry.get().getEntries()) {
            if (filter == null || filter.test(t.getValue())) {
                list.add(t);
            }
        }
        return list;
    }
}
