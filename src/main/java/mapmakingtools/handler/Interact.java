package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public class Interact {

    public static void initListeners() {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(EventPriority.HIGHEST, Interact::onMouseClick);
    }

    public static void onMouseClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getItemStack().is(MapMakingTools.WRENCH.get())) {
            InteractionResult type = MapMakingTools.WRENCH.get().onBlockStartBreak(event.getItemStack(), event.getPos(), event.getFace(), event.getEntity());
            if (type != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }
}
