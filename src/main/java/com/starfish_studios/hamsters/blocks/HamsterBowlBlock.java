package com.starfish_studios.hamsters.blocks;

import com.starfish_studios.hamsters.blocks.util.HamsterBlock;
import com.starfish_studios.hamsters.registry.HamstersTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HamsterBowlBlock extends Block implements HamsterBlock, SimpleWaterloggedBlock {

    private static final int
        seedsMinValue = 0,
        seedsMaxValue = 3
    ;

    private static final IntegerProperty SEEDS = IntegerProperty.create("seeds", seedsMinValue, seedsMaxValue);

    public HamsterBowlBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SEEDS, seedsMinValue).setValue(WATERLOGGED, false));
    }

    // region Block State Initialization

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEEDS, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return Block.box(3, 0, 3, 13, 3, 13);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (HamsterBlock.isWaterlogged(blockState)) levelAccessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        return super.updateShape(blockState, direction, neighborState, levelAccessor, currentPos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateDefinition().any()
        .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState blockState) {
        if (HamsterBlock.isWaterlogged(blockState)) return Fluids.WATER.getSource(false);
        return super.getFluidState(blockState);
    }

    @SuppressWarnings("all")
    public static boolean hasSeeds(BlockState blockState) {
        return blockState.getValue(SEEDS) > seedsMinValue;
    }

    // endregion

    // region Interaction

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).is(HamstersTags.HAMSTER_BOWL_FOOD) && blockState.getValue(SEEDS) < seedsMaxValue) {
            level.playSound(player, blockPos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            addSeeds(level, blockPos, blockState);
            if (!player.getAbilities().instabuild) player.getItemInHand(interactionHand).shrink(1);
            return InteractionResult.SUCCESS;
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    public static void addSeeds(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.getBlockState(blockPos).getValue(SEEDS) >= seedsMaxValue) return;
        level.setBlockAndUpdate(blockPos, blockState.setValue(SEEDS, blockState.getValue(SEEDS) + 1));
    }

    public static void removeSeeds(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.getBlockState(blockPos).getValue(SEEDS) <= seedsMinValue) return;
        level.setBlockAndUpdate(blockPos, blockState.setValue(SEEDS, blockState.getValue(SEEDS) - 1));
    }

    // endregion

    // region Miscellaneous

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        return blockState.getValue(SEEDS);
    }

    // endregion
}