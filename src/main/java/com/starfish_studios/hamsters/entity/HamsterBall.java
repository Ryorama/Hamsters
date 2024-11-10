package com.starfish_studios.hamsters.entity;

import com.starfish_studios.hamsters.entity.common.HamstersGeoEntity;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import com.starfish_studios.hamsters.registry.HamstersItems;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.Objects;

public class HamsterBall extends LivingEntity implements HamstersGeoEntity {

    public long lastHit;

    public HamsterBall(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec3, @NotNull InteractionHand interactionHand) {

        InteractionResult original = super.interactAt(player, vec3, interactionHand);
        if (player.getItemInHand(interactionHand).is(Items.NAME_TAG)) return original;

        if (player.getAbilities().mayBuild && player.isShiftKeyDown()) {
            this.breakHamsterBall();
            return InteractionResult.SUCCESS;
        }

        return original;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damageAmount) {

        Entity entity = damageSource.getEntity();

        if (this.level().isClientSide() || this.isRemoved() || this.isInvulnerableTo(damageSource) || entity instanceof Player player && !player.getAbilities().mayBuild) return false;

        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            this.breakHamsterBall();
            return false;
        }

        if (damageSource.isCreativePlayer()) {
            this.breakHamsterBall();
            return damageSource.getDirectEntity() instanceof AbstractArrow abstractArrow && abstractArrow.getPierceLevel() > 0;
        }

        long gameTime = this.level().getGameTime();

        if (gameTime - this.lastHit <= 5L || damageSource.getDirectEntity() instanceof AbstractArrow) {
            this.breakHamsterBall();
        } else {
            this.knockback(0.4F, Objects.requireNonNull(damageSource.getSourcePosition()).x() - this.getX(), Objects.requireNonNull(damageSource.getSourcePosition()).z() - this.getZ());
            this.level().broadcastEntityEvent(this, (byte) 32);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
            this.lastHit = gameTime;
        }

        return true;
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    private void breakHamsterBall() {

        ItemStack itemStack = Objects.requireNonNull(this.getPickResult()).getItem().getDefaultInstance();
        if (this.hasCustomName()) itemStack.setHoverName(this.getCustomName());
        Block.popResource(this.level(), this.blockPosition(), itemStack);

        this.playSound(SoundEvents.CHICKEN_EGG, 0.5F, 1.0F + (this.level().getRandom().nextFloat() - this.level().getRandom().nextFloat()) * 0.2F);
        this.kill();
    }

    @Override
    public void handleEntityEvent(byte entityEvent) {
        if (entityEvent == 32 && this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SHULKER_HURT_CLOSED, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level().getGameTime();
        } else {
            super.handleEntityEvent(entityEvent);
        }
    }

    // region Movement

    @Override
    public void travel(@NotNull Vec3 vec3) {
        if (this.onGround()) this.setDeltaMovement(this.getDeltaMovement().multiply(1.2D, 1.0D, 1.2D));
        super.travel(vec3);
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(@NotNull Player player, @NotNull Vec3 vec3) {
        return new Vec3(0.0D, 0.0D, 1.0D);
    }

    @Override
    protected void positionRider(@NotNull Entity entity, Entity.@NotNull MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        if (entity instanceof Hamster) entity.setPos(this.getX(), this.getY() + 0.125F, this.getZ());
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

    // endregion

    // region Misc

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(HamstersItems.LIGHT_BLUE_HAMSTER_BALL);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot equipmentSlot, @NotNull ItemStack itemStack) {}

    @Override
    public void thunderHit(@NotNull ServerLevel serverLevel, @NotNull LightningBolt lightningBolt) {}

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    // endregion

    // region GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return GeckoLibUtil.createInstanceCache(this);
    }

    // endregion
}