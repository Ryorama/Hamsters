package com.starfish_studios.hamsters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HamsterBowlBlock extends Block {

    private static final IntegerProperty SEEDS = IntegerProperty.create("seeds", 0, 3);

    public HamsterBowlBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SEEDS, 0));
    }

    @SuppressWarnings("all")
    public static boolean hasSeeds(BlockState blockState) {
        return blockState.getValue(SEEDS) > 0;
    }

    public static void addSeeds(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.getBlockState(blockPos).getValue(SEEDS) >= 3) return;
        level.setBlockAndUpdate(blockPos, blockState.setValue(SEEDS, blockState.getValue(SEEDS) + 1));
    }

    public static void removeSeeds(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.getBlockState(blockPos).getValue(SEEDS) <= 0) return;
        level.setBlockAndUpdate(blockPos, blockState.setValue(SEEDS, blockState.getValue(SEEDS) - 1));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).is(Items.WHEAT_SEEDS) && blockState.getValue(SEEDS) < 3) {
            level.playSound(player, blockPos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
            addSeeds(level, blockPos, blockState);
            if (!player.getAbilities().instabuild) player.getItemInHand(interactionHand).shrink(1);
            return InteractionResult.SUCCESS;
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEEDS);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return Block.box(3, 0, 3, 13, 3, 13);
    }
}