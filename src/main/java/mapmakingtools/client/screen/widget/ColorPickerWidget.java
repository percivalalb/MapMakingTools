package mapmakingtools.client.screen.widget;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import mapmakingtools.util.TextUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;

public class ColorPickerWidget extends NestedWidget {

    private static final int[][] PRIMARY_COLORS = {{255,0,0},{255,255,0},{0,255,0},{0,255,255},{0,0,255},{255,0,255},{255,0,0}};
    private static final int[] BLACK = new int[] {0, 0, 0};
    private static final int[] WHITE = new int[] {255, 255, 255};

    protected BaseColorWidget baseColor;
    protected MainColorWidget mainColor;

    public ColorPickerWidget(int xIn, int yIn, int widthIn, int heightIn, @Nullable ColorPickerWidget previous) {
        super(xIn, yIn, widthIn, heightIn, TextUtil.EMPTY);

        this.baseColor = new BaseColorWidget(this.x, this.y + this.height - 8, this.width, 8, previous != null ? previous.baseColor : null);
        this.mainColor = new MainColorWidget(this.x, this.y, this.width, this.height - 11, previous != null ? previous.mainColor : null);
        this.children.add(this.baseColor);
        this.children.add(this.mainColor);
    }

    @Override
    public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        for (Widget widget : this.children) {
            widget.render(stackIn, mouseX, mouseY, partialTicks);
        }

