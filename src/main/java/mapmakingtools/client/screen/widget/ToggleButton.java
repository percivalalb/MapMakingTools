package mapmakingtools.client.screen.widget;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import mapmakingtools.util.Util;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ToggleButton<T> extends Button {

    private int index;
    private T[] values;

    public ToggleButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent title, T[] values, @Nullable ToggleButton<T> previous, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, title, onPress);
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
            this.index = (int) MathHelper.positiveModulo(this.index + MathHelper.signum(amount), this.values.length);
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
