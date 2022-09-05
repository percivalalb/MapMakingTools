package mapmakingtools;

import com.mojang.brigadier.CommandDispatcher;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import mapmakingtools.client.data.ItemModels;
import mapmakingtools.command.RedoCommand;
import mapmakingtools.command.UndoCommand;
import mapmakingtools.handler.*;
import mapmakingtools.item.WrenchItem;
import mapmakingtools.itemeditor.*;
import mapmakingtools.lib.Constants;
import mapmakingtools.network.*;
import mapmakingtools.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Constants.MOD_ID)
public class MapMakingTools {

    public static final Logger LOGGER = LogManager.getLogger("MMT");

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Constants.MOD_ID, "channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static MapMakingTools INSTANCE;

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final RegistryObject<WrenchItem> WRENCH = ITEMS.register("wrench", WrenchItem::new);

    public MapMakingTools() {
        INSTANCE = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Config.init(modEventBus);
        modEventBus.addListener(this::newRegistry);

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::interModProcess);
        modEventBus.addListener(KeyboardInput::initBinding);

        ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerItemAttributes);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::registerCommands);

        // Client Events
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> mapmakingtools.handler.KeyboardInput::initListeners);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> GameRender::initListeners);
        World.initListeners();
        Interact.initListeners();
        Command.initListeners();
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        //PacketHandler.register();
        HANDLER.registerMessage(0, PacketItemEditorUpdate.class, PacketItemEditorUpdate::encode, PacketItemEditorUpdate::decode, PacketItemEditorUpdate::handle);
        HANDLER.registerMessage(1, PacketSelectionPoints.class, PacketSelectionPoints::encode, PacketSelectionPoints::decode, PacketSelectionPoints::handle);
        HANDLER.registerMessage(2, PacketLastAction.class, PacketLastAction::encode, PacketLastAction::decode, PacketLastAction::handle);
        HANDLER.registerMessage(3, PacketWrenchMode.class, PacketWrenchMode::encode, PacketWrenchMode::decode, PacketWrenchMode::handle);
        HANDLER.registerMessage(4, PacketUpdateLastCommand.class, PacketUpdateLastCommand::encode, PacketUpdateLastCommand::decode, PacketUpdateLastCommand::handle);
    }

    public void clientSetup(final FMLClientSetupEvent event) {

    }

    public void registerItemAttributes(final RegisterEvent event) {
        ResourceKey<Registry<IItemAttribute>> key = Registries.ITEM_ATTRIBUTES.get().getRegistryKey();
        if (!event.getRegistryKey().equals(key)) {
            return;
        }

        event.register(key, Util.getResource("nbt"), NBTViewer::new);
        event.register(key, Util.getResource("name"), ItemNameAttribute::new);
        event.register(key, Util.getResource("stacksize"), StackSizeAttribute::new);
        event.register(key, Util.getResource("repair_cost"), RepairCostAttribute::new);
        event.register(key, Util.getResource("tooltip_flags"), TooltipFlagsAttribute::new);
        event.register(key, Util.getResource("player_head"), PlayerHeadAttribute::new);
        event.register(key, Util.getResource("enchantment"), EnchantmentAttribute::new);
        event.register(key, Util.getResource("damage"), ItemDamageAttribute::new);
        event.register(key, Util.getResource("potion"), PotionAttribute::new);
        event.register(key, Util.getResource("can_place_on"), CanPlaceOnAttribute::new);
        event.register(key, Util.getResource("can_destroy"), CanDestroyAttribute::new);
        event.register(key, Util.getResource("book_details"), BookDetailsAttribute::new);
        event.register(key, Util.getResource("fireworks"), FireworksAttribute::new);
        event.register(key, Util.getResource("lore"), LoreAttribute::new);
        event.register(key, Util.getResource("modifiers"), ModifiersAttribute::new);
        event.register(key, Util.getResource("armor_color"), ArmorColorAttribute::new);
        event.register(key, Util.getResource("book_enchantment"), BookEnchantmentAttribute::new);
        event.register(key, Util.getResource("spawn_egg"), SpawnEggAttribute::new);
        event.register(key, Util.getResource("recipe_knowledge"), RecipeKnowledgeAttribute::new);
    }

    public void registerCommands(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        UndoCommand.register(dispatcher);
        RedoCommand.register(dispatcher);
        // TODO: Needs access to CommandBuildContext
        // Waiting for https://github.com/MinecraftForge/MinecraftForge/pull/8716 to be merged
        // CommandBuildContext buildCtx = event.getBuildContext();
        // WorldEditCommand.register(dispatcher, buildCtx);
    }
    protected void interModProcess(final InterModProcessEvent event) {

    }

    public void newRegistry(final NewRegistryEvent event) {
        Registries.ITEM_ATTRIBUTES = event.create(new RegistryBuilder<IItemAttribute>()
                                            .setName(Util.getResource("item_attributes"))
                                            .disableSaving()
                                            .addCallback(MMTRegistries.AttributeCallbacks.INSTANCE));
    }

    public void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            //gen.addProvider(true, new DTItemTagsProvider(gen));
           // gen.addProvider(true, new DTRecipeProvider(gen));
        }

        if (event.includeClient()) {
            //gen.addProvider(true, new Language(gen));
            //gen.addProvider(true, new DTBlockStateProvider(gen, helper));
            gen.addProvider(true, new ItemModels(gen, helper));
        }
    }
}
