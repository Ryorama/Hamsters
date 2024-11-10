package com.starfish_studios.hamsters.block;

import com.starfish_studios.hamsters.block.properties.CageType;
import com.starfish_studios.hamsters.registry.HamstersTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CagePanelBlock extends Block {

    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final EnumProperty<CageType> TYPE = EnumProperty.create("type", CageType.class);

    private static final VoxelShape NORTH_AABB = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SOUTH_AABB = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape EAST_AABB = Block.box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape WEST_AABB = Block.box(15, 0, 0, 16, 16, 16);

    private static final VoxelShape NORTH_COLLISION_AABB = Block.box(0, 0, 15, 16, 24, 16);
    private static final VoxelShape SOUTH_COLLISION_AABB = Block.box(0, 0, 0, 16, 24, 1);
    private static final VoxelShape EAST_COLLISION_AABB = Block.box(0, 0, 0, 1, 24, 16);
    private static final VoxelShape WEST_COLLISION_AABB = Block.box(15, 0, 0, 16, 24, 16);

    public CagePanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE, CageType.NONE).setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return switch (blockState.getValue(FACING)) {
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            default -> NORTH_AABB;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return switch (blockState.getValue(FACING)) {
            case SOUTH -> SOUTH_COLLISION_AABB;
            case EAST -> EAST_COLLISION_AABB;
            case WEST -> WEST_COLLISION_AABB;
            default -> NORTH_COLLISION_AABB;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, BlockPos blockPos, Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        BlockPos abovePos = blockPos.above();

        if (player.getItemInHand(interactionHand).is(HamstersTags.CAGE_PANEL_ITEMS) && level.isEmptyBlock(abovePos)) {
            BlockItem blockItem = (BlockItem) player.getItemInHand(interactionHand).getItem();
            level.setBlock(abovePos, blockItem.getBlock().defaultBlockState().setValue(FACING, blockState.getValue(FACING)), 3);
            level.playSound(null, blockPos, this.getSoundType(blockState).getPlaceSound(), SoundSource.BLOCKS, level.getRandom().nextFloat() * 0.25F + 0.7F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            return InteractionResult.SUCCESS;
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getHorizontalDirection().getOpposite();

        BlockState blockState = this.defaultBlockState().setValue(FACING, direction);
        blockState = blockState.setValue(TYPE, this.getType(blockState, this.getRelativeTop(level, blockPos), this.getRelativeBottom(level, blockPos)));
        return blockState;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(@NotNull BlockState blockState, BlockState neighborState, @NotNull Direction direction) {
        return neighborState.getBlock() instanceof CagePanelBlock && neighborState.getValue(FACING) == blockState.getValue(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {

        if (level.isClientSide()) return;

        CageType cageType = this.getType(blockState, this.getRelativeTop(level, blockPos), this.getRelativeBottom(level, blockPos));
        if (blockState.getValue(TYPE) == cageType) return;
        blockState = blockState.setValue(TYPE, cageType);
        level.setBlockAndUpdate(blockPos, blockState);
    }

    private BlockState getRelativeTop(Level level, BlockPos blockPos) {
        return level.getBlockState(blockPos.above());
    }

    private BlockState getRelativeBottom(Level level, BlockPos blockPos) {
        return level.getBlockState(blockPos.below());
    }

    private CageType getType(BlockState blockState, BlockState aboveState, BlockState belowState) {

        boolean shape_above_same = aboveState.getBlock() instanceof CagePanelBlock && blockState.getValue(FACING) == aboveState.getValue(FACING);
        boolean shape_below_same = belowState.getBlock() instanceof CagePanelBlock && blockState.getValue(FACING) == belowState.getValue(FACING);

        if (shape_above_same && !shape_below_same) return CageType.BOTTOM;
        else if (!shape_above_same && shape_below_same) return CageType.TOP;
        else if (shape_above_same) return CageType.MIDDLE;
        return CageType.NONE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, FACING);
    }
}