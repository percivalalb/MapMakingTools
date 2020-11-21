package mapmakingtools.client.screen.widget;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mapmakingtools.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

public class ScrollPane extends Widget {

    protected final List<Widget> widgets;
    protected double scrollOffset = 0;
    protected int hiddenHeight;

    public ScrollPane(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, TextUtil.EMPTY);
        this.widgets = new ArrayList<>();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.clampScrollOffset(this.scrollOffset + amount * 15);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double changeX, double changeY) {
        if (super.mouseDragged(mouseX, mouseY, mouseButton, changeX, changeY)) {
            return true;
        }

        return false;
    }

    @Override
    public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        double scale = minecraft.getMainWindow().getGuiScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(this.x * scale), (int)((minecraft.getMainWindow().getScaledHeight() - this.height - this.y) * scale),
                (int)(this.width * scale), (int)(this.height * scale));

        RenderSystem.translated(0, this.scrollOffset, 0);

        this.renderOffset(stackIn, mouseX, mouseY, partialTicks);

        RenderSystem.translated(0, -this.scrollOffset, 0);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void renderOffset(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        for (Widget w : this.widgets) {
            w.render(stackIn, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        for (Widget w : this.widgets) {
            if (w.isMouseOver(mouseX, mouseY - this.scrollOffset)) {
                w.onClick(mouseX, mouseY - this.scrollOffset);
                break;
            }
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {

    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double changeX, double changeY) {
        this.clampScrollOffset(this.scrollOffset + changeY * 2);

        this.widgets.forEach(w -> {

        });
    }

    public void clampScrollOffset(double offset) {
        this.scrollOffset = MathHelper.clamp(offset, -this.hiddenHeight, 0);
    }
}
