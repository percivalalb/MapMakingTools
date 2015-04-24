package mapmakingtools.proxy;

import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.handler.BlockHighlightHandler;
import mapmakingtools.handler.ClientTickHandler;
import mapmakingtools.handler.GuiOpenHandler;
import mapmakingtools.handler.KeyStateHandler;
import mapmakingtools.handler.ScreenRenderHandler;
import mapmakingtools.handler.WorldOverlayHandler;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.tools.attribute.ArmorColourAttribute;
import mapmakingtools.tools.attribute.BookAttribute;
import mapmakingtools.tools.attribute.BookEnchantmentAttribute;
import mapmakingtools.tools.attribute.EnchantmentAttribute;
import mapmakingtools.tools.attribute.ItemMetaAttribute;
import mapmakingtools.tools.attribute.ItemNameAttribute;
import mapmakingtools.tools.attribute.PlayerHeadAttribute;
import mapmakingtools.tools.attribute.PotionAttribute;
import mapmakingtools.tools.attribute.RepairCostAttribute;
import mapmakingtools.tools.attribute.StackSizeAttribute;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void onPreLoad() {
		super.onPreLoad();
    	LogHelper.info("Loading World Transfer file");
		WorldTransferList.readFromFile();
	}
	
	@Override
	public void registerHandlers() {
		ClientRegistry.registerKeyBinding(KeyStateHandler.keyItemEditor);
		ClientRegistry.registerKeyBinding(KeyStateHandler.keyBlockHelper);
    	FMLCommonHandler.instance().bus().register(new KeyStateHandler());
    	FMLCommonHandler.instance().bus().register(new ClientTickHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldOverlayHandler());
    	MinecraftForge.EVENT_BUS.register(new ScreenRenderHandler());
    	MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
    	MinecraftForge.EVENT_BUS.register(new BlockHighlightHandler());
	}
	
	@Override
	public void registerItemAttribute() {
		ItemEditorManager.registerItemHandler(new ItemNameAttribute());
		//ItemEditorManager.registerItemHandler(new LoreAttribute());
		ItemEditorManager.registerItemHandler(new StackSizeAttribute());
		ItemEditorManager.registerItemHandler(new ItemMetaAttribute());
		ItemEditorManager.registerItemHandler(new RepairCostAttribute());
		ItemEditorManager.registerItemHandler(new EnchantmentAttribute());
		ItemEditorManager.registerItemHandler(new BookEnchantmentAttribute());
		ItemEditorManager.registerItemHandler(new PotionAttribute());
		ItemEditorManager.registerItemHandler(new BookAttribute());
		ItemEditorManager.registerItemHandler(new PlayerHeadAttribute());
		//ItemEditorManager.registerItemHandler(new FireworksAttribute());
		ItemEditorManager.registerItemHandler(new ArmorColourAttribute());
	}
	
	@Override
	public EntityPlayer getClientPlayer() {
		return FMLClientHandler.instance().getClientPlayerEntity();
	}
}
