package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class TickButton extends AbstractTickButton {

    private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");

    public TickButton(int xIn, int yIn, Component title, @Nullable TickButton previous, IPressable onPress) {
        super(xIn, yIn, 22, 22, title, previous, onPress);
    }

    @Override
    public void renderButton(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BEACON_GUI_TEXTURES);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        // this.blit(stackIn, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        //  this.blit(stackIn, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

        short short1 = 219;
        int k = 0;

        if (!this.active)
            k += this.width * 2;
        else if (this.isHoveredOrFocused())
            k += this.width * 3;


        this.blit(stackIn, this.x, this.y, k, short1, this.width, this.height);

        if (this.ticked) {
            this.blit(stackIn, this.x + 2, this.y + 2, 90, 220, 18, 18);
        }

        this.renderBg(stackIn, minecraft, mouseX, mouseY);
        int j = getFGColor();
        this.drawCenteredString(stackIn, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
