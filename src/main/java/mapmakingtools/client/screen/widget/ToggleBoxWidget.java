package mapmakingtools.client.screen.widget;

import mapmakingtools.client.screen.widget.AbstractTickButton;
import mapmakingtools.client.screen.widget.TickButton;
import mapmakingtools.lib.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleBoxWidget<T> extends AbstractTickButton {

    private Supplier<T> value;
    public Function<T, Object> toStringFunc = Objects::toString;

    public ToggleBoxWidget(int xIn, int yIn, @Nullable TickButton previous, Supplier<T> value, IPressable onPress) {
        super(xIn, yIn, 8, 9, "", previous, onPress);
        this.value = value;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(Resources.SCREEN_SCROLL);
        this.blit(this.x, this.y, 0 + (this.ticked ? 8 : 0), 135, 8, 9);

        font.drawString(this.getDisplayString(), this.x + 10, this.y, 0);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        System.out.println("" + this.getValue());
    }

    public ToggleBoxWidget<T> setDisplayString(Function<T, Object> toStringFunc) {
        this.toStringFunc = toStringFunc;
        return this;
    }

    public String getDisplayString() {
        return this.toStringFunc.apply(this.getValue()).toString();
    }

    public T getValue() {
        return this.value.get();
    }
}