package mapmakingtools.client.screen.widget;

import mapmakingtools.client.screen.widget.AbstractTickButton.IPressable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class WidgetFactory {

    public static TextFieldWidget getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text) {
        return getTextField(screen, xIn, yIn, widthIn, heightIn, previous, text, "");
    }

    public static TextFieldWidget getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text, String msg) {
        return getTextField(screen.getMinecraft().fontRenderer, xIn, yIn, widthIn, heightIn, previous, text, msg);
    }

    public static TextFieldWidget getTextField(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text, String msg) {
        TextFieldWidget widget = new TextFieldWidget(fontIn, xIn, yIn, widthIn, heightIn, previous, "");
        if (Objects.isNull(previous)) {
            widget.setText(Objects.toString(text.get(), ""));
        }
        return widget;
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, IPressable onPress) {
        return getTickbox(xIn, yIn, previous, default_, "", onPress);
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, String msg, IPressable onPress) {
        TickButton widget = new TickButton(xIn, yIn, msg, previous, onPress);
        if (Objects.isNull(previous)) {
            widget.setTicked(default_.get());
        }
        return widget;
    }
}
