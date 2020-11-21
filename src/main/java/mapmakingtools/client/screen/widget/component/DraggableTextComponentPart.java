package mapmakingtools.client.screen.widget.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.ToggleButton;
import mapmakingtools.client.screen.widget.WidgetUtil;
import mapmakingtools.lib.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public abstract class DraggableTextComponentPart extends Widget {

    protected TextComponentMakerWidget parent;
    private List<? extends Widget> editWidget;

    public DraggableTextComponentPart(int xIn, int yIn, int widthIn, int heightIn, String msg, TextComponentMakerWidget parent) {
        super(xIn, yIn, widthIn, heightIn, msg);
        this.parent = parent;
    }

    public final List<? extends Widget> getOrCreateEditWidget() {
        if (this.editWidget == null) {
            this.editWidget = this.createEditWidget();
        }

        return this.editWidget;
    }

    public abstract List<? extends Widget> createEditWidget();

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        // Check disconnections
        Collection<Entry<Direction, DraggableTextComponentPart>> connections = this.getConnectionEntries();

        RenderSystem.pushMatrix();

        for (Entry<Direction, DraggableTextComponentPart> connection : connections) {
            Direction dir = connection.getKey();
            DraggableTextComponentPart otherPart = connection.getValue();

            Vec2f thisSide = WidgetUtil.getCentreOfSide(this, dir.getOpposite());
            Vec2f otherSide = WidgetUtil.getCentreOfSide(otherPart, dir);

            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            GL11.glLineWidth(2.5F);

            bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(thisSide.x, thisSide.y, 0).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(otherSide.x, otherSide.y, 0).color(0, 0, 0, 255).endVertex();

            bufferbuilder.finishDrawing();
            RenderSystem.enableAlphaTest();
            WorldVertexBufferUploader.draw(bufferbuilder);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.parent.draggingPart = this;
        this.parent.bringToTop(this);
        this.parent.setEditWidgets(this.getOrCreateEditWidget());
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double changeX, double changeY) {
        int changeX1 = (int) mouseX - this.getWidth() / 2 - this.x;
        int changeY1 = (int) mouseY - this.getHeight() / 2 - this.y;

        BiConsumer<DraggableTextComponentPart, DraggableTextComponentPart> action = (parent, widget) -> {
            if (parent != null) {
                widget.parent.snapToPosition(widget, parent, parent.getSide(widget).getOpposite());
            } else {
                widget.x += changeX1;
                widget.y += changeY1;
            }

            widget.x = MathHelper.clamp(widget.x, widget.parent.x, widget.parent.x + widget.parent.getWidth() - widget.getWidth());
            widget.y = MathHelper.clamp(widget.y, widget.parent.y, widget.parent.y + widget.parent.getHeight() - widget.getHeight() - 29);
        };

        if (Screen.hasShiftDown()) {
            this.parent.applyToConnected(null, this, Sets.newHashSet(), action);
        } else {
            action.accept(null, this);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.parent.connectToClosest(this);
    }

    public Map<Direction, DraggableTextComponentPart> connections = Maps.newEnumMap(Direction.class);
    protected void connectTo(DraggableTextComponentPart otherPart, Direction dir) {
        this.connections.put(dir, otherPart);
    }

    public Direction getSide(DraggableTextComponentPart otherPart) {
       for (Entry<Direction, DraggableTextComponentPart> da : this.connections.entrySet()) {
           if (da.getValue() == otherPart) {
               return da.getKey();
           }
       }

       return null;
    }

    public Set<Entry<Direction, DraggableTextComponentPart>> getConnectionEntries() {
        return this.connections.entrySet();
    }

    public Collection<DraggableTextComponentPart> getConnections() {
        return this.connections.values();
    }

    public void removeConnection(Direction key) {
        this.connections.remove(key);

    }

    public abstract boolean canConnectTo(DraggableTextComponentPart otherPart, Direction direction);

    public abstract ITextComponent create();

    public abstract int applyOrder();

    public abstract ITextComponent apply(ITextComponent textComponent);

    public boolean canBeStartingPiece() {
        return true;
    }

    public static class StylePart extends DraggableTextComponentPart {

        private TextFormatting color;

        public StylePart(int xIn, int yIn, int widthIn, int heightIn, String msg, TextComponentMakerWidget parent, TextFormatting color) {
            super(xIn, yIn, widthIn, heightIn, msg, parent);
            this.color = color;
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float partialTicks) {
            super.renderButton(mouseX, mouseY, partialTicks);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (this.color.isColor()) {
                int color = this.color.getColor();
                int red = (color >> 16) & 255;
                int blue = (color >> 8) & 255;
                int green = color & 255;
                RenderSystem.color4f(red / 255F, blue / 255F, green / 255F, 1.0F);

                minecraft.getTextureManager().bindTexture(Resources.BUTTON_TEXT_COLOR);
                this.blit(this.x, y, 0, 46 + i * 20, 10, this.height / 2);//top left
                this.blit(this.x + 10, y, 200 - 10, 46 + i * 20, 10, this.height / 2);//top right
                this.blit(this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom left
                this.blit(this.x + 10, y + this.height / 2, 200 - 10, 46 + i * 20 + 20 - this.height / 2, 10, this.height / 2);//bottom right
            } else {
                minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
                this.blit(this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2); //top left
                this.blit(this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2); //top right
                this.blit(this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom left
                this.blit(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom right
            }

            this.renderBg(minecraft, mouseX, mouseY);
            int j = getFGColor();


            if (!this.color.isColor()) {
                this.drawCenteredString(fontrenderer, this.getLabel(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            } else {
               // this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            }
        }

        public String getLabel() {
            return this.getLabel(this.color);
        }

        public String getLabel(TextFormatting formattingIn) {
            if (formattingIn.isFancyStyling()) {
                switch (formattingIn) {
                    case BOLD: return TextFormatting.BOLD + "B";
                    case STRIKETHROUGH: return TextFormatting.STRIKETHROUGH + "S";
                    case UNDERLINE: return TextFormatting.UNDERLINE + "U";
                    case ITALIC: return TextFormatting.BOLD + "I";
                    case OBFUSCATED: return TextFormatting.OBFUSCATED + "O";
                }

            }

            return "?";
        }

        @Override
        public boolean canConnectTo(DraggableTextComponentPart otherPart, Direction direction) {
            if (otherPart instanceof StylePart) {
                StylePart otherStyle = (StylePart)otherPart;
                if (otherStyle.color.isColor() && this.color.isColor() || (this.color == otherStyle.color && otherStyle.color != TextFormatting.RESET)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public ITextComponent create() {
            return apply(new StringTextComponent(""));
        }


        @Override
        public int applyOrder() {
            return this.color == TextFormatting.RESET ? -1 : 0;
        }

        @Override
        public ITextComponent apply(ITextComponent textComponent) {
            return textComponent.applyTextStyle(this.color);
        }

        @Override
        public boolean canBeStartingPiece() {
            return false;
        }

        @Override
        public List<? extends Widget> createEditWidget() {
            if (this.color.isColor()) {
                ColorFormattingSelector widget = new ColorFormattingSelector(this.parent.x + (this.parent.getWidth() - 320) / 2, this.parent.y + this.parent.getHeight() - 25, (formatting) -> {
                    this.color = formatting;
                });

                return Collections.unmodifiableList(Lists.newArrayList(widget));
            } else {
                List<Widget> options = new ArrayList<>();
                int i = 0;
                for (TextFormatting formatting : TextFormatting.values()) {
                    if (formatting.isColor()) {
                        continue;
                    }

                    options.add(new SmallButton(this.parent.x + (this.parent.getWidth() - 200) / 2 + 22 * options.size(), this.parent.y + this.parent.getHeight() - 25, 20, 20, this.getLabel(formatting), (btn) -> {
                        this.color = formatting;
                    }));
                    i++;
                }

                return Collections.unmodifiableList(options);
            }
        }
    }

    public static class ComponentPart extends DraggableTextComponentPart {

        private boolean translation = false;
        private String text = "";

        public ComponentPart(int xIn, int yIn, int widthIn, int heightIn, String msg, TextComponentMakerWidget parent) {
            super(xIn, yIn, widthIn, heightIn, msg, parent);
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float partialTicks) {
            super.renderButton(mouseX, mouseY, partialTicks);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.blit(this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2); //top left
            this.blit(this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2); //top right
            this.blit(this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom left
            this.blit(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom right

            this.renderBg(minecraft, mouseX, mouseY);
            int j = getFGColor();
            this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }

        @Override
        public boolean canConnectTo(DraggableTextComponentPart otherPart, Direction direction) {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ITextComponent create() {
            if (!this.translation) {
                return new StringTextComponent(this.text);
            } else {
                return new TranslationTextComponent(this.text);
            }
        }

        @Override
        public int applyOrder() {
            return 1;
        }

        @Override
        public ITextComponent apply(ITextComponent textComponent) {
            ITextComponent comp = this.create();
            textComponent.appendSibling(comp);
            return comp;
        }

        @Override
        public List<? extends Widget> createEditWidget() {
            TextFieldWidget widget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, this.parent.x + (this.parent.getWidth() - 200) / 2, this.parent.y + this.parent.getHeight() - 25, 200, 20, "");
            widget.setResponder((str) -> {
                this.text = str;
            });
            ToggleButton<Boolean> toggleButton = new ToggleButton<>(this.parent.x + this.parent.getWidth() / 2 - 120 - 15, this.parent.y + this.parent.getHeight() - 25, 30, 20, "Exact", new Boolean[] {true, false}, null, (btn) -> {
                this.translation = ((ToggleButton<Boolean>) btn).getValue();
                if (this.translation) {
                    btn.setMessage("Trans");
                } else {
                    btn.setMessage("Exact");
                }
            });
            return Collections.unmodifiableList(Lists.newArrayList(widget, toggleButton));
        }
    }

    public static class NewLinePart extends DraggableTextComponentPart {

        public NewLinePart(int xIn, int yIn, int widthIn, int heightIn, String msg, TextComponentMakerWidget parent) {
            super(xIn, yIn, widthIn, heightIn, msg, parent);
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float partialTicks) {
            super.renderButton(mouseX, mouseY, partialTicks);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.blit(this.x, y, 0, 46 + i * 20, this.width / 2, this.height / 2); //top left
            this.blit(this.x + this.width / 2, y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height / 2); //top right
            this.blit(this.x, y + this.height / 2, 0, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom left
            this.blit(this.x + this.width / 2, y + this.height / 2, 200 - this.width / 2, 46 + i * 20 + 20 - this.height / 2, this.width / 2, this.height / 2); //bottom right

            this.renderBg(minecraft, mouseX, mouseY);
            int j = getFGColor();
            this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }

        @Override
        public boolean canConnectTo(DraggableTextComponentPart otherPart, Direction direction) {
            return otherPart instanceof NewLinePart || otherPart instanceof ComponentPart;
        }

        @Override
        public ITextComponent create() {
            return new StringTextComponent("\n");
        }

        @Override
        public int applyOrder() {
            return 1;
        }

        @Override
        public ITextComponent apply(ITextComponent textComponent) {
            ITextComponent comp = this.create();
            textComponent.appendSibling(comp);
            return comp;
        }

        @Override
        public List<? extends Widget> createEditWidget() {
            TextFieldWidget widget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, this.parent.x + (this.parent.getWidth() - 200) / 2, this.parent.y + this.parent.getHeight() - 25, 200, 20, "");
            widget.setResponder((str) -> {});
            return Collections.unmodifiableList(Lists.newArrayList(widget));
        }
    }
}
