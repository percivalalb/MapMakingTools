package mapmakingtools.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
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
    private List<AbstractWidget> attributeWidgets = Lists.newArrayList();
    private Map<IRegistryDelegate<IItemAttribute>, AbstractWidget> attributeTabWidgets = new HashMap<>();
    private int guiX, guiY, guiWidth, guiHeight;
    private long blockUpdateTill = 0;
    private boolean requiresUpdate = false;

    private Player player;
    private ItemStack stack;
    private int slotIndex;
    private List<IRegistryDelegate<IItemAttribute>> itemList;

    private Button leftBtn, rightBtn;

    public ItemEditorScreen(Player player, int slotIndex, ItemStack stack) {
        super(new TranslatableComponent("screen.mapmakingtools.item_editor"));
        this.player = player;
        this.stack = stack;
        this.slotIndex = slotIndex;
        this.itemList = Util.getDelegates(Registries.ITEM_ATTRIBUTES.get(), IFeatureState::isVisible);
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.guiX = 150;
        this.guiY = 13;
        this.guiWidth = this.width - this.guiX - 13;
        this.guiHeight = this.height - this.guiY * 2;

        int size = this.itemList.size();
        int perPage = Math.max(Mth.floor((this.height - 42 - 3) / (double) TAB_HEIGHT), 1);

        if (perPage < size) {
            this.leftBtn = new Button(49, 18, 20, 20, new TextComponent("<"), (btn) -> {
                this.currentPage = Math.max(0, this.currentPage - 1);
                btn.active = this.currentPage > 0;
                this.rightBtn.active = true;
                this.recalculatePage(perPage, false);
            });
            this.leftBtn.active = false;

            this.rightBtn = new Button(72, 18, 20, 20, new TextComponent(">"), (btn) -> {
                this.currentPage = Math.min(this.maxPages - 1, this.currentPage + 1);
                btn.active = this.currentPage < this.maxPages - 1;
                this.leftBtn.active = true;
                this.recalculatePage(perPage, false);
            });

            this.addRenderableWidget(this.leftBtn);
            this.addRenderableWidget(this.rightBtn);
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
        long systemTimeMillis = net.minecraft.Util.getMillis();

        ItemEditorScreen.current.ifPresent(p -> {
            p.getRight().tick(this, systemTimeMillis, this::sendItemUpdate);
        });

        ItemStack playersStack = this.player.getInventory().getItem(this.slotIndex);
        if (!ItemStack.matches(playersStack, this.stack)) {
            this.requiresUpdate = ItemEditorScreen.current.isPresent() ? this.current.get().getRight().requiresUpdate(playersStack, this.stack) : false;
            this.stack = playersStack.copy();
            for (Entry<IRegistryDelegate<IItemAttribute>, AbstractWidget> attributeBtns : this.attributeTabWidgets.entrySet()) {
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
                int direction = Mth.sign(amount);
                this.slotIndex += direction + this.player.getInventory().getContainerSize();
                this.slotIndex %= this.player.getInventory().getContainerSize();

                ItemStack stack = this.player.getInventory().getItem(this.slotIndex);
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

    public void sendItemUpdate(FriendlyByteBuf buf) {
        ItemEditorScreen.current.ifPresent(c -> {
            this.pauseStackUpdatesFor(1000);
            MapMakingTools.HANDLER.sendToServer(new PacketItemEditorUpdate(this.slotIndex, c.getLeft().get(), buf));
        });
    }

    public void pauseStackUpdatesFor(int millis) {
        this.blockUpdateTill = Math.max(net.minecraft.Util.getMillis() + millis, this.blockUpdateTill);
    }

    public void recalculatePage(int perPage, boolean findPage) {
        this.attributeTabWidgets.values().forEach(this::removeWidget);
        this.attributeTabWidgets.clear();

        this.maxPages = Mth.ceil(this.itemList.size() / (double) perPage);

        if (findPage && this.leftBtn != null && this.rightBtn != null) {
            ItemEditorScreen.current.ifPresent(p -> {
                int index = this.itemList.indexOf(p.getLeft());
                if (index >= 0) {
                    this.currentPage = Mth.floor((index + 1) / (double) perPage);
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

            MutableComponent label = new TranslatableComponent(attribute.get().getTranslationKey());
            if (attribute.get().getFeatureState() != State.RELEASE) {
                label = label.append(new TextComponent(" ("+attribute.get().getFeatureState().letter+")").withStyle(ChatFormatting.RED));
            }

            Button button = new SmallButton(18, 42 + i * TAB_HEIGHT, 100, TAB_HEIGHT, label, (btn) -> {
                Pair<IRegistryDelegate<IItemAttribute>, IItemAttributeClient> p = Pair.of(attribute, client);
                ItemEditorScreen.this.selectAttribute(p);
                ItemEditorScreen.current = Optional.of(p);
            });
            button.active = attribute.get().canUse() && attribute.get().isApplicable(this.player, this.stack);

            this.attributeTabWidgets.put(attribute, button);
            this.addRenderableWidget(button);
        }
    }

    @Override
    public void render(PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stackIn);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Resources.ITEM_EDITOR_SLOT);
        this.blit(stackIn, 13, 13, 0, 0, 35, 35);
        stackIn.pushPose();
        stackIn.translate(23, 16, 0);
        stackIn.scale(0.48F, 0.48F, 0.48F);
        this.minecraft.font.draw(stackIn, "Slot: " + this.slotIndex, 0, 0, 0);
        stackIn.popPose();

        Screen.fill(stackIn, this.guiX, this.guiY, this.guiX + this.guiWidth, this.guiY + this.guiHeight, ItemEditorScreen.attributeBackgroundColour);

        ItemEditorScreen.current.ifPresent(p -> {
            if (p.getRight().shouldRenderTitle(this, this.stack)) {
                this.minecraft.font.draw(stackIn, new TranslatableComponent(p.getLeft().get().getTranslationKey()), this.guiX + 2, this.guiY + 2, 1);
            }
            p.getRight().render(stackIn, this, this.guiX, this.guiY, this.guiWidth, this.guiHeight);
        });

        super.render(stackIn, mouseX, mouseY, partialTicks);

        stackIn.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // TODO
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.itemRenderer.blitOffset = 100.0F;

        RenderSystem.enableDepthTest();
        this.itemRenderer.renderAndDecorateItem(this.stack, 23, 23);
        this.itemRenderer.renderGuiItemDecorations(this.font, this.stack, 23, 23, null);

        this.itemRenderer.blitOffset = 0.0F;
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

        for (Widget w : this.renderables) {
            if (!(w instanceof AbstractWidget widget)) { continue; }

            if (widget.visible && widget.isHoveredOrFocused()) {
                widget.renderToolTip(stackIn, mouseX, mouseY);
            }
        }

        stackIn.popPose();
        RenderSystem.enableDepthTest();
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);

        ItemEditorScreen.current.ifPresent(p -> {
            p.getRight().clear(this);
        });
    }

    // TODO Check if it was removed in 1.16
//    @Override

//    public void removed() {
//        //TODO Maybe use this over onClose
//    }

    protected <T extends AbstractWidget> T addAttributeWidget(T widgetIn) {
        this.addRenderableWidget(widgetIn);
        this.attributeWidgets.add(widgetIn);
        return widgetIn;
    }

    protected <T extends AbstractWidget> T removeWidget(T widgetIn) {
        super.removeWidget(widgetIn);
        return widgetIn;
    }
}
