package mapmakingtools;

import com.mojang.brigadier.CommandDispatcher;
import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import mapmakingtools.client.data.ItemModels;
import mapmakingtools.command.RedoCommand;
import mapmakingtools.command.UndoCommand;
import mapmakingtools.command.WorldEditCommand;
import mapmakingtools.handler.Command;
import mapmakingtools.handler.GameRender;
import mapmakingtools.handler.Interact;
import mapmakingtools.handler.World;
import mapmakingtools.item.WrenchItem;
import mapmakingtools.itemeditor.*;
import mapmakingtools.lib.Constants;
import mapmakingtools.network.*;
import mapmakingtools.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

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

    public static WrenchItem WRENCH;

    public MapMakingTools() {
        INSTANCE = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::newRegistry);

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::interModProcess);

        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addGenericListener(IItemAttribute.class, this::registerItemAttributes);

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
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> mapmakingtools.handler.KeyboardInput::initBinding);

    }

    public void registerCommands(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher =  event.getDispatcher();
        UndoCommand.register(dispatcher);
        RedoCommand.register(dispatcher);
        WorldEditCommand.register(dispatcher);
    }

    protected void interModProcess(final InterModProcessEvent event) {

    }

    public void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        WRENCH = register(registry, "wrench", WrenchItem::new);
    }

    public void registerItemAttributes(final RegistryEvent.Register<IItemAttribute> event) {
        IForgeRegistry<IItemAttribute> registry = event.getRegistry();

        register(registry, "nbt", NBTViewer::new);
        register(registry, "name", ItemNameAttribute::new);
        register(registry, "stacksize", StackSizeAttribute::new);
        register(registry, "repair_cost", RepairCostAttribute::new);
        register(registry, "tooltip_flags", TooltipFlagsAttribute::new);
        register(registry, "player_head", PlayerHeadAttribute::new);
        register(registry, "enchantment", EnchantmentAttribute::new);
        register(registry, "damage", ItemDamageAttribute::new);
        register(registry, "potion", PotionAttribute::new);
        register(registry, "can_place_on", CanPlaceOnAttribute::new);
        register(registry, "can_destroy", CanDestroyAttribute::new);
        register(registry, "book_details", BookDetailsAttribute::new);
        register(registry, "fireworks", FireworksAttribute::new);
        register(registry, "lore", LoreAttribute::new);
        register(registry, "modifiers", ModifiersAttribute::new);
        register(registry, "armor_color", ArmorColorAttribute::new);
        register(registry, "book_enchantment", BookEnchantmentAttribute::new);
        register(registry, "spawn_egg", SpawnEggAttribute::new);
        register(registry, "recipe_knowledge", RecipeKnowledgeAttribute::new);
    }

    public <T extends IForgeRegistryEntry<T>, A extends T> A register(final IForgeRegistry<T> registry, final String name, final Supplier<A> supplier) {
        A object = supplier.get();
        object.setRegistryName(Util.getResource(name));
        registry.register(object);
        return object;
    }

    public void newRegistry(final RegistryEvent.NewRegistry event) {
        Registries.ITEM_ATTRIBUTES = new RegistryBuilder<IItemAttribute>()
                                            .setType(IItemAttribute.class)
                                            .setName(Util.getResource("item_attributes"))
                                            .disableSaving()
                                            .addCallback(MMTRegistries.AttributeCallbacks.INSTANCE)
                                            .create();
    }

    public void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            //gen.addProvider(new DTItemTagsProvider(gen));
           // gen.addProvider(new DTRecipeProvider(gen));
        }

        if (event.includeClient()) {
            //gen.addProvider(new Language(gen));
            //gen.addProvider(new DTBlockStateProvider(gen, helper));
            gen.addProvider(new ItemModels(gen, helper));
        }
    }
}
