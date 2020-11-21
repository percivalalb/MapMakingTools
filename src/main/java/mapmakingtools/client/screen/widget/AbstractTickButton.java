package mapmakingtools.client.screen.widget;

import javax.annotation.Nullable;

import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AbstractTickButton extends AbstractButton {

    protected final AbstractTickButton.IPressable onPress;
    protected boolean ticked;

    public AbstractTickButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent title, @Nullable TickButton previous, IPressable onPress) {
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

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
       void onPress(AbstractTickButton btn);
    }
}
