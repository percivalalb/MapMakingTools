package mapmakingtools.client.screen.widget;

import mapmakingtools.client.screen.widget.AbstractTickButton.IPressable;
import mapmakingtools.util.TextUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class WidgetFactory {

    public static EditBox getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable EditBox previous, @Nullable Supplier<Object> text) {
        return getTextField(screen, xIn, yIn, widthIn, heightIn, previous, text, TextUtil.EMPTY);
    }

    public static EditBox getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable EditBox previous, @Nullable Supplier<Object> text, Component title) {
        return getTextField(screen.getMinecraft().font, xIn, yIn, widthIn, heightIn, previous, text, title);
    }

    public static EditBox getTextField(Font fontIn, int xIn, int yIn, int widthIn, int heightIn, @Nullable EditBox previous, @Nullable Supplier<Object> text, Component title) {
        EditBox widget = new EditBox(fontIn, xIn, yIn, widthIn, heightIn, previous, title);
        if (Objects.isNull(previous)) {
            widget.setValue(Objects.toString(text.get(), ""));
        }
        return widget;
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, IPressable onPress) {
        return getTickbox(xIn, yIn, previous, default_, TextUtil.EMPTY, onPress);
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, Component title, IPressable onPress) {
        TickButton widget = new TickButton(xIn, yIn, title, previous, onPress);
        if (Objects.isNull(previous)) {
            widget.setTicked(default_.get());
        }
        return widget;
    }
}
