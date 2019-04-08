package mapmakingtools.proxy;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.handler.GameOverlay;
import mapmakingtools.handler.GuiHandler;
import mapmakingtools.handler.GuiOpen;
import mapmakingtools.handler.KeyboardInput;
import mapmakingtools.handler.RenderWorld;
import mapmakingtools.tools.attribute.ArmourColourAttribute;
import mapmakingtools.tools.attribute.BlockDestroyAttribute;
import mapmakingtools.tools.attribute.BookAttribute;
import mapmakingtools.tools.attribute.BookEnchantmentAttribute;
import mapmakingtools.tools.attribute.CanPlaceOnAttribute;
import mapmakingtools.tools.attribute.EnchantmentAttribute;
import mapmakingtools.tools.attribute.FireworksAttribute;
import mapmakingtools.tools.attribute.ItemMetaAttribute;
import mapmakingtools.tools.attribute.ItemNameAttribute;
import mapmakingtools.tools.attribute.LoreAttribute;
import mapmakingtools.tools.attribute.ModifiersAttribute;
import mapmakingtools.tools.attribute.PlayerHeadAttribute;
import mapmakingtools.tools.attribute.PotionAttribute;
import mapmakingtools.tools.attribute.RecipeKnowledgeAttribute;
import mapmakingtools.tools.attribute.RepairCostAttribute;
import mapmakingtools.tools.attribute.StackSizeAttribute;
import mapmakingtools.tools.attribute.TooltipFlagsAttribute;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author ProPercivalalb
 */
public class ClientProxy extends CommonProxy {

	public ClientProxy() {
    	super();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::openGui);
    }
	
	@Override
	public void postInit(InterModProcessEvent event) {
		super.postInit(event);
		MapMakingTools.LOGGER.info("Loading World Transfer file");
		WorldTransferList.readFromFile();
	}
	
	@Override
	public void registerEventHandlers() {
		super.registerEventHandlers();
		ClientRegistry.registerKeyBinding(KeyboardInput.KEY_ITEM_EDITOR);
    	MinecraftForge.EVENT_BUS.register(new RenderWorld());
    	MinecraftForge.EVENT_BUS.register(new GameOverlay());
    	MinecraftForge.EVENT_BUS.register(new KeyboardInput());
    	MinecraftForge.EVENT_BUS.register(new GuiOpen());
	}
	
	@Override
	public void registerItemAttribute() {
		ItemEditorManager.registerItemHandler(new ItemNameAttribute());
		ItemEditorManager.registerItemHandler(new LoreAttribute());
		ItemEditorManager.registerItemHandler(new TooltipFlagsAttribute());
		ItemEditorManager.registerItemHandler(new StackSizeAttribute());
		ItemEditorManager.registerItemHandler(new ItemMetaAttribute());
		ItemEditorManager.registerItemHandler(new RepairCostAttribute());
		ItemEditorManager.registerItemHandler(new BlockDestroyAttribute());
		ItemEditorManager.registerItemHandler(new CanPlaceOnAttribute());
		ItemEditorManager.registerItemHandler(new ModifiersAttribute());
		ItemEditorManager.registerItemHandler(new EnchantmentAttribute());
		ItemEditorManager.registerItemHandler(new BookEnchantmentAttribute());
		ItemEditorManager.registerItemHandler(new PotionAttribute());
		ItemEditorManager.registerItemHandler(new BookAttribute());
		ItemEditorManager.registerItemHandler(new PlayerHeadAttribute());
		ItemEditorManager.registerItemHandler(new FireworksAttribute());
		ItemEditorManager.registerItemHandler(new ArmourColourAttribute());
		ItemEditorManager.registerItemHandler(new RecipeKnowledgeAttribute());
	}
	
	@Override
	public EntityPlayer getPlayerEntity() {
		//MapMakingTools.LOGGER.info("GET PLAYER");
		return Minecraft.getInstance().player;
	}
}
