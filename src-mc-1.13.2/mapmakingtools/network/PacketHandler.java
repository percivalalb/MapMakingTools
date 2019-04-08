package mapmakingtools.network;

import mapmakingtools.lib.Reference;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.network.packet.PacketDrawPNG;
import mapmakingtools.network.packet.PacketEditBlock;
import mapmakingtools.network.packet.PacketEditEntity;
import mapmakingtools.network.packet.PacketItemEditorUpdate;
import mapmakingtools.network.packet.PacketOpenItemEditor;
import mapmakingtools.network.packet.PacketRequestPNG;
import mapmakingtools.network.packet.PacketSelectedFilter;
import mapmakingtools.network.packet.PacketSetPoint;
import mapmakingtools.network.packet.PacketSetPoint2;
import mapmakingtools.network.packet.PacketSkullModify;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.tools.filter.packet.PacketChestSymmetrify;
import mapmakingtools.tools.filter.packet.PacketCommandBlockAlias;
import mapmakingtools.tools.filter.packet.PacketConvertToDispenser;
import mapmakingtools.tools.filter.packet.PacketConvertToDropper;
import mapmakingtools.tools.filter.packet.PacketCreeperProperties;
import mapmakingtools.tools.filter.packet.PacketCustomGive;
import mapmakingtools.tools.filter.packet.PacketFetchMobArmour;
import mapmakingtools.tools.filter.packet.PacketFillInventory;
import mapmakingtools.tools.filter.packet.PacketItemSpawner;
import mapmakingtools.tools.filter.packet.PacketMobArmour;
import mapmakingtools.tools.filter.packet.PacketMobPosition;
import mapmakingtools.tools.filter.packet.PacketMobType;
import mapmakingtools.tools.filter.packet.PacketMobVelocity;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsAdd;
import mapmakingtools.tools.filter.packet.PacketPotentialSpawnsRemove;
import mapmakingtools.tools.filter.packet.PacketSignEdit;
import mapmakingtools.tools.filter.packet.PacketSpawnerTimings;
import mapmakingtools.tools.filter.packet.PacketVillagerProfession;
import mapmakingtools.tools.filter.packet.PacketVillagerRecipeAmounts;
import mapmakingtools.tools.filter.packet.PacketVillagerShop;
import mapmakingtools.tools.worldtransfer.PacketAddArea;
import mapmakingtools.tools.worldtransfer.PacketPaste;
import mapmakingtools.tools.worldtransfer.PacketPasteNotify;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class PacketHandler
{
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Reference.MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register()  {
        int disc = 0;

        HANDLER.registerMessage(disc++, PacketSetPoint.class, PacketSetPoint::encode, PacketSetPoint::decode, PacketSetPoint.Handler::handle);
        HANDLER.registerMessage(disc++, PacketSetPoint2.class, PacketSetPoint2::encode, PacketSetPoint2::decode, PacketSetPoint2.Handler::handle);
        HANDLER.registerMessage(disc++, PacketBiomeUpdate.class, PacketBiomeUpdate::encode, PacketBiomeUpdate::decode, PacketBiomeUpdate.Handler::handle);
        HANDLER.registerMessage(disc++, PacketDrawPNG.class, PacketDrawPNG::encode, PacketDrawPNG::decode, PacketDrawPNG.Handler::handle);
        HANDLER.registerMessage(disc++, PacketEditBlock.class, PacketEditBlock::encode, PacketEditBlock::decode, PacketEditBlock.Handler::handle);
        HANDLER.registerMessage(disc++, PacketEditEntity.class, PacketEditEntity::encode, PacketEditEntity::decode, PacketEditEntity.Handler::handle);
        HANDLER.registerMessage(disc++, PacketItemEditorUpdate.class, PacketItemEditorUpdate::encode, PacketItemEditorUpdate::decode, PacketItemEditorUpdate.Handler::handle);
        HANDLER.registerMessage(disc++, PacketOpenItemEditor.class, PacketOpenItemEditor::encode, PacketOpenItemEditor::decode, PacketOpenItemEditor.Handler::handle);
        HANDLER.registerMessage(disc++, PacketRequestPNG.class, PacketRequestPNG::encode, PacketRequestPNG::decode, PacketRequestPNG.Handler::handle);
        HANDLER.registerMessage(disc++, PacketSelectedFilter.class, PacketSelectedFilter::encode, PacketSelectedFilter::decode, PacketSelectedFilter.Handler::handle);
        HANDLER.registerMessage(disc++, PacketSkullModify.class, PacketSkullModify::encode, PacketSkullModify::decode, PacketSkullModify.Handler::handle);
        HANDLER.registerMessage(disc++, PacketUpdateBlock.class, PacketUpdateBlock::encode, PacketUpdateBlock::decode, PacketUpdateBlock.Handler::handle);
        HANDLER.registerMessage(disc++, PacketUpdateEntity.class, PacketUpdateEntity::encode, PacketUpdateEntity::decode, PacketUpdateEntity.Handler::handle);
        
        
        HANDLER.registerMessage(disc++, PacketBabyMonster.class, PacketBabyMonster::encode, PacketBabyMonster::decode, PacketBabyMonster.Handler::handle);
        HANDLER.registerMessage(disc++, PacketChestSymmetrify.class, PacketChestSymmetrify::encode, PacketChestSymmetrify::decode, PacketChestSymmetrify.Handler::handle);
        HANDLER.registerMessage(disc++, PacketCommandBlockAlias.class, PacketCommandBlockAlias::encode, PacketCommandBlockAlias::decode, PacketCommandBlockAlias.Handler::handle);
        HANDLER.registerMessage(disc++, PacketConvertToDispenser.class, PacketConvertToDispenser::encode, PacketConvertToDispenser::decode, PacketConvertToDispenser.Handler::handle);
        HANDLER.registerMessage(disc++, PacketConvertToDropper.class, PacketConvertToDropper::encode, PacketConvertToDropper::decode, PacketConvertToDropper.Handler::handle);
        HANDLER.registerMessage(disc++, PacketCreeperProperties.class, PacketCreeperProperties::encode, PacketCreeperProperties::decode, PacketCreeperProperties.Handler::handle);
        HANDLER.registerMessage(disc++, PacketCustomGive.class, PacketCustomGive::encode, PacketCustomGive::decode, PacketCustomGive.Handler::handle);
        HANDLER.registerMessage(disc++, PacketFetchMobArmour.class, PacketFetchMobArmour::encode, PacketFetchMobArmour::decode, PacketFetchMobArmour.Handler::handle);
        HANDLER.registerMessage(disc++, PacketFillInventory.class, PacketFillInventory::encode, PacketFillInventory::decode, PacketFillInventory.Handler::handle);
        HANDLER.registerMessage(disc++, PacketItemSpawner.class, PacketItemSpawner::encode, PacketItemSpawner::decode, PacketItemSpawner.Handler::handle);
        HANDLER.registerMessage(disc++, PacketMobArmour.class, PacketMobArmour::encode, PacketMobArmour::decode, PacketMobArmour.Handler::handle);
        HANDLER.registerMessage(disc++, PacketMobPosition.class, PacketMobPosition::encode, PacketMobPosition::decode, PacketMobPosition.Handler::handle);
        HANDLER.registerMessage(disc++, PacketMobType.class, PacketMobType::encode, PacketMobType::decode, PacketMobType.Handler::handle);
        HANDLER.registerMessage(disc++, PacketMobVelocity.class, PacketMobVelocity::encode, PacketMobVelocity::decode, PacketMobVelocity.Handler::handle);
        HANDLER.registerMessage(disc++, PacketPotentialSpawnsAdd.class, PacketPotentialSpawnsAdd::encode, PacketPotentialSpawnsAdd::decode, PacketPotentialSpawnsAdd.Handler::handle);
        HANDLER.registerMessage(disc++, PacketPotentialSpawnsRemove.class, PacketPotentialSpawnsRemove::encode, PacketPotentialSpawnsRemove::decode, PacketPotentialSpawnsRemove.Handler::handle);
        HANDLER.registerMessage(disc++, PacketSignEdit.class, PacketSignEdit::encode, PacketSignEdit::decode, PacketSignEdit.Handler::handle);
        HANDLER.registerMessage(disc++, PacketSpawnerTimings.class, PacketSpawnerTimings::encode, PacketSpawnerTimings::decode, PacketSpawnerTimings.Handler::handle);
        HANDLER.registerMessage(disc++, PacketVillagerProfession.class, PacketVillagerProfession::encode, PacketVillagerProfession::decode, PacketVillagerProfession.Handler::handle);
        HANDLER.registerMessage(disc++, PacketVillagerRecipeAmounts.class, PacketVillagerRecipeAmounts::encode, PacketVillagerRecipeAmounts::decode, PacketVillagerRecipeAmounts.Handler::handle);
        HANDLER.registerMessage(disc++, PacketVillagerShop.class, PacketVillagerShop::encode, PacketVillagerShop::decode, PacketVillagerShop.Handler::handle);
        
        HANDLER.registerMessage(disc++, PacketAddArea.class, PacketAddArea::encode, PacketAddArea::decode, PacketAddArea.Handler::handle);
        HANDLER.registerMessage(disc++, PacketPaste.class, PacketPaste::encode, PacketPaste::decode, PacketPaste.Handler::handle);
        HANDLER.registerMessage(disc++, PacketPasteNotify.class, PacketPasteNotify::encode, PacketPasteNotify::decode, PacketPasteNotify.Handler::handle);
    }

    public static <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        HANDLER.send(target, message);
    }
}