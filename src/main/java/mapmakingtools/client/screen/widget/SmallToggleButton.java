package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Function;

import net.minecraft.client.gui.components.Button.OnPress;

public class SmallToggleButton<T> extends ToggleButton<T> {

    private Function<T, Component> displayFunc;

    public SmallToggleButton(int xIn, int yIn, int widthIn, int heightIn, T[] values, Function<T, Component> displayFunc, @Nullable ToggleButton<T> previous, OnPress onPress) {
        this(xIn, yIn, widthIn, heightIn, values, displayFunc, previous, onPress, Button.NO_TOOLTIP);
    }

    public SmallToggleButton(int xIn, int yIn, int widthIn, int heightIn, T[] values, Function<T, Component> displayFunc, @Nullable ToggleButton<T> previous, OnPress onPress, Button.OnTooltip onTooltip) {
        super(xIn, yIn, widthIn, heightIn, TextUtil.EMPTY, values, previous, onPress, onTooltip);
        this.displayFunc = displayFunc;
    }

    @Override
    public void renderButton(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.blit(stackIn, this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2);//top left
        this.blit(stackIn, this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2);//top right
        this.blit(stackIn, this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
        this.blit(stackIn, this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right

        this.renderBg(stackIn, minecraft, mouseX, mouseY);
        int j = getFGColor();
        GuiComponent.drawCenteredString(stackIn, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        RenderSystem.enableDepthTest();
    }

    @Override
    public Component getMessage() {
        return this.displayFunc.apply(this.getValue());
    }
}
