package com.starfish_studios.hamsters.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.starfish_studios.hamsters.blocks.util.HamsterBlock;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

@SuppressWarnings("unused")
public class TunnelBlock extends Block implements HamsterBlock, SimpleWaterloggedBlock {

    private static final BooleanProperty
        NORTH = BlockStateProperties.NORTH,
        SOUTH = BlockStateProperties.SOUTH,
        EAST = BlockStateProperties.EAST,
        WEST = BlockStateProperties.WEST,
        UP = BlockStateProperties.UP,
        DOWN = BlockStateProperties.DOWN
    ;

    @SuppressWarnings("rawtypes, unchecked")
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf((Map) Util.make(Maps.newEnumMap(Direction.class), (enumMap) -> {
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.WEST, WEST);
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
    }));

    private static final VoxelShape
        SOLID_LEFT = Block.box(0, 0, 0, 2, 16, 16),
        SOLID_RIGHT = Block.box(14, 0, 0, 16, 16, 16),
        SOLID_FRONT = Block.box(0, 0, 0, 16, 16, 2),
        SOLID_BACK = Block.box(0, 0, 14, 16, 16, 16),
        SOLID_TOP = Block.box(0, 14, 0, 16, 16, 16),
        SOLID_BOTTOM = Block.box(0, 0, 0, 16, 2, 16)
    ;

    public TunnelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
        .setValue(NORTH, false)
        .setValue(SOUTH, false)
        .setValue(EAST, false)
        .setValue(WEST, false)
        .setValue(UP, false)
        .setValue(DOWN, false)
        .setValue(WATERLOGGED, false));
    }

    // region Block State Initialization

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return Shapes.block();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {

        // TODO: This should be a modular system that can handle any combination of tunnels, but it's not working properly.

        VoxelShape shape = Shapes.empty();

        if (blockState.getValue(SOUTH) || blockState.getValue(NORTH)) shape = Shapes.or(shape, SOLID_LEFT, SOLID_RIGHT);
        if (blockState.getValue(EAST) || blockState.getValue(WEST)) shape = Shapes.or(shape, SOLID_FRONT, SOLID_BACK);
        if (!blockState.getValue(UP) || !blockState.getValue(DOWN)) shape = Shapes.or(shape, SOLID_TOP, SOLID_BOTTOM);

        return shape;
    }

    // TODO: This may be why the collisions aren't working properly, but I haven't played with this part yet.

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (HamsterBlock.isWaterlogged(blockState)) levelAccessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        return blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), neighborState.is(this));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateForPlacement(context.getLevel(), context.getClickedPos())
        .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    private BlockState getStateForPlacement(BlockGetter blockGetter, BlockPos blockPos) {

        // This determines if it is next to another tunnel block.

        BlockState northState = blockGetter.getBlockState(blockPos.north());
        BlockState southState = blockGetter.getBlockState(blockPos.south());
        BlockState eastState = blockGetter.getBlockState(blockPos.east());
        BlockState westState = blockGetter.getBlockState(blockPos.west());
        BlockState aboveState = blockGetter.getBlockState(blockPos.above());
        BlockState belowState = blockGetter.getBlockState(blockPos.below());

        return this.getStateDefinition().any()
        .setValue(NORTH, northState.is(this))
        .setValue(SOUTH, southState.is(this))
        .setValue(EAST, eastState.is(this))
        .setValue(WEST, westState.is(this))
        .setValue(UP, aboveState.is(this))
        .setValue(DOWN, belowState.is(this));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState blockState) {
        if (HamsterBlock.isWaterlogged(blockState)) return Fluids.WATER.getSource(false);
        return super.getFluidState(blockState);
    }

    // endregion

    // region Interaction

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).isEmpty() && !player.isShiftKeyDown()) {
            player.teleportTo(blockPos.getX() + 0.5D, blockPos.getY() + 0.2D, blockPos.getZ() + 0.5D);
            return InteractionResult.SUCCESS;
        }

        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    // endregion

    // region Miscellaneous

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        // TODO: Having a hard time getting this to work without launching the entity while they're in a corner.
        // entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.2F, 1.0F, 1.2F));
    }

    // This is for their final glassy look and making their model culling work properly.

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(@NotNull BlockState blockState, BlockState neighborState, @NotNull Direction direction) {
        return neighborState.is(this) || super.skipRendering(blockState, neighborState, direction);
    }

    // endregion
}