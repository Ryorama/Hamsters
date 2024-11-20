package com.starfish_studios.hamsters.entities;

import com.starfish_studios.hamsters.blocks.HamsterWheelBlock;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SeatEntity extends Entity {

    // region Initialization

    public SeatEntity(Level level) {
        super(HamstersEntityTypes.SEAT.get(), level);
        this.noPhysics = true;
    }

    public SeatEntity(Level level, BlockPos blockPos) {
        this(level);
        this.setPos(blockPos.getX() + 0.5D, blockPos.getY() + 0.01D, blockPos.getZ() + 0.5D);
    }

    // endregion

    // region Entity Data

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {}

    // endregion

    // region Ticking

    @Override
    public void tick() {

        if (this.level().isClientSide()) return;

        boolean canMount;

        if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) canMount = hamsterWheelBlock.isMountable();
        else canMount = false;

        if (this.isVehicle() && canMount) return;

        this.discard();
        this.level().updateNeighbourForOutputSignal(this.blockPosition(), this.level().getBlockState(this.blockPosition()).getBlock());
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.0D;
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity entity) {

        BlockPos blockPos = this.blockPosition();
        Vec3 safeVec;

        if (this.level().getBlockState(blockPos).getBlock() instanceof HamsterWheelBlock) {
            safeVec = DismountHelper.findSafeDismountLocation(entity.getType(), this.level(), blockPos, false);
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

    // endregion

    // region Miscellaneous

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        return true;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    // endregion
}