package mapmakingtools.client.screen.widget.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.lib.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.Util;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ColorFormattingSelector extends AbstractWidget {

    private static final List<ChatFormatting> COLORS = Util.make(() -> Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(Collectors.toList()));

    private Consumer<ChatFormatting> action;

    public ColorFormattingSelector(int xIn, int yIn, Consumer<ChatFormatting> action) {
        super(xIn, yIn, COLORS.size() * 20, 20, new TextComponent("Color Formatting Selector"));
        this.action = action;
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        int index = (int) ((mouseX - this.x) / 20);
        index = Mth.clamp(index, 0, COLORS.size() - 1);
        this.action.accept(COLORS.get(index));
     }

    @Override
    public void renderButton(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Resources.BUTTON_TEXT_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        for (int j = 0; j < COLORS.size(); j++) {
            ChatFormatting formatting = COLORS.get(j);
            int color = formatting.getColor();
            int red = (color >> 16) & 255;
            int blue = (color >> 8) & 255;
            int green = color & 255;
            RenderSystem.setShaderColor(red / 255F, blue / 255F, green / 255F, 1.0F);

            int i = this.getYImage(this.isHovered() && mouseX >= this.x + j * 20 && mouseY >= this.y && mouseX < this.x + (j + 1) * 20 && mouseY < this.y + this.height);

            this.blit(stackIn, this.x + j * 20, y, 0, 46 + i * 20, 10, this.height / 2);//top left
            this.blit(stackIn, this.x + j * 20 + 10, y, 200 - 10, 46 + i * 20, 10, this.height / 2);//top right
            this.blit(stackIn, this.x + j * 20, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom left
            this.blit(stackIn, this.x + j * 20 + 10, y + this.height / 2, 200 - 10, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom right
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        // TODO
    }
}
