package mapmakingtools.proxy;

import java.util.List;

import mapmakingtools.ModItems;
import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.manager.FilterManager;
import mapmakingtools.api.manager.ItemEditorManager;
import mapmakingtools.client.gui.GuiFilter;
import mapmakingtools.client.gui.GuiItemEditor;
import mapmakingtools.client.gui.GuiWorldTransfer;
import mapmakingtools.client.model.ModelHelper;
import mapmakingtools.handler.BlockHighlightHandler;
import mapmakingtools.handler.GuiOpenHandler;
import mapmakingtools.handler.KeyStateHandler;
import mapmakingtools.handler.ScreenRenderHandler;
import mapmakingtools.handler.WorldOverlayHandler;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.lib.Reference;
import mapmakingtools.tools.attribute.ArmorColourAttribute;
import mapmakingtools.tools.attribute.BlockDestroyAttribute;
import mapmakingtools.tools.attribute.BookAttribute;
import mapmakingtools.tools.attribute.BookEnchantmentAttribute;
import mapmakingtools.tools.attribute.CanPlaceOnAttribute;
import mapmakingtools.tools.attribute.EnchantmentAttribute;
import mapmakingtools.tools.attribute.ItemMetaAttribute;
import mapmakingtools.tools.attribute.ItemNameAttribute;
import mapmakingtools.tools.attribute.LoreAttribute;
import mapmakingtools.tools.attribute.ModifiersAttribute;
import mapmakingtools.tools.attribute.PlayerHeadAttribute;
import mapmakingtools.tools.attribute.PotionAttribute;
import mapmakingtools.tools.attribute.RepairCostAttribute;
import mapmakingtools.tools.attribute.StackSizeAttribute;
import mapmakingtools.tools.attribute.TooltipFlagsAttribute;
import mapmakingtools.tools.worldtransfer.WorldTransferList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author ProPercivalalb
 */
public class ClientProxy extends CommonProxy {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		
		if(ID == ID_FILTER_BLOCK) {
			List<IFilterClient> filterList = FilterManager.getClientBlocksFilters(player, world, pos);
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, pos);
		}
		else if(ID == ID_FILTER_ENTITY) {
			List<IFilterClient> filterList = FilterManager.getClientEntitiesFilters(player, world.getEntityByID(x));
			if(filterList.size() > 0)
				return new GuiFilter(filterList, player, x);
		}
		else if(ID == GUI_ID_ITEM_EDITOR) {
			return new GuiItemEditor(player, x);
		}
		else if(ID == GUI_ID_WORLD_TRANSFER) {
			return new GuiWorldTransfer();
		}
		return null;
	}
	
	@Override
	public void onPreLoad() {
		super.onPreLoad();
	}
	
	@Override
	public void onPostLoad() {
		super.onPostLoad();
    	LogHelper.info("Loading World Transfer file");
		WorldTransferList.readFromFile();
	}
	
	@Override
	public void registerHandlers() {
		ClientRegistry.registerKeyBinding(KeyStateHandler.keyItemEditor);
    	FMLCommonHandler.instance().bus().register(new KeyStateHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldOverlayHandler());
    	MinecraftForge.EVENT_BUS.register(new ScreenRenderHandler());
    	MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
    	MinecraftForge.EVENT_BUS.register(new BlockHighlightHandler());
    	
    	ModelHelper.registerItem(ModItems.editItem, 0, "mapmakingtools:edit_item");
    	ModelHelper.registerItem(ModItems.editItem, 1, "mapmakingtools:wrench");
    	ModelBakery.registerItemVariants(ModItems.editItem, new ResourceLocation(Reference.MOD_ID, "edit_item"), new ResourceLocation(Reference.MOD_ID, "wrench"));

    	
	}
	
	public void registerItem(ItemModelMesher itemModelMesher, Item itm, int subType, String identifier) {
		itemModelMesher.register(itm, subType, new ModelResourceLocation(identifier, "inventory"));
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
		//ItemEditorManager.registerItemHandler(new FireworksAttribute());
		ItemEditorManager.registerItemHandler(new ArmorColourAttribute());
	}
	
	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
	}
	
	@Override
	public EntityPlayer getPlayerEntity() {
		return Minecraft.getMinecraft().player;
	}
	
	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromContext(ctx));
	}
}
