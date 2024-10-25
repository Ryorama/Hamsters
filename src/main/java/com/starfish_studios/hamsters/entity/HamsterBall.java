package com.starfish_studios.hamsters.entity;

import com.starfish_studios.hamsters.entity.common.HamstersGeoEntity;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import com.starfish_studios.hamsters.registry.HamstersItems;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.Objects;

public class HamsterBall extends PathfinderMob implements HamstersGeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(0, ItemStack.EMPTY);

    protected static final RawAnimation ROLL = RawAnimation.begin().thenLoop("animation.sf_nba.hamster_ball.roll");

    public HamsterBall(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.SHULKER_HURT_CLOSED;
    }

    protected void positionRider(@NotNull Entity entity, Entity.@NotNull MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        if (entity instanceof Hamster) entity.setPos(this.getX(), this.getY() + 0.125f, this.getZ());
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.getFirstPassenger() instanceof Hamster hamster) return hamster;
        return null;
    }

    @Override
    public final @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {

        if (!this.level().isClientSide() && player.isShiftKeyDown()) {
            this.remove(RemovalReason.DISCARDED);
            this.spawnAtLocation(Objects.requireNonNull(this.getPickResult()));
            level().playSound(null, this.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 0.6F, ((this.level().getRandom().nextFloat() - this.level().getRandom().nextFloat()) * 0.2F + 1.0F));
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void travel(@NotNull Vec3 vec3) {
        float baseMovement = 0.91F;
        float horizontalMultiplier = this.onGround() ? baseMovement * 0.91F : 0.91F;
        Vec3 calculateMovement = this.handleRelativeFrictionAndCalculateMovement(vec3, baseMovement);
        this.setDeltaMovement(calculateMovement.x() * (double) (horizontalMultiplier), calculateMovement.y() * 0.9800000190734863, calculateMovement.z() * (double) horizontalMultiplier);
    }

    @Override
    protected void tickRidden(@NotNull Player player, @NotNull Vec3 vec3) {

        LivingEntity controllingPassenger = this.getControllingPassenger();
        assert controllingPassenger != null;

        if (controllingPassenger.getType() == HamstersEntityType.HAMSTER && controllingPassenger != player) {
            this.setRot(controllingPassenger.getYRot(), controllingPassenger.getXRot() * 0.5F);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        }

        super.tickRidden(player, vec3);
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(@NotNull Player player, @NotNull Vec3 vec3) {
        return new Vec3(0.0, 0.0, 1.0);
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player player) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.25);
    }

    // region Misc

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot equipmentSlot, @NotNull ItemStack itemStack) {
        this.verifyEquippedItem(itemStack);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(HamstersItems.BLUE_HAMSTER_BALL);
    }

    // endregion

    // region GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends HamsterBall> PlayState animController(final AnimationState<E> event) {

        if (event.isMoving() && this.getDeltaMovement().x() != 0 || this.getDeltaMovement().z() != 0) {
            event.setAnimation(ROLL);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // endregion
}