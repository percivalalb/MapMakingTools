package mapmakingtools.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import net.minecraft.client.gui.widget.button.Button.IPressable;

public class SmallButton extends Button {

    public SmallButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent title, IPressable onPress) {
        this(xIn, yIn, widthIn, heightIn, title, onPress, Button.NO_TOOLTIP);
    }

    public SmallButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent title, IPressable onPress, Button.ITooltip onTooltip) {
        super(xIn, yIn, widthIn, heightIn, title, onPress, onTooltip);
    }

    @Override
    public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        this.blit(stackIn, this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2);//top left
        this.blit(stackIn, this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2);//top right
        this.blit(stackIn, this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom left
        this.blit(stackIn, this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2);//bottom right

        this.renderBg(stackIn, minecraft, mouseX, mouseY);
        int j = getFGColor();
        AbstractGui.drawCenteredString(stackIn, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
     }
}
