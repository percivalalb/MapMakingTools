package mapmakingtools.itemeditor;

import mapmakingtools.util.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BufferFactory {

    public static Button.OnPress ping(int i, Consumer<FriendlyByteBuf> destination) {
        Button.OnPress onPress = (btn) -> {
            FriendlyByteBuf buf = Util.createBuf();
            buf.writeByte(i);
            destination.accept(buf);
        };
        return onPress;
    }

    public static Consumer<Boolean> createBoolean(int i, Consumer<FriendlyByteBuf> destination) {
        return (value) -> {
            FriendlyByteBuf buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeBoolean(value);
            destination.accept(buf);
        };
    }

    public static Consumer<String> createString(int i, Consumer<FriendlyByteBuf> destination) {
        return (value) -> {
            FriendlyByteBuf buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeUtf(value);
            destination.accept(buf);
        };
    }

    public static Consumer<String> createInteger(int i, Consumer<FriendlyByteBuf> destination) {
        return createInteger(i, null, destination);
    }

    public static Consumer<String> createInteger(int i, @Nullable Predicate<String> additionalCheck, Consumer<FriendlyByteBuf> destination) {
        return (value) -> {
            if (additionalCheck != null && additionalCheck.test(value)) {
                return;
            }

            FriendlyByteBuf buf = Util.createBuf();
            buf.writeByte(i);
            buf.writeInt(Integer.valueOf(value));
            destination.accept(buf);
        };
    }

}
