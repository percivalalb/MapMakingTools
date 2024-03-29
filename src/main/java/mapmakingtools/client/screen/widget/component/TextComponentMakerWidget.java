package mapmakingtools.client.screen.widget.component;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import mapmakingtools.client.screen.widget.NestedWidget;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.client.screen.widget.component.DraggableTextComponentPart.ComponentPart;
import mapmakingtools.client.screen.widget.component.DraggableTextComponentPart.StylePart;
import mapmakingtools.util.TextUtil;
import mapmakingtools.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TextComponentMakerWidget extends NestedWidget {

    @Nullable
    public DraggableTextComponentPart starting;
    @Nullable
    public DraggableTextComponentPart draggingPart;
    @Nullable
    private List<? extends AbstractWidget> editWidget;
    @Nullable
    public Component textComponent;

    public TextComponentMakerWidget(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, TextUtil.EMPTY);
        this.children.add(new SmallButton(this.x + 3, this.y + 15, 63, 12, new TextComponent("Text"), (btn) -> {
            this.addPart(new ComponentPart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 80, 20, new TextComponent("Comp Piece"), this));
        }));

//        this.children.add(new SmallButton(this.x + 18, this.y + 15, 13, 12, "N", (btn) -> {
//            this.addPart(new NewLinePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, "\\n", this));
//        }));

        this.children.add(new SmallButton(this.x + 68, this.y + 15, 63, 12, new TextComponent("Color"), (btn) -> {
            this.addPart(new StylePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, new TextComponent(ChatFormatting.BLUE.getName()), this, ChatFormatting.BLUE));
        }));

        this.children.add(new SmallButton(this.x + 133, this.y + 15, 63, 12, new TextComponent("Format"), (btn) -> {
            this.addPart(new StylePart(x + (int) (Math.random() * 150), y + (int) (Math.random() * 150), 20, 20, new TextComponent(ChatFormatting.ITALIC.getName()), this, ChatFormatting.ITALIC));
        }));
    }

    public boolean hasTextComponent() {
        return this.textComponent != null;
    }

    @Nullable
    public Component getTextComponent() {
        return this.textComponent;
    }

    public void createChild() {
        if (this.starting == null) return;

        try {
            Component component = this.starting.create();
            this.applyRecursive(component, this.starting, Sets.newHashSet(this.starting));
            this.textComponent = component;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void applyRecursive(Component textComponent, DraggableTextComponentPart part, Set<DraggableTextComponentPart> visited) {
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

    public void setEditWidgets(List<? extends AbstractWidget> editWidget) {
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
        for (GuiEventListener iguieventlistener : this.children()) {
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
            part.y = otherPart.y - part.getHeight() - PADDING;
            break;
        case EAST:
            part.x = otherPart.x + otherPart.getWidth() + PADDING;
            part.y = otherPart.y + otherPart.getHeight() / 2 - part.getHeight() / 2;
            break;
        case SOUTH:
            part.x = otherPart.x + otherPart.getWidth() / 2 - part.getWidth() / 2;
            part.y = otherPart.y + otherPart.getHeight() + PADDING;
            break;
        case WEST:
            part.x = otherPart.x - part.getWidth() - PADDING;
            part.y = otherPart.y + otherPart.getHeight() / 2 - part.getHeight() / 2;
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
                    + Math.pow(part.y - otherPair.y - otherPair.getHeight(), 2);
        case WEST:
            return Math.pow(part.x + part.getWidth() - otherPair.x, 2)
                    + Math.pow(part.y + part.getHeight() / 2 - otherPair.y - otherPair.getHeight() / 2, 2) ;
        case NORTH:
            return Math.pow(part.x + part.getWidth() / 2 - otherPair.x - otherPair.getWidth() / 2, 2)
                    + Math.pow(part.y + part.getHeight() - otherPair.y, 2);
        case EAST:
            return Math.pow(part.x - otherPair.x - otherPair.getWidth(), 2)
                    + Math.pow(part.y + part.getHeight() / 2 - otherPair.y - otherPair.getHeight() / 2, 2) ;
        default:
            return Double.MAX_VALUE;
        }
    }

    @Override
    public void render(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        //super.render(mouseX, mouseY, partialTicks);
        for (AbstractWidget part : this.children) {
            part.render(stackIn, mouseX, mouseY, partialTicks);
        }
        this.createChild();
        if (this.hasTextComponent()) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            String[] text = this.textComponent.getString().split("\n");
            for (int i = 0; i < text.length; i++) {
                font.draw(stackIn, text[i], this.x + 10, this.y + 30 + i * 10, 0);
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

    public void populateFrom(Component textComponent) {
        textComponent.getSiblings();
    }
}
