package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.client.screen.ItemEditorScreen;
import mapmakingtools.network.PacketLastAction;
import mapmakingtools.network.PacketWrenchMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.ClientRegistry;

import java.util.Optional;

public class KeyboardInput {

    public static KeyMapping KEY_ITEM_EDITOR;
    public static Long lastPressed = null;
    public static boolean released = false;

    public static void initBinding() {
        KEY_ITEM_EDITOR = KeyboardInput.create("item_editor", "m");
    }

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(KeyboardInput::onKeyPressed);
        forgeEventBus.addListener(KeyboardInput::onKeyReleased);
        forgeEventBus.addListener(KeyboardInput::onGUIScroll);
        forgeEventBus.addListener(KeyboardInput::onTick);
    }

    public static KeyMapping create(String name, String key) {
        KeyMapping keyBinding = new KeyMapping("mapmakingtools.keybind." + name, (key == null ? InputConstants.UNKNOWN : InputConstants.getKey("key.keyboard." + key)).getValue(), "mapmakingtools.key.category");
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static void onKeyPressed(final KeyboardKeyPressedEvent.Pre event) {
        if (KEY_ITEM_EDITOR == null || event.getKeyCode() != KEY_ITEM_EDITOR.getKey().getValue()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (FeatureAvailability.canEdit(mc.player) && event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) event.getScreen();

            Optional<Slot> hoveredSlot = Optional.ofNullable(containerScreen.getSlotUnderMouse())
                    .filter(s -> s.container instanceof Inventory)
                    .filter(Slot::isActive)
                    .filter(Slot::hasItem);

            hoveredSlot.ifPresent(slot -> {
               containerScreen.getMinecraft().setScreen(new ItemEditorScreen(containerScreen.getMinecraft().player, slot.getSlotIndex(), slot.getItem()));
               event.setCanceled(true);
            });
        }
    }

    public static void onGUIScroll(final ScreenEvent.MouseScrollEvent event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) event.getScreen();

            Optional<Slot> hoveredSlot = Optional.ofNullable(containerScreen.getSlotUnderMouse())
                    .filter(s -> s.container instanceof Inventory)
                    .filter(Slot::isActive)
                    .filter(Slot::hasItem);

            hoveredSlot.ifPresent(slot -> {
                Minecraft mc = Minecraft.getInstance();

                if (FeatureAvailability.canEdit(mc.player) && slot.getItem().is(MapMakingTools.WRENCH.get())) {
                    MapMakingTools.HANDLER.sendToServer(new PacketWrenchMode(slot.getSlotIndex(), Math.signum(event.getScrollDelta()) == 1));
                    event.setCanceled(true);
                }
            });
        };
    }

    public static void onKeyReleased(final InputEvent.KeyInputEvent event) {
        if (KEY_ITEM_EDITOR == null || event.getKey() != KEY_ITEM_EDITOR.getKey().getValue()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null && FeatureAvailability.canEdit(mc.player) && mc.player.getMainHandItem().is(MapMakingTools.WRENCH.get())) {
            if (event.getAction() == 1) {
                if (lastPressed == null || lastPressed + 500 < System.currentTimeMillis()) {
                    lastPressed = System.currentTimeMillis();
                    released = false;
                } else if (released) {
                    lastPressed = null;
                    released = false;
                    MapMakingTools.HANDLER.sendToServer(new PacketLastAction());
                }
            } else if (event.getAction() == 0) {
                released = true;
            }
        }
    }

    public static void onTick(final TickEvent.ClientTickEvent event) {
        if (released) {

        }
    }
}
