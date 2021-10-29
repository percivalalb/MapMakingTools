package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.client.screen.ItemEditorScreen;
import mapmakingtools.network.PacketLastAction;
import mapmakingtools.network.PacketWrenchMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Optional;

public class KeyboardInput {

    public static KeyBinding KEY_ITEM_EDITOR;
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

    public static KeyBinding create(String name, String key) {
        KeyBinding keyBinding = new KeyBinding("mapmakingtools.keybind." + name, (key == null ? InputMappings.UNKNOWN : InputMappings.getKey("key.keyboard." + key)).getValue(), "mapmakingtools.key.category");
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static void onKeyPressed(final KeyboardKeyPressedEvent.Pre event) {
        if (KEY_ITEM_EDITOR == null || event.getKeyCode() != KEY_ITEM_EDITOR.getKey().getValue()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (FeatureAvailability.canEdit(mc.player) && event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>) event.getGui();

            Optional<Slot> hoveredSlot = Optional.ofNullable(containerScreen.getSlotUnderMouse())
                    .filter(s -> s.container instanceof PlayerInventory)
                    .filter(Slot::isActive)
                    .filter(Slot::hasItem);

            hoveredSlot.ifPresent(slot -> {
               containerScreen.getMinecraft().setScreen(new ItemEditorScreen(containerScreen.getMinecraft().player, slot.getSlotIndex(), slot.getItem()));
               event.setCanceled(true);
            });
        }
    }

    public static void onGUIScroll(final GuiScreenEvent.MouseScrollEvent event) {
        if (event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>) event.getGui();

            Optional<Slot> hoveredSlot = Optional.ofNullable(containerScreen.getSlotUnderMouse())
                    .filter(s -> s.container instanceof PlayerInventory)
                    .filter(Slot::isActive)
                    .filter(Slot::hasItem);

            hoveredSlot.ifPresent(slot -> {
                Minecraft mc = Minecraft.getInstance();

                if (FeatureAvailability.canEdit(mc.player) && slot.getItem().getItem() == MapMakingTools.WRENCH) {
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

        if (mc.screen == null && FeatureAvailability.canEdit(mc.player) && mc.player.getMainHandItem().getItem() == MapMakingTools.WRENCH) {
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
