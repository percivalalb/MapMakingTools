package mapmakingtools.itemeditor;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import mapmakingtools.util.Util;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.PacketBuffer;

public class BufferFactory {

    public static Button.IPressable ping(int i, Consumer<PacketBuffer> destination) {
        Button.IPressable onPress = (btn) -> {
            PacketBuffer buf = Util.createBuf();
            buf.writeByte(i);
            destination.accept(buf);
        };
        return onPress;
    }

    public static Consumer<Boolean> createBoolean(int i, Consumer<PacketBuffer> destination) {
        return (value) -> {
            PacketBuffer buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeBoolean(value);
            destination.accept(buf);
        };
    }

    public static Consumer<String> createString(int i, Consumer<PacketBuffer> destination) {
        return (value) -> {
            PacketBuffer buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeString(value);
            destination.accept(buf);
        };
    }

    public static Consumer<String> createInteger(int i, Consumer<PacketBuffer> destination) {
        return createInteger(i, null, destination);
    }

    public static Consumer<String> createInteger(int i, @Nullable Predicate<String> additionalCheck, Consumer<PacketBuffer> destination) {
        return (value) -> {
            if (additionalCheck != null && additionalCheck.test(value)) {
                return;
            }

            PacketBuffer buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeInt(Integer.valueOf(value));
            destination.accept(buf);
        };
    }

}
