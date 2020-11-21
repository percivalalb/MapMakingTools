package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SmallToggleButton<T> extends ToggleButton<T> {

    private Function<T, String> displayFunc;

    public SmallToggleButton(int xIn, int yIn, int widthIn, int heightIn, T[] values, Function<T, String> displayFunc, @Nullable ToggleButton<T> previous, IPressable onPress) {
        super(xIn, yIn, widthIn, heightIn, "", values, previous, onPress);
        this.displayFunc = displayFunc;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.blit(this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2);//top left
        this.blit(this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2);//top right
        this.blit(this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
        this.blit(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right

        this.renderBg(minecraft, mouseX, mouseY);
        int j = getFGColor();
        this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    public String getMessage() {
        return this.displayFunc.apply(this.getValue());
    }
}
