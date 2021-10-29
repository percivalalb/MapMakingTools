package mapmakingtools.item;

import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.api.util.IFeatureState;
import mapmakingtools.api.util.State;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class WrenchItem extends Item {

    public WrenchItem() {
        super(new Properties().fireResistant().setNoRepair());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Mode mode = this.getMode(stack);
        if (!FeatureAvailability.canEdit(context.getPlayer()) || !mode.canUse()) {
            return InteractionResult.FAIL;
        }

        switch (mode) {
            case BLOCK_EDIT:
                BlockState state = context.getLevel().getBlockState(context.getClickedPos());
                BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
                if (state.getBlock().is(Blocks.SNOW)) {
                    int i = state.getValue(SnowLayerBlock.LAYERS);
                    BlockState rotState = state.setValue(SnowLayerBlock.LAYERS, context.getPlayer().isShiftKeyDown() ? ((i + 6) % 8) + 1 : i % 8 + 1);
                    if (!context.getLevel().isClientSide) {
                        context.getLevel().setBlock(context.getClickedPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return InteractionResult.SUCCESS;
                }

                BlockState rotState = context.getPlayer().isShiftKeyDown()
                        ? state.mirror(Mirror.FRONT_BACK)
                        : state.rotate(context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);

                if (!rotState.equals(state)) {
                    if (!context.getLevel().isClientSide) {
                        context.getLevel().setBlock(context.getClickedPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.FAIL;
            case QUICK_BUILD:
                if (!context.getLevel().isClientSide) {
                    BlockPos pos = context.getClickedPos();
                    if (context.getPlayer().isShiftKeyDown()) {
                        pos = pos.relative(context.getClickedFace());
                    }

                    SelectionManager selManager = DimensionData.get(context.getLevel()).getSelectionManager();
                    if (selManager.setSecondary(context.getPlayer(), pos)) {
                        selManager.sync(context.getPlayer());
                    }
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            case ENTITY_EDIT:
                return InteractionResult.FAIL;
        }

        return null;
    }

    public InteractionResult onBlockStartBreak(ItemStack stack, BlockPos pos, Direction face, Player player) {
        Mode mode = this.getMode(stack);
        if (!FeatureAvailability.canEdit(player) || !mode.canUse()) {
            return InteractionResult.FAIL;
        }

        if (mode == Mode.QUICK_BUILD) {
            if (!player.getCommandSenderWorld().isClientSide) {
                if (player.isShiftKeyDown()) {
                    pos = pos.relative(face);
                }

                SelectionManager selManager = DimensionData.get(player.getCommandSenderWorld()).getSelectionManager();
                if (selManager.setPrimary(player, pos)) {
                    selManager.sync(player);
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }


    @Override
    public Component getName(ItemStack stack) {
        Mode mode = this.getMode(stack);
        MutableComponent label = new TranslatableComponent(this.getDescriptionId(stack));
        if (mode.getFeatureState() != State.RELEASE) {
            label = label.append(new TextComponent(" ("+mode.getFeatureState().letter+")").withStyle(ChatFormatting.RED));
        }
        return label;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId() + '.' + this.getMode(stack).getModeName();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Mode mode = this.getMode(stack);
        if (!mode.canUse()) {
            tooltip.add(new TranslatableComponent("item.mapmakingtools.wrench.in_development").withStyle(ChatFormatting.ITALIC));
        } else {
            tooltip.add(new TranslatableComponent(this.getDescriptionId(stack) + ".desc.1", new TranslatableComponent(this.getDescriptionId(stack) + ".word.primary").withStyle(ChatFormatting.YELLOW)));
            tooltip.add(new TranslatableComponent(this.getDescriptionId(stack) + ".desc.2", new TranslatableComponent(this.getDescriptionId(stack) + ".word.secondary").withStyle(ChatFormatting.AQUA)));
            tooltip.add(new TranslatableComponent(this.getDescriptionId(stack) + ".desc.3"));
        }
    }

    public static Mode getMode(ItemStack stack) {
        Mode mode;
        if (stack.hasTag() && (mode = Mode.getFromString(stack.getTag().getString("mode"))) != null) {
            return mode;
        }

        return Mode.QUICK_BUILD;
    }

    public enum Mode implements IFeatureState {
        QUICK_BUILD("quick_build", State.BETA),
        BLOCK_EDIT("block_edit", State.BETA),
        ENTITY_EDIT("entity_edit", State.DEVELOPMENT);

        String modeName;
        State state;

        Mode(String modeName, State state) {
            this.modeName = modeName;
            this.state = state;
        }

        public static Mode getFromString(String input) {
            for (Mode v : Mode.values()) {
                if (v.getModeName().equalsIgnoreCase(input)) {
                    return v;
                }
            }

            return null;
        }

        public String getModeName() {
            return this.modeName;
        }

        @Override
        public State getFeatureState() {
            return this.state;
        }
    }
}
