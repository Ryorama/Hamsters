package com.starfish_studios.hamsters.entities;

import com.starfish_studios.hamsters.blocks.HamsterWheelBlock;
import com.starfish_studios.hamsters.entities.util.RideableHamsterEntity;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SeatEntity extends Entity implements RideableHamsterEntity {

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
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity ridingEntity) {
        return this.findDismountLocation(this, ridingEntity, super.getDismountLocationForPassenger(ridingEntity));
    }

    @Override
    protected void addPassenger(@NotNull Entity passenger) {
        if (this.level().getBlockState(this.blockPosition()).getBlock() instanceof HamsterWheelBlock hamsterWheelBlock) passenger.setYRot(hamsterWheelBlock.setRiderRotation(passenger));
        super.addPassenger(passenger);
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