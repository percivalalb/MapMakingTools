package mapmakingtools.item;

import mapmakingtools.api.util.FeatureAvailability;
import mapmakingtools.api.util.IFeatureState;
import mapmakingtools.api.util.State;
import mapmakingtools.storage.DimensionData;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class WrenchItem extends Item {

    public WrenchItem() {
        super(new Properties().fireResistant().setNoRepair());
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        Mode mode = this.getMode(stack);
        if (!FeatureAvailability.canEdit(context.getPlayer()) || !mode.canUse()) {
            return ActionResultType.FAIL;
        }

        switch (mode) {
            case BLOCK_EDIT:
                BlockState state = context.getLevel().getBlockState(context.getClickedPos());
                TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
                if (state.getBlock().is(Blocks.SNOW)) {
                    int i = state.getValue(SnowBlock.LAYERS);
                    BlockState rotState = state.setValue(SnowBlock.LAYERS, context.getPlayer().isShiftKeyDown() ? ((i + 6) % 8) + 1 : i % 8 + 1);
                    if (!context.getLevel().isClientSide) {
                        context.getLevel().setBlock(context.getClickedPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return ActionResultType.SUCCESS;
                }

                BlockState rotState = context.getPlayer().isShiftKeyDown()
                        ? state.mirror(Mirror.FRONT_BACK)
                        : state.rotate(context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);

                if (!rotState.equals(state)) {
                    if (!context.getLevel().isClientSide) {
                        context.getLevel().setBlock(context.getClickedPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return ActionResultType.SUCCESS;
                }

                return ActionResultType.FAIL;
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
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.FAIL;
            case ENTITY_EDIT:
                return ActionResultType.FAIL;
        }

        return null;
    }

    public ActionResultType onBlockStartBreak(ItemStack stack, BlockPos pos, Direction face, PlayerEntity player) {
        Mode mode = this.getMode(stack);
        if (!FeatureAvailability.canEdit(player) || !mode.canUse()) {
            return ActionResultType.FAIL;
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

                return ActionResultType.SUCCESS;
            }
            return ActionResultType.FAIL;
        }
        return ActionResultType.PASS;
    }


    @Override
    public ITextComponent getName(ItemStack stack) {
        Mode mode = this.getMode(stack);
        IFormattableTextComponent label = new TranslationTextComponent(this.getDescriptionId(stack));
        if (mode.getFeatureState() != State.RELEASE) {
            label = label.append(new StringTextComponent(" ("+mode.getFeatureState().letter+")").withStyle(TextFormatting.RED));
        }
        return label;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId() + '.' + this.getMode(stack).getModeName();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Mode mode = this.getMode(stack);
        if (!mode.canUse()) {
            tooltip.add(new TranslationTextComponent("item.mapmakingtools.wrench.in_development").withStyle(TextFormatting.ITALIC));
        } else {
            tooltip.add(new TranslationTextComponent(this.getDescriptionId(stack) + ".desc.1", new TranslationTextComponent(this.getDescriptionId(stack) + ".word.primary").withStyle(TextFormatting.YELLOW)));
            tooltip.add(new TranslationTextComponent(this.getDescriptionId(stack) + ".desc.2", new TranslationTextComponent(this.getDescriptionId(stack) + ".word.secondary").withStyle(TextFormatting.AQUA)));
            tooltip.add(new TranslationTextComponent(this.getDescriptionId(stack) + ".desc.3"));
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
