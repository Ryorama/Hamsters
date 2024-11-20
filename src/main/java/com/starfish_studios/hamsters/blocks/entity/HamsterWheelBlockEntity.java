package com.starfish_studios.hamsters.blocks.entity;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.starfish_studios.hamsters.blocks.HamsterWheelBlock;
import com.starfish_studios.hamsters.registry.HamstersBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HamsterWheelBlockEntity extends GeneratingKineticBlockEntity implements GeoBlockEntity {

    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.sf_hba.hamster_wheel.spin");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HamsterWheelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(HamstersBlockEntities.HAMSTER_WHEEL, blockPos, blockState);
    }

    // region Ticking

    @Override
    public void tick() {

        super.tick();
        if (this.level == null) return;

        if (HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) {
            this.updateGeneratedRotation();
        } else if (this.getGeneratedSpeed() == 0) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (this.level != null && this.getBlockState().getBlock() instanceof HamsterWheelBlock && HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) return -16;
        return 0;
    }

    // endregion

    // region GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::controller));
    }

    private <E extends HamsterWheelBlockEntity> PlayState controller(final AnimationState<E> event) {

        if (this.level != null && HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) {
            event.getController().setAnimation(SPIN);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // endregion
}