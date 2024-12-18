package com.starfish_studios.hamsters.entities;

import com.starfish_studios.hamsters.entities.util.RideableHamsterEntity;
import com.starfish_studios.hamsters.items.HamsterItem;
import com.starfish_studios.hamsters.registry.HamstersItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import javax.annotation.Nullable;
import java.util.Objects;

public class HamsterBall extends LivingEntity implements GeoEntity, RideableHamsterEntity {

    // region Initialization

    public long lastHit;

    public HamsterBall(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0F);
        this.blocksBuilding = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    // endregion

    // region Entity Data

    private static final EntityDataAccessor<Integer>
        COLOR = SynchedEntityData.defineId(HamsterBall.class, EntityDataSerializers.INT)
    ;

    private final String
        colorTag = "color"
    ;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(COLOR, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(this.colorTag, this.getColor().getId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setColor(DyeColor.byId(compoundTag.getInt(this.colorTag)));
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(COLOR));
    }

    public void setColor(DyeColor dyeColor) {
        this.getEntityData().set(COLOR, dyeColor.getId());
    }

    // endregion

    // region Interactions

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec3, @NotNull InteractionHand interactionHand) {

        InteractionResult original = super.interactAt(player, vec3, interactionHand);
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (itemInHand.is(Items.NAME_TAG)) return original;

        if (!player.level().isClientSide() && player.getAbilities().mayBuild) {

            boolean success = false;

            if (player.getMainHandItem().isEmpty()) {

                if (player.isShiftKeyDown() && this.getPassengers().isEmpty()) {
                    this.breakHamsterBall();
                    success = true;
                } else if (this.getFirstPassenger() != null) {
                    this.getFirstPassenger().stopRiding();
                    success = true;
                }

            } else if (itemInHand.getItem() instanceof HamsterItem hamsterItem) {
                hamsterItem.spawnHamster(this.level(), this.blockPosition(), itemInHand, player, this);
                success = true;
            }

            if (success) return InteractionResult.SUCCESS;
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
            if (damageSource.getSourcePosition() != null) this.knockback(0.4F, Objects.requireNonNull(damageSource.getSourcePosition()).x() - this.getX(), Objects.requireNonNull(damageSource.getSourcePosition()).z() - this.getZ());
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

        if (this.level().isClientSide()) return;

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

    private Item getDropItem() {
        return switch (this.getColor().getId()) {
            case 1 -> HamstersItems.ORANGE_HAMSTER_BALL;
            case 2 -> HamstersItems.MAGENTA_HAMSTER_BALL;
            case 3 -> HamstersItems.LIGHT_BLUE_HAMSTER_BALL;
            case 4 -> HamstersItems.YELLOW_HAMSTER_BALL;
            case 5 -> HamstersItems.LIME_HAMSTER_BALL;
            case 6 -> HamstersItems.PINK_HAMSTER_BALL;
            case 7 -> HamstersItems.GRAY_HAMSTER_BALL;
            case 8 -> HamstersItems.LIGHT_GRAY_HAMSTER_BALL;
            case 9 -> HamstersItems.CYAN_HAMSTER_BALL;
            case 10 -> HamstersItems.PURPLE_HAMSTER_BALL;
            case 11 -> HamstersItems.BLUE_HAMSTER_BALL;
            case 12 -> HamstersItems.BROWN_HAMSTER_BALL;
            case 13 -> HamstersItems.GREEN_HAMSTER_BALL;
            case 14 -> HamstersItems.RED_HAMSTER_BALL;
            case 15 -> HamstersItems.BLACK_HAMSTER_BALL;
            default -> HamstersItems.WHITE_HAMSTER_BALL;
        };
    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity ridingEntity) {
        return this.findDismountLocation(this, ridingEntity, super.getDismountLocationForPassenger(ridingEntity));
    }

    // endregion

    // region Movement

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public @NotNull Vec3 handleRelativeFrictionAndCalculateMovement(@NotNull Vec3 vec3, float friction) {
        return super.handleRelativeFrictionAndCalculateMovement(vec3, 0.98F);
    }

    @Nullable @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : super.getControllingPassenger();
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() * 0.2D;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return true;
    }

    // endregion

    // region Misc

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
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