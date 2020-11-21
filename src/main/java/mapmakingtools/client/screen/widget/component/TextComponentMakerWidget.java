package mapmakingtools.client.screen.widget.component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;

import mapmakingtools.client.screen.widget.NestedWidget;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.component.DraggableTextComponentPart.ComponentPart;
import mapmakingtools.client.screen.widget.component.DraggableTextComponentPart.StylePart;
import mapmakingtools.util.TextUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class TextComponentMakerWidget extends NestedWidget {

    @Nullable
    public DraggableTextComponentPart starting;
    @Nullable
    public DraggableTextComponentPart draggingPart;
    @Nullable
    private List<? extends Widget> editWidget;
    @Nullable
    public ITextComponent textComponent;

    public TextComponentMakerWidget(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, TextUtil.EMPTY);
        this.children.add(new SmallButton(this.x + 3, this.y + 15, 63, 12, new StringTextComponent("Text"), (btn) -> {
            this.addPart(new ComponentPart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 80, 20, new StringTextComponent("Comp Piece"), this));
        }));

//        this.children.add(new SmallButton(this.x + 18, this.y + 15, 13, 12, "N", (btn) -> {
//            this.addPart(new NewLinePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, "\\n", this));
//        }));

        this.children.add(new SmallButton(this.x + 68, this.y + 15, 63, 12, new StringTextComponent("Color"), (btn) -> {
            this.addPart(new StylePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, new StringTextComponent(TextFormatting.BLUE.getFriendlyName()), this, TextFormatting.BLUE));
        }));

        this.children.add(new SmallButton(this.x + 133, this.y + 15, 63, 12, new StringTextComponent("Format"), (btn) -> {
            this.addPart(new StylePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, new StringTextComponent(TextFormatting.ITALIC.getFriendlyName()), this, TextFormatting.ITALIC));
        }));
    }

    public boolean hasTextComponent() {
        return this.textComponent != null;
    }

    @Nullable
    public ITextComponent getTextComponent() {
        return this.textComponent;
    }

    public void createChild() {
        if (this.starting == null) return;

        try {
            ITextComponent component = this.starting.create();
            this.applyRecursive(component, this.starting, Sets.newHashSet(this.starting));
            this.textComponent = component;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void applyRecursive(ITextComponent textComponent, DraggableTextComponentPart part, Set<DraggableTextComponentPart> visited) {
        Collection<DraggableTextComponentPart> next = part.getConnections()
                .stream()
                .sorted(Comparator.comparingInt(DraggableTextComponentPart::applyOrder)) // Apply in the correct order
                .collect(Collectors.toList());

        for (DraggableTextComponentPart connectedParts : next) {
            if (visited.add(connectedParts)) {
                textComponent = connectedParts.apply(textComponent);
                this.applyRecursive(textComponent, connectedParts, visited);
            }
        }
    }

    public void applyToConnected(DraggableTextComponentPart parent, DraggableTextComponentPart part, Set<DraggableTextComponentPart> visited, BiConsumer<DraggableTextComponentPart, DraggableTextComponentPart> action) {
        action.accept(parent, part);

        Collection<DraggableTextComponentPart> next = part.getConnections()
                .stream()
                .sorted(Comparator.comparingInt(DraggableTextComponentPart::applyOrder)) // Apply in the correct order
                .collect(Collectors.toList());

        for (DraggableTextComponentPart connectedParts : next) {
            if (visited.add(connectedParts)) {
                this.applyToConnected(part, connectedParts, visited, action);
            }
        }
    }

    public void setEditWidgets(List<? extends Widget> editWidget) {
        if (this.editWidget != null) {
            this.children.removeAll(this.editWidget);
        }
        this.editWidget = editWidget;
        this.children.addAll(editWidget);
    }

    /**
     * Adds part and brings to top, follows the mouse
     * @param part
     */
    public void addPart(DraggableTextComponentPart part) {
        this.children.add(part);

        if (this.starting == null && part.canBeStartingPiece()) {
            this.starting = part;
        }
    }

    /**
     * @return If it was successfully brought to the top (could already have been there)
     */
    public boolean bringToTop(DraggableTextComponentPart part) {
        int index = this.children.indexOf(part);

        if (index >= 0) {
            if (index + 1 == this.children.size()) {
                return true;
            }

            // Removes it from the list and re-adds it at the end
            this.children.add(this.children.remove(index));

            return true;
        }

        return false;
    }

    public Optional<DraggableTextComponentPart> getPartAbove(double mouseX, double mouseY) {
        for (IGuiEventListener iguieventlistener : this.getEventListeners()) {
            if (iguieventlistener instanceof DraggableTextComponentPart && iguieventlistener.isMouseOver(mouseX, mouseY)) {
               return Optional.of((DraggableTextComponentPart) iguieventlistener);
            }
         }

         return Optional.empty();
    }

    public boolean connectToClosest(DraggableTextComponentPart part) {
        // Check disconnections
        Collection<Entry<Direction, DraggableTextComponentPart>> connections = part.getConnectionEntries();

        for (Entry<Direction, DraggableTextComponentPart> oldConnections : connections) {
            Direction dir = oldConnections.getKey();
            DraggableTextComponentPart otherPart = oldConnections.getValue();
            if (getDistanceBetween(part, otherPart, dir) >= 244D) {
                part.removeConnection(dir);
                otherPart.removeConnection(dir.getOpposite());
            }
        }

        Optional<Pair<DraggableTextComponentPart, Direction>> closets = this.getClosest(part);
        if (closets.isPresent()) {
            Pair<DraggableTextComponentPart, Direction> pair = closets.get();
            DraggableTextComponentPart otherPart = pair.getLeft();
            if (otherPart != null) {
                part.connectTo(otherPart, pair.getRight());
                otherPart.connectTo(part, pair.getRight().getOpposite());

                this.snapToPosition(part, otherPart, pair.getRight());
            }
        }

        return false;
    }

    public void snapToPosition(DraggableTextComponentPart part, DraggableTextComponentPart otherPart, Direction dir) {
        // Snaps the part that was moved to the same level

        int PADDING = 5;

        switch(dir) {
        case NORTH:
            part.x = otherPart.x + otherPart.getWidth() / 2 - part.getWidth() / 2;
            part.y = otherPart.y - part.getHeightRealms() - PADDING;
            break;
        case EAST:
            part.x = otherPart.x + otherPart.getWidth() + PADDING;
            part.y = otherPart.y + otherPart.getHeightRealms() / 2 - part.getHeightRealms() / 2;
            break;
        case SOUTH:
            part.x = otherPart.x + otherPart.getWidth() / 2 - part.getWidth() / 2;
            part.y = otherPart.y + otherPart.getHeightRealms() + PADDING;
            break;
        case WEST:
            part.x = otherPart.x - part.getWidth() - PADDING;
            part.y = otherPart.y + otherPart.getHeightRealms() / 2 - part.getHeightRealms() / 2;
            break;
        default:
            break;
        }
    }

    @Nullable
    public Optional<Pair<DraggableTextComponentPart, Direction>> getClosest(DraggableTextComponentPart part) {
        List<DraggableTextComponentPart> parts = this.children.stream()
          .filter(m -> m instanceof DraggableTextComponentPart)
          .filter(m -> m != part)
          .map(m -> (DraggableTextComponentPart)m)
          .collect(Collectors.toList());

        for (Direction dir : Util.HORIZONTAL_DIRECTIONS) {
            Optional<DraggableTextComponentPart> closest = this.getClosestOnSide(part, parts, dir);
            if (closest.isPresent()) {
                return Optional.of(Pair.of(closest.get(), dir));
            }
        }

        return Optional.empty();
    }

    public Optional<DraggableTextComponentPart> getClosestOnSide(DraggableTextComponentPart part, List<DraggableTextComponentPart> parts, Direction dir) {
        return parts.stream()
                .sorted(Comparator.comparingDouble(otherPart -> getDistanceBetween(part, otherPart, dir)))
                .filter(d -> getDistanceBetween(part, d, dir) < 244D)
                .filter(d -> part.canConnectTo(d, dir))
                .findFirst();
    }

    public double getDistanceBetween(DraggableTextComponentPart part, DraggableTextComponentPart otherPair, Direction direction) {
        switch(direction) {
        case SOUTH:
            return Math.pow(part.x + part.getWidth() / 2 - otherPair.x - otherPair.getWidth() / 2, 2)
                    + Math.pow(part.y - otherPair.y - otherPair.getHeightRealms(), 2);
        case WEST:
            return Math.pow(part.x + part.getWidth() - otherPair.x, 2)
                    + Math.pow(part.y + part.getHeightRealms() / 2 - otherPair.y - otherPair.getHeightRealms() / 2, 2) ;
        case NORTH:
            return Math.pow(part.x + part.getWidth() / 2 - otherPair.x - otherPair.getWidth() / 2, 2)
                    + Math.pow(part.y + part.getHeightRealms() - otherPair.y, 2);
        case EAST:
            return Math.pow(part.x - otherPair.x - otherPair.getWidth(), 2)
                    + Math.pow(part.y + part.getHeightRealms() / 2 - otherPair.y - otherPair.getHeightRealms() / 2, 2) ;
        default:
            return Double.MAX_VALUE;
        }
    }

    @Override
    public void render(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        //super.render(mouseX, mouseY, partialTicks);
        for (Widget part : this.children) {
            part.render(stackIn, mouseX, mouseY, partialTicks);
        }
        this.createChild();
        if (this.hasTextComponent()) {
            Minecraft mc = Minecraft.getInstance();
            FontRenderer font = mc.fontRenderer;

            String[] text = this.textComponent.getString().split("\n");
            for (int i = 0; i < text.length; i++) {
                font.drawString(stackIn, text[i], this.x + 10, this.y + 30 + i * 10, 0);
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {

    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double changeX, double changeY) {

    }

    public void populateFrom(ITextComponent textComponent) {
        textComponent.getSiblings();
    }
}
