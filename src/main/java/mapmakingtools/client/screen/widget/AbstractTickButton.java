package mapmakingtools.client.screen.widget;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class AbstractTickButton extends AbstractButton {

    protected final AbstractTickButton.IPressable onPress;
    protected boolean ticked;

    public AbstractTickButton(int xIn, int yIn, int widthIn, int heightIn, Component title, @Nullable TickButton previous, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, title);
        this.onPress = onPress;
        if (previous != null) {
            this.ticked = previous.isTicked();
        }
    }

    @Override
    public void onPress() {
        this.toggle();
        this.onPress.onPress(this);
    }

    public boolean isTicked() {
        return this.ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public void toggle() {
        this.ticked = !this.ticked;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
       void onPress(AbstractTickButton btn);
    }
}
