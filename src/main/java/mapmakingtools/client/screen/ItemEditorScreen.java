package mapmakingtools.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mapmakingtools.MMTRegistries;
import mapmakingtools.MapMakingTools;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.IItemAttributeClient;
import mapmakingtools.api.itemeditor.Registries;
import mapmakingtools.api.util.IFeatureState;
import mapmakingtools.api.util.State;
import mapmakingtools.client.screen.widget.SmallButton;
import mapmakingtools.lib.Resources;
import mapmakingtools.network.PacketItemEditorUpdate;
import mapmakingtools.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ItemEditorScreen extends Screen {

    private static final int attributeBackgroundColour = new Color(255, 255, 255, 75).getRGB();
    private static final int TAB_HEIGHT = 15;

    private int currentPage = 0;
    private int maxPages = 1;
    private static Optional<Pair<IRegistryDelegate<IItemAttribute>, IItemAttributeClient>> current = Optional.empty();
    private List<Widget> attributeWidgets = Lists.newArrayList();
    private Map<IRegistryDelegate<IItemAttribute>, Widget> attributeTabWidgets = new HashMap<>();
    private int guiX, guiY, guiWidth, guiHeight;
    private long blockUpdateTill = 0;
    private boolean requiresUpdate = false;

    private PlayerEntity player;
    private ItemStack stack;
    private int slotIndex;
    private List<IRegistryDelegate<IItemAttribute>> itemList;

    private Button leftBtn, rightBtn;

    public ItemEditorScreen(PlayerEntity player, int slotIndex, ItemStack stack) {
        super(new TranslationTextComponent("screen.mapmakingtools.item_editor"));
        this.player = player;
        this.stack = stack;
        this.slotIndex = slotIndex;
        this.itemList = Util.getDelegates(Registries.ITEM_ATTRIBUTES, IFeatureState::isVisible);
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.guiX = 150;
        this.guiY = 13;
        this.guiWidth = this.width - this.guiX - 13;
        this.guiHeight = this.height - this.guiY * 2;

        int size = this.itemList.size();
        int perPage = Math.max(MathHelper.floor((this.height - 42 - 3) / (double) TAB_HEIGHT), 1);

        if (perPage < size) {
            this.leftBtn = new Button(49, 18, 20, 20, new StringTextComponent("<"), (btn) -> {
                this.currentPage = Math.max(0, this.currentPage - 1);
                btn.active = this.currentPage > 0;
                this.rightBtn.active = true;
                this.recalculatePage(perPage, false);
            });
            this.leftBtn.active = false;

            this.rightBtn = new Button(72, 18, 20, 20, new StringTextComponent(">"), (btn) -> {
                this.currentPage = Math.min(this.maxPages - 1, this.currentPage + 1);
                btn.active = this.currentPage < this.maxPages - 1;
                this.leftBtn.active = true;
                this.recalculatePage(perPage, false);
            });

            this.addButton(this.leftBtn);
            this.addButton(this.rightBtn);
        }

        this.recalculatePage(perPage, true);

        ItemEditorScreen.current.ifPresent(p -> {
            // If old selected attribute is no longer applicable delete
            if (!p.getLeft().get().canUse() || !p.getLeft().get().isApplicable(this.player, this.stack)) {
                ItemEditorScreen.current = Optional.empty();
                return;
            }

            ItemEditorScreen.this.selectAttribute(p);
        });
    }

    @Override
    public void tick() {
        long systemTimeMillis = net.minecraft.util.Util.milliTime();

        ItemEditorScreen.current.ifPresent(p -> {
            p.getRight().tick(this, systemTimeMillis, this::sendItemUpdate);
        });

        ItemStack playersStack = this.player.inventory.getStackInSlot(this.slotIndex);
        if (!ItemStack.areItemStacksEqual(playersStack, this.stack)) {
            this.requiresUpdate = ItemEditorScreen.current.isPresent() ? this.current.get().getRight().requiresUpdate(playersStack, this.stack) : false;
            this.stack = playersStack.copy();
            for (Entry<IRegistryDelegate<IItemAttribute>, Widget> attributeBtns : this.attributeTabWidgets.entrySet()) {
                IItemAttribute iAttr = attributeBtns.getKey().get();
                attributeBtns.getValue().active = iAttr.canUse() && iAttr.isApplicable(this.player, this.stack);
            }
        }

        if (this.requiresUpdate && systemTimeMillis >= this.blockUpdateTill) {
            ItemEditorScreen.current.ifPresent(p -> {
                p.getRight().populateFrom(this, this.stack);
            });
            this.requiresUpdate = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (super.mouseScrolled(mouseX, mouseY, amount)) {
            return true;
        }

        // If

        if (Util.isPointInRegion(23, 23, 16, 16, mouseX, mouseY)) {
            int original = this.slotIndex;

            do {
                int direction = MathHelper.signum(amount);
                this.slotIndex += direction + this.player.inventory.getSizeInventory();
                this.slotIndex %= this.player.inventory.getSizeInventory();

                ItemStack stack = this.player.inventory.getStackInSlot(this.slotIndex);
                if (!stack.isEmpty()) {
                    break;
                }
            }
            while (original != this.slotIndex);

            return true;
        }

        return false;
     }

    public void selectAttribute(Pair<IRegistryDelegate<IItemAttribute>, IItemAttributeClient> attribute) {
        ItemEditorScreen.current.ifPresent(p -> {
            this.clearAttribute(p);
        });

        attribute.getRight().init(this, this::addAttributeWidget, this::sendItemUpdate, this::pauseStackUpdatesFor, () -> this.stack, this.guiX, this.guiY, this.guiWidth, this.guiHeight);

        // TODO Populate GUI from item stack
        attribute.getRight().populateFrom(this, this.stack);
    }

    public void clearAttribute(@Nullable Pair<IRegistryDelegate<IItemAttribute>, IItemAttributeClient> attribute) {
        this.attributeWidgets.forEach(this::removeWidget);
        this.attributeWidgets.clear(); // Remove widget from screen first then from list to avoid concurrent modification errors

        if (attribute != null) {
            attribute.getRight().clear(this);
        }
    }

    public void sendItemUpdate(PacketBuffer buf) {
        ItemEditorScreen.current.ifPresent(c -> {
            this.pauseStackUpdatesFor(1000);
            MapMakingTools.HANDLER.sendToServer(new PacketItemEditorUpdate(this.slotIndex, c.getLeft().get(), buf));
        });
    }

    public void pauseStackUpdatesFor(int millis) {
        this.blockUpdateTill = Math.max(net.minecraft.util.Util.milliTime() + millis, this.blockUpdateTill);
    }

    public void recalculatePage(int perPage, boolean findPage) {
        this.attributeTabWidgets.values().forEach(this::removeWidget);
        this.attributeTabWidgets.clear();

        this.maxPages = MathHelper.ceil(this.itemList.size() / (double) perPage);

        if (findPage && this.leftBtn != null && this.rightBtn != null) {
            ItemEditorScreen.current.ifPresent(p -> {
                int index = this.itemList.indexOf(p.getLeft());
                if (index >= 0) {
                    this.currentPage = MathHelper.floor((index + 1) / (double) perPage);
                    this.leftBtn.active = this.currentPage > 0;
                    this.rightBtn.active = this.currentPage < this.maxPages - 1;
                }
            });
        }

        for (int i = 0; i < perPage; ++i) {

            int index = this.currentPage * perPage + i;
            if (index >= this.itemList.size()) break;

            IRegistryDelegate<IItemAttribute> attribute = this.itemList.get(index);
            IItemAttributeClient client = MMTRegistries.getClientMapping().get(attribute);

            IFormattableTextComponent label = new TranslationTextComponent(attribute.get().getTranslationKey());
            if (attribute.get().getFeatureState() != State.RELEASE) {
                label = label.appendSibling(new StringTextComponent(" ("+attribute.get().getFeatureState().letter+")").mergeStyle(TextFormatting.RED));
            }

            Button button = new SmallButton(18, 42 + i * TAB_HEIGHT, 100, TAB_HEIGHT, label, (btn) -> {
                Pair<IRegistryDelegate<IItemAttribute>, IItemAttributeClient> p = Pair.of(attribute, client);
                ItemEditorScreen.this.selectAttribute(p);
                ItemEditorScreen.current = Optional.of(p);
            });
            button.active = attribute.get().canUse() && attribute.get().isApplicable(this.player, this.stack);

            this.attributeTabWidgets.put(attribute, button);
            this.addButton(button);
        }
    }

    @Override
    public void render(MatrixStack stackIn, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stackIn);
        this.minecraft.getTextureManager().bindTexture(Resources.ITEM_EDITOR_SLOT);
        this.blit(stackIn, 13, 13, 0, 0, 35, 35);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(23, 16, 0);
        RenderSystem.scalef(0.48F, 0.48F, 0.48F);
        this.minecraft.fontRenderer.drawString(stackIn, "Slot: " + this.slotIndex, 0, 0, 0);
        RenderSystem.popMatrix();

        Screen.fill(stackIn, this.guiX, this.guiY, this.guiX + this.guiWidth, this.guiY + this.guiHeight, ItemEditorScreen.attributeBackgroundColour);

        ItemEditorScreen.current.ifPresent(p -> {
            if (p.getRight().shouldRenderTitle(this, this.stack)) {
                this.minecraft.fontRenderer.drawText(stackIn, new TranslationTextComponent(p.getLeft().get().getTranslationKey()), this.guiX + 2, this.guiY + 2, 1);
            }
            p.getRight().render(stackIn, this, this.guiX, this.guiY, this.guiWidth, this.guiHeight);
        });

        super.render(stackIn, mouseX, mouseY, partialTicks);

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.itemRenderer.zLevel = 100.0F;

        RenderSystem.enableDepthTest();
        this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, this.stack, 23, 23);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, this.stack, 23, 23, null);

        this.itemRenderer.zLevel = 0.0F;
        // Draw item tooltip and shade slot if mouse is above
        if (Util.isPointInRegion(23, 23, 16, 16, mouseX, mouseY)) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int slotColor = -2130706433; // From ContainerScreen
            this.fillGradient(stackIn, 23, 23, 23 + 16, 23 + 16, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            this.renderTooltip(stackIn, this.stack, mouseX, mouseY);
        }

        for (Widget widget : this.buttons) {
            if (widget.visible && widget.isHovered()) {
                widget.renderToolTip(stackIn, mouseX, mouseY);
            }
        }

        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);

        ItemEditorScreen.current.ifPresent(p -> {
            p.getRight().clear(this);
        });
    }

    // TODO Check if it was removed in 1.16
//    @Override

//    public void removed() {
//        //TODO Maybe use this over onClose
//    }

    protected <T extends Widget> T addAttributeWidget(T widgetIn) {
        this.addButton(widgetIn);
        this.attributeWidgets.add(widgetIn);
        return widgetIn;
    }

    protected <T extends Widget> T removeWidget(T widgetIn) {
        this.buttons.remove(widgetIn);
        this.children.remove(widgetIn);
        return widgetIn;
    }

    /**
     * Draws an ItemStack.
     *
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    private void drawItemStack(ItemStack stack, int x, int y) {
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, null);
        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0F;
    }
}
