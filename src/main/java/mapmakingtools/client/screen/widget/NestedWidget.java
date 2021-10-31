package mapmakingtools.client.screen.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class NestedWidget extends AbstractWidget implements ContainerEventHandler {

    protected List<AbstractWidget> children = Lists.newArrayList();
    @Nullable
    private GuiEventListener focused;
    // Was the current focused element clicked on
    private boolean isDragging;

    public NestedWidget(int xIn, int yIn, int widthIn, int heightIn, Component title) {
        super(xIn, yIn, widthIn, heightIn, title);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public final boolean isDragging() {
       return this.isDragging;
    }

    @Override
    public final void setDragging(boolean dragging) {
       this.isDragging = dragging;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
       return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener eventListener) {
       this.focused = eventListener;
    }

 // Methods for a Widget that is also a INestedGuiEventHandler

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (ContainerEventHandler.super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        } else if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        return false;
     }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double changeX, double changeY) {
        if (ContainerEventHandler.super.mouseDragged(mouseX, mouseY, mouseButton, changeX, changeY)) {
            return true;
        } else if (super.mouseDragged(mouseX, mouseY, mouseButton, changeX, changeY)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (ContainerEventHandler.super.mouseReleased(mouseX, mouseY, mouseButton)) {
            return true;
        } else if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double change) {
        if (ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, change)) {
            return true;
        } else if (super.mouseScrolled(mouseX, mouseY, change)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ContainerEventHandler.super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (ContainerEventHandler.super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (ContainerEventHandler.super.charTyped(codePoint, modifiers)) {
            return true;
        } else if (super.charTyped(codePoint, modifiers)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean changeFocus(boolean shiftNotDown) {
        if (ContainerEventHandler.super.changeFocus(shiftNotDown)) {
            return true;
        } else if (super.changeFocus(shiftNotDown)) {
            return true;
        }

        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}
