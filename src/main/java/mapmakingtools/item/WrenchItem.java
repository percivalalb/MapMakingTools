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

public class WrenchItem extends Item {

    public WrenchItem() {
        super(new Properties().isImmuneToFire().setNoRepair());
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        Mode mode = this.getMode(stack);
        if (!FeatureAvailability.canEdit(context.getPlayer()) || !mode.canUse()) {
            return ActionResultType.FAIL;
        }

        switch (mode) {
            case BLOCK_EDIT:
                BlockState state = context.getWorld().getBlockState(context.getPos());
                TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
                if (state.getBlock().matchesBlock(Blocks.SNOW)) {
                    int i = state.get(SnowBlock.LAYERS);
                    BlockState rotState = state.with(SnowBlock.LAYERS, context.getPlayer().isSneaking() ? ((i + 6) % 8) + 1 : i % 8 + 1);
                    if (!context.getWorld().isRemote) {
                        context.getWorld().setBlockState(context.getPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return ActionResultType.SUCCESS;
                }

                BlockState rotState = context.getPlayer().isSneaking()
                        ? state.mirror(Mirror.FRONT_BACK)
                        : state.rotate(context.getWorld(), context.getPos(), Rotation.CLOCKWISE_90);

                if (!rotState.equals(state)) {
                    if (!context.getWorld().isRemote) {
                        context.getWorld().setBlockState(context.getPos(), rotState, Constants.BlockFlags.DEFAULT);
                    }

                    return ActionResultType.SUCCESS;
                }

                return ActionResultType.FAIL;
            case QUICK_BUILD:
                if (!context.getWorld().isRemote) {
                    BlockPos pos = context.getPos();
                    if (context.getPlayer().isSneaking()) {
                        pos = pos.offset(context.getFace());
                    }

                    SelectionManager selManager = DimensionData.get(context.getWorld()).getSelectionManager();
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
            if (!player.getEntityWorld().isRemote) {
                if (player.isSneaking()) {
                    pos = pos.offset(face);
                }

                SelectionManager selManager = DimensionData.get(player.getEntityWorld()).getSelectionManager();
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
    public ITextComponent getDisplayName(ItemStack stack) {
        Mode mode = this.getMode(stack);
        IFormattableTextComponent label = new TranslationTextComponent(this.getTranslationKey(stack));
        if (mode.getFeatureState() != State.RELEASE) {
            label = label.appendSibling(new StringTextComponent(" ("+mode.getFeatureState().letter+")").mergeStyle(TextFormatting.RED));
        }
        return label;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey() + '.' + this.getMode(stack).getModeName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Mode mode = this.getMode(stack);
        if (!mode.canUse()) {
            tooltip.add(new TranslationTextComponent("item.mapmakingtools.wrench.in_development").mergeStyle(TextFormatting.ITALIC));
        } else {
            tooltip.add(new TranslationTextComponent(this.getTranslationKey(stack) + ".desc.1", new TranslationTextComponent(this.getTranslationKey(stack) + ".word.primary").mergeStyle(TextFormatting.YELLOW)));
            tooltip.add(new TranslationTextComponent(this.getTranslationKey(stack) + ".desc.2", new TranslationTextComponent(this.getTranslationKey(stack) + ".word.secondary").mergeStyle(TextFormatting.AQUA)));
            tooltip.add(new TranslationTextComponent(this.getTranslationKey(stack) + ".desc.3"));
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
