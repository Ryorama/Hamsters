package com.starfish_studios.hamsters.entity;

import com.starfish_studios.hamsters.block.HamsterWheelBlock;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class SeatEntity extends Entity {

    public SeatEntity(Level level) {
        super(HamstersEntityType.SEAT.get(), level);
        this.noPhysics = true;
    }

    public SeatEntity(Level level, BlockPos pos) {
        this(level);
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.01, pos.getZ() + 0.5);
    }

    @Override
    public void tick() {

        if (this.level().isClientSide()) return;

        BlockState blockState = this.level().getBlockState(this.blockPosition());
        boolean canMount;

        if (blockState.getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) canMount = hamsterWheelBlock.isMountable();
        else canMount = false;
        if (this.isVehicle() && canMount) return;

        this.discard();
        this.level().updateNeighbourForOutputSignal(this.blockPosition(), this.level().getBlockState(this.blockPosition()).getBlock());
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    public double getPassengersRidingOffset() {

        List<Entity> passengers = this.getPassengers();

        if (passengers.isEmpty()) return 0.0;
        double seatHeight = 0.0;

        BlockState blockState = this.level().getBlockState(this.blockPosition());
        if (blockState.getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) seatHeight = hamsterWheelBlock.seatHeight();

        return seatHeight;
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        return true;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity entity) {

        BlockPos blockPos = this.blockPosition();
        Vec3 safeVec;
        BlockState blockState = this.level().getBlockState(blockPos);

        if (blockState.getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) {
            safeVec = DismountHelper.findSafeDismountLocation(entity.getType(), this.level(), hamsterWheelBlock.primaryDismountLocation(blockPos), false);
            if (safeVec != null) return safeVec.add(0, 0.25, 0);
        }

        Direction original = this.getDirection();
        Direction[] offsets = {original, original.getClockWise(), original.getCounterClockWise(), original.getOpposite()};

        for (Direction direction : offsets) {
            safeVec = DismountHelper.findSafeDismountLocation(entity.getType(), this.level(), blockPos.relative(direction), false);
            if (safeVec != null) return safeVec.add(0, 0.25, 0);
        }

        return super.getDismountLocationForPassenger(entity);
    }

    @Override
    protected void addPassenger(@NotNull Entity passenger) {
        if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) passenger.setYRot(hamsterWheelBlock.setRiderRotation(passenger));
        super.addPassenger(passenger);
    }

    @Override
    protected void removePassenger(@NotNull Entity entity) {
        super.removePassenger(entity);
        if (entity instanceof TamableAnimal tamableAnimal) tamableAnimal.setInSittingPose(false);
    }
}