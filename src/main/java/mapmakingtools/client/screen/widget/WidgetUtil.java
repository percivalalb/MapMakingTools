package mapmakingtools.client.screen.widget;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;

public class WidgetUtil {

    /**
     * Sets the text without triggering responder
     *
     * A copy of {@link TextFieldWidget#setText(String)} with necessary changes
     */
    public static void setTextQuietly(TextFieldWidget widget, String textIn) {
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


    public static Vector2f getCentreOfSide(Widget widget, Direction dir) {
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

        return new Vector2f(x, y);
    }
}
