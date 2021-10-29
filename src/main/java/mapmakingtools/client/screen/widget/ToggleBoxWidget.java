package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.lib.Resources;
import mapmakingtools.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToggleBoxWidget<T> extends AbstractTickButton {

    private Supplier<T> value;
    public Function<T, Object> toStringFunc = Objects::toString;

    public ToggleBoxWidget(int xIn, int yIn, @Nullable TickButton previous, Supplier<T> value, IPressable onPress) {
        super(xIn, yIn, 8, 9, TextUtil.EMPTY, previous, onPress);
        this.value = value;
    }

    @Override
    public void renderButton(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        minecraft.getTextureManager().bind(Resources.SCREEN_SCROLL);
        this.blit(stackIn, this.x, this.y, 0 + (this.ticked ? 8 : 0), 135, 8, 9);

        Object obj = this.getDisplayObject();

        if (obj instanceof Component) {
            font.draw(stackIn, (Component) obj, this.x + 10, this.y, 0);
        } else {
            font.draw(stackIn, obj.toString(), this.x + 10, this.y, 0);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    public ToggleBoxWidget<T> setDisplayString(Function<T, Object> toStringFunc) {
        this.toStringFunc = toStringFunc;
        return this;
    }

    public Object getDisplayObject() {
        return this.toStringFunc.apply(this.getValue());
    }

    public T getValue() {
        return this.value.get();
    }
}
