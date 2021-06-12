package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import net.minecraft.util.ActionResultType;
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
        if (event.getItemStack().getItem() == MapMakingTools.WRENCH) {
            ActionResultType type = MapMakingTools.WRENCH.onBlockStartBreak(event.getItemStack(), event.getPos(), event.getFace(), event.getPlayer());
            if (type != ActionResultType.PASS) {
                event.setCanceled(true);
            }
        }
    }
}
