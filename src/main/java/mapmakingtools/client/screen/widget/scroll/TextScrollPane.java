package mapmakingtools.client.screen.widget.scroll;

import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.client.screen.widget.ScrollPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class TextScrollPane extends ScrollPane {

    @Nullable
    private Component text;

    public TextScrollPane(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }

    public void setText(@Nullable Component textIn) {
        this.text = textIn;
        if (textIn != null) {
            int textHeight = textIn.getString().split("\n").length * 10 + 2;
            this.hiddenHeight = Math.max(0, textHeight - this.height);
        } else {
            this.hiddenHeight = 0;
        }
    }

    @Override
    public void renderOffset(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        super.renderOffset(stackIn, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        if (this.text != null) {

            List<FormattedCharSequence> test = font.split(this.text, Integer.MAX_VALUE);
            for (int i = 0; i < test.size(); i++) {
                font.draw(stackIn, test.get(i), this.x + 2, this.y + 2 + i * 10, 0);
            }
        }
    }
}
