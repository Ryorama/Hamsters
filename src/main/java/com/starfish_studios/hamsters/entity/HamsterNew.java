package com.starfish_studios.hamsters.entity;

import com.google.common.collect.Lists;
import com.starfish_studios.hamsters.entity.common.MMPathNavigatorGround;
import com.starfish_studios.hamsters.entity.common.SmartBodyHelper;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import com.starfish_studios.hamsters.registry.HamstersSoundEvents;
import com.starfish_studios.hamsters.registry.HamstersTags;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.*;
import java.util.function.IntFunction;

public class HamsterNew extends ShoulderRidingEntity implements GeoEntity {

    // region Variables

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.walk");
    protected static final RawAnimation PINKIE_WALK = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.pinkie_walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.run");
    protected static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.sleep");
    protected static final RawAnimation STANDING = RawAnimation.begin().thenLoop("animation.sf_nba.hamster.standing");
    protected static final RawAnimation SQUISH = RawAnimation.begin().thenPlay("animation.sf_nba.hamster.squish")
    .thenPlayXTimes("animation.sf_nba.hamster.squished", 5).thenPlay("animation.sf_nba.hamster.unsquish");

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MARKING = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BOW_COLOR = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHEEK_LEVEL = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SQUISHED_TICKS = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(HamstersTags.HAMSTER_FOOD);

    // endregion

