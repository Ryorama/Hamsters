package com.starfish_studios.hamsters.block.entity;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.starfish_studios.hamsters.block.HamsterWheelBlock;
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

    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.sf_nba.hamster_wheel.spin");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HamsterWheelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(HamstersBlockEntities.HAMSTER_WHEEL, blockPos, blockState);
    }

    @Override
    public float getGeneratedSpeed() {

        if (!(this.getBlockState().getBlock() instanceof HamsterWheelBlock)) return 0;
        assert this.level != null;

        if (HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) return -16;
        else return 0;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
    }

    @Override
    public void tick() {

        super.tick();
        assert this.level != null;

        if (HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) {
            this.updateGeneratedRotation();
        } else if (this.getGeneratedSpeed() == 0) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::controller));
    }

    private <E extends HamsterWheelBlockEntity> PlayState controller(final AnimationState<E> event) {

        assert this.level != null;

        if (HamsterWheelBlock.isOccupied(this.level, this.getBlockPos())) {
            event.getController().setAnimation(SPIN);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}