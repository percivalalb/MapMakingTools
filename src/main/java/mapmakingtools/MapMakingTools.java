package mapmakingtools;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapmakingtools.api.itemeditor.IItemAttribute;
import mapmakingtools.api.itemeditor.Registries;
import mapmakingtools.handler.KeyboardInput;
import mapmakingtools.itemeditor.ArmorColorAttribute;
import mapmakingtools.itemeditor.BookDetailsAttribute;
import mapmakingtools.itemeditor.BookEnchantmentAttribute;
import mapmakingtools.itemeditor.CanDestroyAttribute;
import mapmakingtools.itemeditor.CanPlaceOnAttribute;
import mapmakingtools.itemeditor.EnchantmentAttribute;
import mapmakingtools.itemeditor.FireworksAttribute;
import mapmakingtools.itemeditor.ItemDamageAttribute;
import mapmakingtools.itemeditor.ItemNameAttribute;
import mapmakingtools.itemeditor.LoreAttribute;
import mapmakingtools.itemeditor.ModifiersAttribute;
import mapmakingtools.itemeditor.NBTViewer;
import mapmakingtools.itemeditor.PlayerHeadAttribute;
import mapmakingtools.itemeditor.PotionAttribute;
import mapmakingtools.itemeditor.RepairCostAttribute;
import mapmakingtools.itemeditor.SpawnEggAttribute;
import mapmakingtools.itemeditor.StackSizeAttribute;
import mapmakingtools.itemeditor.TooltipFlagsAttribute;
import mapmakingtools.lib.Constants;
import mapmakingtools.network.PacketItemEditorUpdate;
import mapmakingtools.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

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

    public MapMakingTools() {
        INSTANCE = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //modEventBus.addGenericListener(Item.class, ModItems::registerItems);

        modEventBus.addListener(this::newRegistry);

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::interModProcess);

        modEventBus.addGenericListener(IItemAttribute.class, this::registerItemAttributes);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        //forgeEventBus.register(new NewEvent());

        // Client Events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            //modEventBus.addListener(KeyboardInput::registerBlockColours);
            KeyboardInput.init(forgeEventBus);
            //forgeEventBus.addListener(GameOverlay::onPreRenderGameOverlay);
        });
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        //PacketHandler.register();
        HANDLER.registerMessage(0, PacketItemEditorUpdate.class, PacketItemEditorUpdate::encode, PacketItemEditorUpdate::decode, PacketItemEditorUpdate::handle);
    }

    public void clientSetup(final FMLClientSetupEvent event) {

        //ScreenManager.registerFactory(ModContainerTypes.FOOD_BOWL, GuiFoodBowl::new);
    }

    protected void interModProcess(final InterModProcessEvent event) {

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
    }

    public <T extends IForgeRegistryEntry<T>> void register(final IForgeRegistry<T> registry, final String name, final Supplier<? extends T> supplier) {
        registry.register(supplier.get().setRegistryName(Util.getResource(name)));
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
            //gen.addProvider(new ItemModels(gen, helper));
        }
    }
}
