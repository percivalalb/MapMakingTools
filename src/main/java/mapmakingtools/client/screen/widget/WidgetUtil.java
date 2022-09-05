package mapmakingtools.client.screen.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

public class WidgetUtil {

    /**
     * Sets the text without triggering responder
     *
     * A copy of {@link TextFieldWidget#setText(String)} with necessary changes
     */
    public static void setTextQuietly(EditBox widget, String textIn) {
        // If text is equal do nothing
        if (textIn.equals(widget.value)) {
            return;
        }

        if (widget.filter.test(textIn)) {
            if (textIn.length() > widget.maxLength) {
                widget.value = textIn.substring(0, widget.maxLength);
            } else {
                widget.value = textIn;
            }

            widget.setCursorPosition(widget.getCursorPosition());
            widget.setHighlightPos(widget.getCursorPosition());
        }

    }


    public static Vec2 getCentreOfSide(AbstractWidget widget, Direction dir) {
        float x = widget.x;
        float y = widget.y;

        switch(dir) {
        case NORTH:
            x += widget.getWidth() / 2F;
            break;
        case EAST:
            x += widget.getWidth();
            y += widget.getHeight() / 2F;
            break;
        case SOUTH:
            x += widget.getWidth() / 2F;
            y += widget.getHeight();
            break;
        case WEST:
            y += widget.getHeight() / 2F;
            break;
        default:
            x += widget.getWidth() / 2F;
            y += widget.getHeight() / 2F;
            break;
        }

        return new Vec2(x, y);
    }
}