    public HamsterNew(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new MMPathNavigatorGround(this, level);
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new SmartBodyHelper(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.0, FOOD_ITEMS, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public void tick() {
                if (this.mob instanceof HamsterNew hamsterNew && hamsterNew.getSquishedTicks() > 0) return;
                super.tick();
            }
        });
        this.goalSelector.addGoal(9, new HamsterLookAroundGoal(this));
        // this.goalSelector.addGoal(10, new GetOnOwnersShoulderGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, @NotNull EntityDimensions dimensions) {
        return 0.3F;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(HamstersTags.HAMSTER_FOOD) && this.getSquishedTicks() <= 0;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damageAmount) {

        boolean original = super.hurt(damageSource, damageAmount);

        if (original && damageSource.is(DamageTypeTags.IS_FALL)) {
            this.setSquishedTicks(120);
            this.playSound(SoundEvents.SLIME_HURT, 1.0F, 1.0F);
        }

        return original;
    }

    @Override
    public void aiStep() {

        super.aiStep();

        if (this.getSquishedTicks() > 0) {
            this.setSquishedTicks(this.getSquishedTicks() - 1);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.getSquishedTicks() == 10) this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
        }

        if (this.level().isClientSide()) {
            if (this.getCheekLevel() == 2 && this.tickCount % 10 == 0) {
                this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(1.2), this.getZ(), 0.0D, 0.2D, 0.0D);
                this.playSound(HamstersSoundEvents.HAMSTER_BEG, 1.0F, 1.0F);
            } else if (this.getCheekLevel() == 3 && this.tickCount % 5 == 0) {
                this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(1.2), this.getZ(), 0.0D, 0.2D, 0.0D);
            }
        } else {
            if (this.isAlive() && this.getCheekLevel() >= 2 && this.tickCount % (80 - (this.getCheekLevel() * 10)) == 0) this.playSound(HamstersSoundEvents.HAMSTER_BEG, 1.0F, 1.0F);
        }
    }

    @SuppressWarnings("unused")
    private ItemStack getFirework(DyeColor dyeColor, int i) {

        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack itemStack2 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundTag = itemStack2.getOrCreateTagElement("Explosion");

        List<Integer> list = Lists.newArrayList();

        list.add(16711680); // Red
        list.add(16753920); // Orange
        list.add(16776960); // Yellow
        list.add(65280); // Green
        list.add(255); // Blue
        list.add(16711935); // Purple

        compoundTag.putIntArray("Colors", list);
        compoundTag.putByte("Type", (byte) FireworkRocketItem.Shape.SMALL_BALL.getId());
        CompoundTag compoundTag2 = itemStack.getOrCreateTagElement("Fireworks");
        ListTag listTag = new ListTag();
        CompoundTag compoundTag3 = itemStack2.getTagElement("Explosion");

        if (compoundTag3 != null) {
            listTag.add(compoundTag3);
        }

        CompoundTag compoundTag4 = itemStack2.getOrCreateTagElement("LifeTime");
        compoundTag4.putInt("LifeTime", 0);
        compoundTag2.putInt("Flight", -128);

        if (!listTag.isEmpty()) {
            compoundTag2.put("Explosions", listTag);
            compoundTag4.putInt("LifeTime", 0);
        }

        return itemStack;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand interactionHand) {

        ItemStack itemStack = player.getItemInHand(interactionHand);
        Item item = itemStack.getItem();

        if (this.level().isClientSide()) {

            boolean canInteract = this.isOwnedBy(player) || this.isTame() || itemStack.is(HamstersTags.HAMSTER_FOOD) && !this.isTame();
            return canInteract ? InteractionResult.CONSUME : InteractionResult.PASS;

        } else {

            if (this.isTame()) {

                if (this.isFood(itemStack)) {

                    // Feeding

                    if (player.getCooldowns().isOnCooldown(itemStack.getItem())) return InteractionResult.FAIL;
                    this.playSound(this.getEatingSound(itemStack));
                    if (!player.getAbilities().instabuild) itemStack.shrink(1);

                    if (this.getHealth() < this.getMaxHealth()) {

                        this.heal(this.getMaxHealth() / 4);

                    } else {

                        if (this.getCheekLevel() < 3) {

                            this.setCheekLevel(this.getCheekLevel() + 1);
                            player.getCooldowns().addCooldown(itemStack.getItem(), 20);

                        } else {

                            this.playSound(HamstersSoundEvents.HAMSTER_EXPLODE);
                            this.remove(RemovalReason.KILLED);

                            ItemStack fireworkStack = this.getFirework(Util.getRandom(DyeColor.values(), this.getRandom()), this.getRandom().nextInt(3));
                            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(this.level(), this, this.getX(), this.getEyeY(), this.getZ(), fireworkStack);

                            fireworkRocketEntity.setSilent(true);
                            fireworkRocketEntity.setInvisible(true);

                            this.level().addFreshEntity(fireworkRocketEntity);
                            fireworkRocketEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
                        }
                    }

                    return InteractionResult.SUCCESS;
                }

                else if (this.isOwnedBy(player)) {

                    // Bow Dyeing

                    if (item instanceof DyeItem dyeItem) {

                        DyeColor dyeColor = dyeItem.getDyeColor();

                        if (dyeColor != this.getBowColor()) {
                            this.setBowColor(dyeColor);
                            this.playSound(SoundEvents.DYE_USE);
                            if (!player.getAbilities().instabuild) itemStack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }

                    // Ordering to Sit

                    else if (this.isOwnedBy(player) && this.getSquishedTicks() <= 0) {

                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.getNavigation().stop();
                        return InteractionResult.SUCCESS;
                    }
                }

            } else if (this.isFood(itemStack)) {

                // Taming

                this.playSound(this.getEatingSound(itemStack));
                if (!player.getAbilities().instabuild) itemStack.shrink(1);

                if (this.getRandom().nextInt(3) == 0) {
                    this.tame(player);
                    this.getNavigation().stop();
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7); // Heart Particles
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6); // Smoke Particles
                }

                return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, interactionHand);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor serverLevelAccessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {

        this.populateDefaultEquipmentSlots(this.getRandom(), difficultyInstance);

        if (spawnGroupData == null) {
            this.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length - 1)]);
            this.setMarking(this.getRandom().nextInt(Marking.values().length));
        }

        return spawnGroupData;
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {

        HamsterNew hamster = HamstersEntityType.HAMSTER_NEW.create(serverLevel);
        assert hamster != null;

        // If both parents are Wild variant with no markings, the offspring will have a random color and marking.

        if (ageableMob instanceof HamsterNew hamsterParent && hamsterParent.getVariant() == Variant.WILD.getId() && hamsterParent.getMarking() == Marking.BLANK.getId()) {
            hamster.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length)]);
            hamster.setMarking(Marking.byId(this.getRandom().nextInt(Marking.values().length)).getId());
        }

        // If one parent is Wild, and one parent has color/marking, the offspring will have a 50% chance of a random color/marking or will have the same as the parent with color/marking.

        else if (ageableMob instanceof HamsterNew hamsterParent) {
            if (hamsterParent.getVariant() == Variant.WILD.getId() && hamsterParent.getMarking() == Marking.BLANK.getId()) {
                hamster.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length)]);
                hamster.setMarking(Marking.byId(this.getRandom().nextInt(Marking.values().length)).getId());
            } else {
                hamster.setVariant(Variant.getTypeById(hamsterParent.getVariant()));
                hamster.setMarking(hamsterParent.getMarking());
            }
        }

        // If neither parent is Wild, and they have their own color and marking, the offspring will pick a color and marking from one of the parents.

        else if (ageableMob instanceof HamsterNew hamsterParent) {
            hamster.setVariant(this.getOffspringVariant(this, hamsterParent));
            hamster.setMarking(this.getOffspringPattern(this, hamsterParent).getId());
        }

        return hamster;
    }

    private Marking getOffspringPattern(HamsterNew hamster, HamsterNew otherParent) {
        Marking marking = Marking.byId(hamster.getMarking());
        Marking otherMarking = Marking.byId(otherParent.getMarking());
        return this.getRandom().nextBoolean() ? marking : otherMarking;
    }

    private Variant getOffspringVariant(HamsterNew hamster, HamsterNew otherParent) {
        Variant variant = Variant.getTypeById(hamster.getVariant());
        Variant otherVariant = Variant.getTypeById(otherParent.getVariant());
        return this.getRandom().nextBoolean() ? variant : otherVariant;
    }

    @SuppressWarnings("unused")
    private void pathfindTowards(BlockPos blockPos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
        this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
    }

    // region Entity Data

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(VARIANT, 2);
        this.getEntityData().define(MARKING, 0);
        this.getEntityData().define(DATA_BOW_COLOR, 0);
        this.getEntityData().define(CHEEK_LEVEL, 0);
        this.getEntityData().define(SQUISHED_TICKS, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(Variant.BY_ID[compoundTag.getInt("Variant")]);
        this.setMarking(compoundTag.getInt("Marking"));
        this.setBowColor(DyeColor.byId(compoundTag.getInt("BowColor")));
        this.setCheekLevel(compoundTag.getInt("CheekLevel"));
        this.setSquishedTicks(compoundTag.getInt("SquishedTicks"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.getVariant());
        compoundTag.putInt("Marking", this.getMarking());
        compoundTag.putInt("BowColor", this.getBowColor().getId());
        compoundTag.putInt("CheekLevel", this.getCheekLevel());
        compoundTag.putInt("SquishedTicks", this.getSquishedTicks());
    }

    public int getSquishedTicks() {
        return this.getEntityData().get(SQUISHED_TICKS);
    }

    public void setSquishedTicks(int squishedTicks) {
        this.getEntityData().set(SQUISHED_TICKS, squishedTicks);
    }

    public DyeColor getBowColor() {
        return DyeColor.byId(this.entityData.get(DATA_BOW_COLOR));
    }

    public void setBowColor(DyeColor dyeColor) {
        this.getEntityData().set(DATA_BOW_COLOR, dyeColor.getId());
    }

    @SuppressWarnings("all")
    private boolean closerThan(BlockPos blockPos, int distance) {
        return !blockPos.closerThan(this.blockPosition(), distance);
    }

    @SuppressWarnings("unused")
    private boolean isTooFarAway(BlockPos blockPos) {
        return this.closerThan(blockPos, 32);
    }

    public int getMarking() {
        return this.entityData.get(MARKING);
    }

    public void setMarking(int marking) {
        this.entityData.set(MARKING, marking);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    public int getCheekLevel() {
        return this.entityData.get(CHEEK_LEVEL);
    }

    public void setCheekLevel(int cheekLevel) {
        this.entityData.set(CHEEK_LEVEL, cheekLevel);
    }

    public enum Marking {
        BLANK (0, "blank"),
        BANDED (1, "banded"),
        DOMINANT_SPOTS (2, "dominant_spots"),
        ROAN (3, "roan"),
        BELLY (4, "belly");

        private static final IntFunction<Marking> BY_ID = ByIdMap.continuous(Marking::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
        private final String name;

        @SuppressWarnings("unused")
        Marking(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public static Marking byId(int id) {
            return BY_ID.apply(id);
        }

        public String getName() {
            return this.name;
        }
    }

    public enum Variant {
        WHITE (0, "white"),
        CREAM (1, "cream"),
        CHAMPAGNE (2, "champagne"),
        SILVER_DOVE (3, "silver_dove"),
        DOVE (4, "dove"),
        CHOCOLATE (5, "chocolate"),
        BLACK (6, "black"),
        WILD (7, "wild");

        public static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;
        private final String name;

        @SuppressWarnings("unused")
        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Variant getTypeById(int id) {
            for (Variant type : values()) if (type.id == id) return type;
            return Variant.CHAMPAGNE;
        }
    }

    // endregion

    // region Sounds

    @Override
    public @NotNull SoundEvent getEatingSound(@NotNull ItemStack itemStack) {
        return SoundEvents.PARROT_EAT;
    }

    // endregion

    // region GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends HamsterNew> PlayState animController(final AnimationState<E> event) {
        if (this.getSquishedTicks() > 0) {
            event.setAnimation(SQUISH);
        } else if (this.isSleeping()) {
            event.setAnimation(SLEEP);
        } else if (this.isInSittingPose()) {
            event.setAnimation(STANDING);
        }  else if (event.isMoving()) {
            if (this.isSprinting()) {
                event.setControllerSpeed(1.3F);
                event.setAnimation(RUN);
            } else {
                if (this.isBaby()) {
                    event.setControllerSpeed(1.1F);
                    event.setAnimation(PINKIE_WALK);
                } else {
                    event.setControllerSpeed(1.1F);
                    event.setAnimation(WALK);
                }
            }
        }  else if (this.isPassenger() && this.getVehicle() instanceof SeatEntity) {
            event.setControllerSpeed(1.4F);
            event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // endregion

    // region Goals

    public static class HamsterLookAroundGoal extends RandomLookAroundGoal {

        private final Mob mob;

        public HamsterLookAroundGoal(Mob mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (this.mob instanceof HamsterNew hamsterNew && hamsterNew.getSquishedTicks() > 0) return;
            super.tick();
        }
    }

    @SuppressWarnings("unused")
    public static class GetOnOwnersShoulderGoal extends Goal {

        private final ShoulderRidingEntity entity;
        private ServerPlayer owner;
        private boolean isSittingOnShoulder;

        public GetOnOwnersShoulderGoal(ShoulderRidingEntity shoulderRidingEntity) {
            this.entity = shoulderRidingEntity;
        }

        public boolean canUse() {
            ServerPlayer serverPlayer = (ServerPlayer) this.entity.getOwner();
            boolean canSitOnPlayer = serverPlayer != null && !serverPlayer.isSpectator() && !serverPlayer.isInWater() && !serverPlayer.isInPowderSnow;
            return !this.entity.isOrderedToSit() && canSitOnPlayer && this.entity.canSitOnShoulder();
        }

        public boolean isInterruptable() {
            return !this.isSittingOnShoulder;
        }

        public void start() {
            this.owner = (ServerPlayer) this.entity.getOwner();
            this.isSittingOnShoulder = false;
        }

        public void tick() {
            if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed() && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
        }
    }

    // endregion
}