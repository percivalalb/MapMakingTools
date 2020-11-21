package mapmakingtools.handler;

import java.util.Optional;

import mapmakingtools.client.screen.ItemEditorScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyboardInput {

    public static KeyBinding KEY_ITEM_EDITOR;

    public static void init(IEventBus eventBus) {
        KEY_ITEM_EDITOR = KeyboardInput.create("item_editor", "m");
        eventBus.addListener(KeyboardInput::onKeyPressed);
    }

    public static KeyBinding create(String name, String key) {
        KeyBinding keyBinding = new KeyBinding("mapmakingtools.keybind." + name, (key == null ? InputMappings.INPUT_INVALID : InputMappings.getInputByName("key.keyboard." + key)).getKeyCode(), "mapmakingtools.key.category");
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static void onKeyPressed(final KeyboardKeyPressedEvent.Pre event) {
        if (KEY_ITEM_EDITOR != null && event.getKeyCode() == KEY_ITEM_EDITOR.getKey().getKeyCode() && event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>) event.getGui();

            Optional<Slot> hoveredSlot = Optional.ofNullable(containerScreen.getSlotUnderMouse())
                    .filter(s -> s.inventory instanceof PlayerInventory)
                    .filter(Slot::isEnabled)
                    .filter(Slot::getHasStack);

            hoveredSlot.ifPresent(slot -> {
               containerScreen.getMinecraft().displayGuiScreen(new ItemEditorScreen(containerScreen.getMinecraft().player, slot.getSlotIndex(), slot.getStack()));
               event.setCanceled(true);
            });
        }
    }
}
