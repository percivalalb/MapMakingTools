package mapmakingtools.client.screen.widget.scroll;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.client.screen.widget.ScrollPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public class TextScrollPane extends ScrollPane {

    @Nullable
    private ITextComponent text;

    public TextScrollPane(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }

    public void setText(@Nullable ITextComponent textIn) {
        this.text = textIn;
        if (textIn != null) {
            int textHeight = textIn.getString().split("\n").length * 10 + 2;
            this.hiddenHeight = Math.max(0, textHeight - this.height);
        } else {
            this.hiddenHeight = 0;
        }
    }

    @Override
    public void renderOffset(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        super.renderOffset(stackIn, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.fontRenderer;

        if (this.text != null) {
            String[] text = this.text.getString().split("\n");
            for (int i = 0; i < text.length; i++) {
                font.drawString(stackIn, text[i], this.x + 2, this.y + 2 + i * 10, 0);
            }
        }
    }
}