        RenderSystem.disableTexture();
        this.drawSingleColor(stackIn, this.x + this.width + 5, this.y + 31, 20, 20, this.getColorPicked());

        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.fontRenderer;
        font.drawString(stackIn, "RBG: " + Arrays.toString(this.getColorPicked()), this.x + this.width + 5, this.y, 0);
        font.drawString(stackIn, "Base: " + Arrays.toString(this.getBaseColor()), this.x + this.width + 5, this.y + 11, 0);
        font.drawString(stackIn, "Hex: " + Util.toHex(this.getColorPicked()), this.x + this.width + 5, this.y + 20, 0);
    }

    public int[] getContrastColor(int[] color, int threshold) {
        int[] contrastColor = WHITE;

        // Calculate luminance to determine if to show white or black outline
        if (0.2126 * color[0] + 0.7152 * color[1] + 0.0722 * color[2] > threshold) {
            contrastColor = BLACK;
        }

        return contrastColor;
    }

    private void recalculateColor() {
        int[] intermediate = new int[3];

        for (int i = 0; i < 3; i++) {
            intermediate[i] = (int) ((this.baseColor.baseColor[i] + (255 - this.baseColor.baseColor[i]) * (1.0D - this.mainColor.distanceX)) * (1.0D - this.mainColor.distanceY));
        }

        this.mainColor.setMainColor(intermediate);
    }

    public int[] getColorPicked() {
        return this.mainColor.mainColor;
    }

    public int getColorPickedRGB() {
        return (this.mainColor.mainColor[0] << 16) + (this.mainColor.mainColor[1] << 8) + this.mainColor.mainColor[2];
    }

    public int[] getBaseColor() {
        return this.baseColor.baseColor;
    }

    public void setColor(int color) {
        int red = (color >> 16) & 255;
        int blue = (color >> 8) & 255;
        int green = color & 255;
        this.setColor(red, blue, green);
    }

    public void setColor(int red, int green, int blue) {
        this.setColor(new int[] {red, green, blue});
    }

    public void setColor(int[] rgb) {
        // If color is already set do not do anything as this will likely change the base color
        if (Arrays.equals(this.getColorPicked(), rgb)) {
            return;
        }

        int f = rgb[0] >= rgb[1] && rgb[0] >= rgb[2] ? 0 : rgb[1] >= rgb[0] && rgb[1] >= rgb[2] ? 1 : 2; // maxIndex
        int s = (f + 1) % 3;
        int t = (f + 2) % 3;

        if (rgb[s] >= rgb[t]) {
            int temp = s;
            s = t;
            t = temp;
        }

        int[] baseColor = new int[3];
        baseColor[f] = 255;
        baseColor[s] = rgb[s];
        baseColor[t] = 0;
        this.baseColor.setBaseColor(baseColor);
        this.mainColor.setMainColor(Arrays.copyOf(rgb, rgb.length));

        this.calculateMarkerPositions(f, s, t);
    }

    private void calculateMarkerPositions(int f, int s, int t) {
        this.baseColor.calculateMarkerPositions(f, s, t);
        this.mainColor.calculateMarkerPositions(f, s, t);
    }


    protected void testFillGradient(MatrixStack stackIn, int xIn, int yIn, int widthIn, int heightIn, int[] color) {
       Matrix4f matrix = stackIn.getLast().getMatrix();
       this.enableGradientDrawing();
       BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
       bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
       bufferbuilder.pos(matrix, xIn + widthIn, yIn, this.getBlitOffset()).color(color[0], color[1], color[2], 255).endVertex();
       bufferbuilder.pos(matrix, xIn, yIn, this.getBlitOffset()).color(255, 255, 255, 255).endVertex();
       bufferbuilder.pos(matrix, xIn, yIn + heightIn, this.getBlitOffset()).color(0, 0, 0, 255).endVertex();
       bufferbuilder.pos(matrix, xIn + widthIn, yIn + heightIn, this.getBlitOffset()).color(0, 0, 0, 255).endVertex();
       bufferbuilder.finishDrawing();
       WorldVertexBufferUploader.draw(bufferbuilder);
       this.disableGradientDrawing();
    }

    protected void drawSingleColor(MatrixStack stackIn, int xIn, int yIn, int widthIn, int heightIn, int[] color) {
        Matrix4f matrix = stackIn.getLast().getMatrix();
        this.enableGradientDrawing();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(xIn + widthIn, yIn, this.getBlitOffset()).color(color[0], color[1], color[2], 255).endVertex();
        bufferbuilder.pos(xIn, yIn, this.getBlitOffset()).color(color[0], color[1], color[2], 255).endVertex();
        bufferbuilder.pos(xIn, yIn + heightIn, this.getBlitOffset()).color(color[0], color[1], color[2], 255).endVertex();
        bufferbuilder.pos(xIn + widthIn, yIn + heightIn, this.getBlitOffset()).color(color[0], color[1], color[2], 255).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        this.disableGradientDrawing();
     }

    protected void fillGradientHorizontal(MatrixStack stackIn, int xIn, int yIn, int widthIn, int heightIn, int[] left, int[] right) {
        Matrix4f matrix = stackIn.getLast().getMatrix();
        this.enableGradientDrawing();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(xIn + widthIn, yIn, this.getBlitOffset()).color(right[0], right[1], right[2], 255).endVertex();
        bufferbuilder.pos(xIn, yIn, this.getBlitOffset()).color(left[0], left[1], left[2], 255).endVertex();
        bufferbuilder.pos(xIn, yIn + heightIn, this.getBlitOffset()).color(left[0], left[1], left[2], 255).endVertex();
        bufferbuilder.pos(xIn + widthIn, yIn + heightIn, this.getBlitOffset()).color(right[0], right[1], right[2], 255).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        this.disableGradientDrawing();
     }

    public void enableGradientDrawing() {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }

    public void disableGradientDrawing() {
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public class MainColorWidget extends Widget {

        private int[] mainColor = new int[] {255, 0, 0};
        private int[] contrastColor = WHITE;

        private double distanceX = 0;
        private double distanceY = 0;

        public MainColorWidget(int xIn, int yIn, int widthIn, int heightIn, @Nullable MainColorWidget previous) {
            super(xIn, yIn, widthIn, heightIn, new StringTextComponent("Main Color Picker"));

            if (previous != null) {
                this.mainColor = previous.mainColor;
                this.contrastColor = previous.contrastColor;
                this.distanceX = previous.distanceX;
                this.distanceY = previous.distanceY;
            }
        }

        @Override
        public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.disableTexture();
            ColorPickerWidget.this.testFillGradient(stackIn, this.x, this.y, this.width, this.height, ColorPickerWidget.this.getBaseColor());

            // Draw marker
            int markerX = this.x + (int)(this.width * this.distanceX) - 3;
            int markerY = this.y + (int)(this.height * this.distanceY) - 3;

            ColorPickerWidget.this.drawSingleColor(stackIn, markerX - 1, markerY - 1, 8, 8, this.contrastColor);
            ColorPickerWidget.this.drawSingleColor(stackIn, markerX, markerY, 6, 6, this.mainColor);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.distanceX = (mouseX - this.x) / this.width;
            this.distanceY = (mouseY - this.y) / this.height;
            this.distanceX =  MathHelper.clamp(this.distanceX, 0, 1.0D);
            this.distanceY = MathHelper.clamp(this.distanceY, 0, 1.0D);

            ColorPickerWidget.this.recalculateColor();
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double changeX, double changeY) {
            this.onClick(mouseX, mouseY);
        }

        public void setMainColor(int[] color) {
            this.mainColor = color;
            this.contrastColor = ColorPickerWidget.this.getContrastColor(this.mainColor, 110);
        }

        private void calculateMarkerPositions(int f, int s, int t) {
            if (this.mainColor[f] == 0) {
                this.distanceX = this.distanceY = 1.0D;
            } else {
                this.distanceY = this.mainColor[f] / 255D;
                this.distanceX = this.mainColor[t] / (255D * this.distanceY);

                this.distanceX = 1.0D - this.distanceX;
                this.distanceY = 1.0D - this.distanceY;
            }
        }

    }

    public class BaseColorWidget extends Widget {

        private int[] baseColor = new int[] {255, 0, 0};
        private int[] contrastColor = WHITE;

        private int indexIn = 0;
        private double distX = 0;

        public BaseColorWidget(int xIn, int yIn, int widthIn, int heightIn, @Nullable BaseColorWidget previous) {
            super(xIn, yIn, widthIn, heightIn, new StringTextComponent("Base Color Picker"));

            if (previous != null) {
                this.baseColor = previous.baseColor;
                this.contrastColor = previous.contrastColor;
                this.indexIn = previous.indexIn;
                this.distX = previous.distX;
            }
        }

        @Override
        public void renderButton(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.disableTexture();

            int w = this.width / (PRIMARY_COLORS.length - 1);

            for (int i = 0; i < PRIMARY_COLORS.length - 1; i++) {
                ColorPickerWidget.this.fillGradientHorizontal(stackIn, this.x + i * w, this.y, w, this.height, PRIMARY_COLORS[i], PRIMARY_COLORS[i + 1]);
            }

            int markerX = this.x + (int)((this.indexIn + this.distX) * w) - 3;
            int markerY = this.y + this.height - 8;

            ColorPickerWidget.this.drawSingleColor(stackIn, markerX - 1, markerY - 1, 7, 10, this.contrastColor);
            ColorPickerWidget.this.drawSingleColor(stackIn, markerX, markerY, 5, 8, this.baseColor);
        }


        @Override
        public void onClick(double mouseX, double mouseY) {
            int w = this.width / (PRIMARY_COLORS.length - 1);

            int index = MathHelper.floor((mouseX - this.x) / w);
            index = MathHelper.clamp(index, 0, PRIMARY_COLORS.length - 2);
            this.indexIn = index;

            double distance = (mouseX - this.x) / w  - index;
            distance = MathHelper.clamp(distance, 0, 1.0D);
            this.distX = distance;

            int[] intermediate = new int[3];
            for (int i = 0; i < 3; i++) {
                intermediate[i] = MathHelper.floor(PRIMARY_COLORS[index][i] + (PRIMARY_COLORS[index+1][i] - PRIMARY_COLORS[index][i]) * distance);
            }

            this.setBaseColor(intermediate);

            ColorPickerWidget.this.recalculateColor();
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double changeX, double changeY) {
            this.onClick(mouseX, mouseY);
        }

        public void setBaseColor(int[] color) {
            this.baseColor = color;
            this.contrastColor = ColorPickerWidget.this.getContrastColor(this.baseColor, 110);
        }

        private void calculateMarkerPositions(int f, int s, int t) {
            int index = 0;

            if (f == 0) {
                index = s == 1 ? 0 : 5;
            } else if (f == 1) {
                index = s == 0 ? 1 : 2;
            } else { // f == 2
                index = s == 0 ? 4 : 3;
            }
            this.indexIn = index;
            this.distX = this.baseColor[s] / 255D;
        }
    }
}
