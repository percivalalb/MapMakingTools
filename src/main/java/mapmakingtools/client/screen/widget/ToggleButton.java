package mapmakingtools.client.screen.widget;

import mapmakingtools.util.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.Button.OnPress;

public class ToggleButton<T> extends Button {

    private int index;
    private T[] values;

    public ToggleButton(int xIn, int yIn, int widthIn, int heightIn, Component title, T[] values, @Nullable ToggleButton<T> previous, OnPress onPress) {
        this(xIn, yIn, widthIn, heightIn, title, values, previous, onPress, Button.NO_TOOLTIP);
    }

    public ToggleButton(int xIn, int yIn, int widthIn, int heightIn, Component title, T[] values, @Nullable ToggleButton<T> previous, OnPress onPress, Button.OnTooltip onTooltip) {
        super(xIn, yIn, widthIn, heightIn, title, onPress, onTooltip);
        this.values = values;
        if (previous != null) {
            this.index = previous.index;
        }
    }

    @Override
    public void onPress() {
        this.index = (this.index + 1) % this.values.length;
        super.onPress();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (Util.isPointInRegion(this.x, this.y, this.width, this.height, mouseX, mouseY)) {
            this.index = (int) Mth.positiveModulo(this.index + Mth.sign(amount), this.values.length);
            super.onPress();
            return true;
        }

       return false;
    }

    public T getValue() {
        return this.values[this.index];
    }

    /**
     * Sets the current value, if the value is successfully set
     * true is returned if value is not part of the expected values
     * false is returned and nothing happens
     */
    public boolean setValue(T value) {
        int i = ArrayUtils.indexOf(this.values, value);
        if (i != -1) {
            this.index = i;
            return true;
        }

        return false;
    }

}
