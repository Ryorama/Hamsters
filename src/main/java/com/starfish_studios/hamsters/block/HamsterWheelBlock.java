package com.starfish_studios.hamsters.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.starfish_studios.hamsters.block.entity.HamsterWheelBlockEntity;
import com.starfish_studios.hamsters.entity.Hamster;
import com.starfish_studios.hamsters.entity.SeatEntity;
import com.starfish_studios.hamsters.registry.HamstersBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class HamsterWheelBlock extends DirectionalKineticBlock implements IBE<HamsterWheelBlockEntity>{

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HamsterWheelBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    private static final VoxelShape NORTH = Block.box(1, 0, 3, 15, 16, 16);
    private static final VoxelShape SOUTH = Block.box(1, 0, 0, 15, 16, 13);
    private static final VoxelShape EAST = Block.box(0, 0, 1, 13, 16, 15);
    private static final VoxelShape WEST = Block.box(3, 0, 1, 16, 16, 15);

    public boolean isMountable() {
        return true;
    }

    public BlockPos primaryDismountLocation(BlockPos blockPos) {
        return blockPos;
    }

    public float setRiderRotation(Entity entity) {
        return entity.getYRot();
    }

    public static boolean isOccupied(Level level, BlockPos blockPos) {
        return !level.getEntitiesOfClass(SeatEntity.class, new AABB(blockPos)).isEmpty();
    }

    public float seatHeight() {
        return 0F;
    }

    public static Optional<Entity> getLeashed(Player player) {
        List<Entity> nearbyEntities = player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(10), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        for (Entity entities : nearbyEntities) if (entities instanceof Mob mob && mob.getLeashHolder() == player && canBePickedUp(entities)) return Optional.of(mob);
        return Optional.empty();
    }

    public static boolean ejectSeatedExceptPlayer(Level level, SeatEntity seatEntity) {
        if (seatEntity.getPassengers().isEmpty()) return false;
        if (!level.isClientSide()) seatEntity.ejectPassengers();
        return true;
    }

    public static boolean canBePickedUp(Entity passenger) {
        if (passenger instanceof Player) return false;
        return passenger instanceof LivingEntity;
    }

    public static void sitDown(Level level, BlockPos blockPos, Entity entity) {

        if (level.isClientSide() || entity == null) return;

        SeatEntity seatEntity = new SeatEntity(level, blockPos);
        level.addFreshEntity(seatEntity);
        entity.startRiding(seatEntity);

        level.updateNeighbourForOutputSignal(blockPos, level.getBlockState(blockPos).getBlock());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos) {
        return isOccupied(level, blockPos) ? 15 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public Class<HamsterWheelBlockEntity> getBlockEntityClass() {
        return HamsterWheelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HamsterWheelBlockEntity> getBlockEntityType() {
        return HamstersBlockEntities.HAMSTER_WHEEL;
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return HamstersBlockEntities.HAMSTER_WHEEL.create(blockPos, blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        InteractionResult defaultResult = super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);

        if (level.mayInteract(player, blockPos) && player.getItemInHand(interactionHand).isEmpty() && !player.isShiftKeyDown() && !player.isPassenger() && this.isMountable()) {

            if (isOccupied(level, blockPos)) {

                List<SeatEntity> seatEntities = level.getEntitiesOfClass(SeatEntity.class, new AABB(blockPos));

                if (seatEntities.get(0).getFirstPassenger() instanceof Hamster hamster) {
                    hamster.setWaitTimeWhenRunningTicks(0);
                    hamster.setWaitTimeBeforeRunTicks(hamster.getRandom().nextInt(200) + 600);
                }

                if (ejectSeatedExceptPlayer(level, seatEntities.get(0))) return InteractionResult.SUCCESS;
                return defaultResult;
            }

            if (getLeashed(player).isPresent() && getLeashed(player).get() instanceof Hamster hamster) sitDown(level, blockPos, hamster);
            return InteractionResult.SUCCESS;
        }

        return defaultResult;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isFaceSturdy(levelReader, blockPos.below(), Direction.UP);
    }

    @Override
    public boolean hasShaftTowards(LevelReader levelReader, BlockPos blockPos, BlockState blockState, Direction direction) {
        return direction == blockState.getValue(FACING).getOpposite();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean showCapacityWithAnnotation() {
        return true;
    }
}