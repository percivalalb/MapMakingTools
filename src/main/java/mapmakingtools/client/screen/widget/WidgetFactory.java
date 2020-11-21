package mapmakingtools.client.screen.widget;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import mapmakingtools.client.screen.widget.AbstractTickButton.IPressable;
import mapmakingtools.util.TextUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class WidgetFactory {

    public static TextFieldWidget getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text) {
        return getTextField(screen, xIn, yIn, widthIn, heightIn, previous, text, TextUtil.EMPTY);
    }

    public static TextFieldWidget getTextField(Screen screen, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text, ITextComponent title) {
        return getTextField(screen.getMinecraft().fontRenderer, xIn, yIn, widthIn, heightIn, previous, text, title);
    }

    public static TextFieldWidget getTextField(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget previous, @Nullable Supplier<Object> text, ITextComponent title) {
        TextFieldWidget widget = new TextFieldWidget(fontIn, xIn, yIn, widthIn, heightIn, previous, title);
        if (Objects.isNull(previous)) {
            widget.setText(Objects.toString(text.get(), ""));
        }
        return widget;
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, IPressable onPress) {
        return getTickbox(xIn, yIn, previous, default_, TextUtil.EMPTY, onPress);
    }

    public static TickButton getTickbox(int xIn, int yIn, @Nullable TickButton previous, @Nullable Supplier<Boolean> default_, ITextComponent title, IPressable onPress) {
        TickButton widget = new TickButton(xIn, yIn, title, previous, onPress);
        if (Objects.isNull(previous)) {
            widget.setTicked(default_.get());
        }
        return widget;
    }
}
