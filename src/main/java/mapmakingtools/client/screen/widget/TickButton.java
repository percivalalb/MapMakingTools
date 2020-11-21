package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class TickButton extends AbstractTickButton {

    private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");

    public TickButton(int xIn, int yIn, String text, @Nullable TickButton previous, IPressable onPress) {
        super(xIn, yIn, 22, 22, text, previous, onPress);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
       // this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
      //  this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

        short short1 = 219;
        int k = 0;

        if (!this.active)
            k += this.width * 2;
        else if (this.isHovered())
            k += this.width * 3;


        this.blit(this.x, this.y, k, short1, this.width, this.height);

        if (this.ticked) {
            this.blit(this.x + 2, this.y + 2, 90, 220, 18, 18);
        }

        this.renderBg(minecraft, mouseX, mouseY);
        int j = getFGColor();
        this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
