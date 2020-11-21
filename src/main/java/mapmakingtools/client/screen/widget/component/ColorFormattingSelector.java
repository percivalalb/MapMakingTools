package mapmakingtools.client.screen.widget.component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import mapmakingtools.lib.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ColorFormattingSelector extends Widget {

    private static final List<TextFormatting> COLORS = Util.make(() -> Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).collect(Collectors.toList()));

    private Consumer<TextFormatting> action;

    public ColorFormattingSelector(int xIn, int yIn, Consumer<TextFormatting> action) {
        super(xIn, yIn, COLORS.size() * 20, 20, new StringTextComponent("Color Formatting Selector"));
        this.action = action;
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        int index = (int) ((mouseX - this.x) / 20);
        index = MathHelper.clamp(index, 0, COLORS.size() - 1);
        this.action.accept(COLORS.get(index));
     }

    @Override
    public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(Resources.BUTTON_TEXT_COLOR);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        for (int j = 0; j < COLORS.size(); j++) {
            TextFormatting formatting = COLORS.get(j);
            int color = formatting.getColor();
            int red = (color >> 16) & 255;
            int blue = (color >> 8) & 255;
            int green = color & 255;
            RenderSystem.color4f(red / 255F, blue / 255F, green / 255F, 1.0F);

            int i = this.getYImage(this.isHovered() && mouseX >= this.x + j * 20 && mouseY >= this.y && mouseX < this.x + (j + 1) * 20 && mouseY < this.y + this.height);

            this.blit(stackIn, this.x + j * 20, y, 0, 46 + i * 20, 10, this.height / 2);//top left
            this.blit(stackIn, this.x + j * 20 + 10, y, 200 - 10, 46 + i * 20, 10, this.height / 2);//top right
            this.blit(stackIn, this.x + j * 20, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom left
            this.blit(stackIn, this.x + j * 20 + 10, y + this.height / 2, 200 - 10, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom right
        }
    }
}
