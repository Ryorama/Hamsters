package com.starfish_studios.hamsters.entity;

import com.google.common.collect.Lists;
import com.starfish_studios.hamsters.block.HamsterBowlBlock;
import com.starfish_studios.hamsters.block.HamsterWheelBlock;
import com.starfish_studios.hamsters.entity.common.MMPathNavigatorGround;
import com.starfish_studios.hamsters.entity.common.SleepGoal;
import com.starfish_studios.hamsters.entity.common.SleepingAnimal;
import com.starfish_studios.hamsters.entity.common.SmartBodyHelper;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import com.starfish_studios.hamsters.registry.HamstersItems;
import com.starfish_studios.hamsters.registry.HamstersSoundEvents;
import com.starfish_studios.hamsters.registry.HamstersTags;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
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
import static com.starfish_studios.hamsters.HamstersConfig.*;

public class HamsterNew extends ShoulderRidingEntity implements GeoEntity, SleepingAnimal {

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
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SLEEPING_COOLDOWN_TICKS = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EATING_COOLDOWN_TICKS = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHEEK_LEVEL = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SQUISHED_TICKS = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BIRTH_COUNTDOWN = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_HAND = SynchedEntityData.defineId(HamsterNew.class, EntityDataSerializers.BOOLEAN);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(HamstersTags.HAMSTER_FOOD);

    // endregion

    public HamsterNew(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CAUTIOUS, -1.0F);
    }

    @SuppressWarnings("unused")
    public static boolean checkHamsterNewSpawnRules(EntityType<? extends TamableAnimal> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getBlockState(blockPos.below()).is(HamstersTags.HAMSTERS_SPAWNABLE_ON) && isBrightEnoughToSpawn(levelAccessor, blockPos);
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
    public SoundEvent getAmbientSound() {
        return HamstersSoundEvents.HAMSTER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return HamstersSoundEvents.HAMSTER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return HamstersSoundEvents.HAMSTER_DEATH;
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3D) {
            @Override
            public boolean canUse() {
                if (this.mob instanceof HamsterNew hamster && (hamster.getSquishedTicks() > 0 || hamster.isInSittingPose())) return false;
                return super.canUse();
            }
        });

        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new HamsterSleepGoal(this));
        this.goalSelector.addGoal(5, new HamsterTemptGoal(this, 1.0D, FOOD_ITEMS, true));

        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.3D, 1.5D, livingEntity -> livingEntity instanceof Player ? !this.isTame() : livingEntity.getType().is(HamstersTags.HAMSTER_AVOIDED)));

        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public void tick() {
                if (this.mob instanceof HamsterNew hamster && hamster.getSquishedTicks() > 0) return;
                super.tick();
            }
        });

        this.goalSelector.addGoal(10, new HamsterGoToBottleGoal(this, 1.2D, 8));
        this.goalSelector.addGoal(10, new HamsterGoToBowlGoal(this, 1.2D, 8));
        this.goalSelector.addGoal(10, new HamsterGoToWheelGoal(this, 1.2D, 8));

        this.goalSelector.addGoal(10, new HamsterLookAroundGoal(this));
        // this.goalSelector.addGoal(10, new GetOnOwnersShoulderGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, @NotNull EntityDimensions dimensions) {
        return 0.3F;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(HamstersTags.HAMSTER_FOOD) && this.getSquishedTicks() <= 0;
    }

    public void squishHamster() {

        this.setSleeping(false);

        if (this.getSquishedTicks() <= 0) {
            this.setSquishedTicks(120);
            this.playSound(SoundEvents.SLIME_HURT, 1.0F, 1.0F);
        }

        if (this.getCheekLevel() > 0) {

            for (int seedItems = 0; seedItems < this.getCheekLevel(); seedItems++) {
                ItemEntity seedsItem = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WHEAT_SEEDS));
                seedsItem.setDeltaMovement(this.getRandom().nextGaussian() * 0.1, this.getRandom().nextGaussian() * 0.2 + 0.2, this.getRandom().nextGaussian() * 0.1);
                seedsItem.setPickUpDelay(20);
                this.level().addFreshEntity(seedsItem);
            }

            this.setCheekLevel(0);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damageAmount) {

        boolean original = super.hurt(damageSource, damageAmount);

        if (original) {
            if (damageSource.is(HamstersTags.SQUISHES_HAMSTERS)) this.squishHamster();
            else this.setSleeping(false);
        }

        return original;
    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.3D);
        } else {
            this.setSprinting(false);
        }

        super.customServerAiStep();
    }

    @Override
    public void aiStep() {

        super.aiStep();

        if (hamstersSquish) {
            for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox())) {
                if (!player.onGround() && player.getDeltaMovement().y() < 0) {
                    if (jumpHurtsHamsters && EnchantmentHelper.getEnchantmentLevel(Enchantments.FALL_PROTECTION, player) == 0 && this.getSquishedTicks() <= 0 && !this.hasCustomName()) this.hurt(damageSources().generic(), 1.0F);
                    this.squishHamster();
                }
            }
        }

        if (this.getSquishedTicks() > 0) {
            this.setSquishedTicks(this.getSquishedTicks() - 1);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.getSquishedTicks() == 10) this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
        }

        if (getNearbyAvoidedEntities(this).isEmpty() && this.getSleepingCooldownTicks() > 0) this.setSleepingCooldownTicks(this.getSleepingCooldownTicks() - 1);
        if (this.getBirthCountdown() > 0) this.setBirthCountdown(this.getBirthCountdown() - 1);
        if (this.getEatingCooldownTicks() > 0) this.setEatingCooldownTicks(this.getEatingCooldownTicks() - 1);

        if (hamstersBurst) {
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

        if (this.isInWheel() && this.getCheekLevel() > 0 && this.tickCount % 100 == 0) this.setCheekLevel(this.getCheekLevel() - 1);
    }

    @SuppressWarnings("unused")
    public static ItemStack getFirework(DyeColor dyeColor, int i) {

        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack fireworkStar = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundTag = fireworkStar.getOrCreateTagElement("Explosion");

        List<Integer> list = Lists.newArrayList();

        list.add(16711680); // Red
        list.add(16753920); // Orange
        list.add(16776960); // Yellow
        list.add(65280); // Green
        list.add(255); // Blue
        list.add(16711935); // Purple

        compoundTag.putIntArray("Colors", list);
        compoundTag.putByte("Type", (byte) FireworkRocketItem.Shape.SMALL_BALL.getId());
        CompoundTag fireworksTag = itemStack.getOrCreateTagElement("Fireworks");
        ListTag listTag = new ListTag();

        CompoundTag explosionTag = fireworkStar.getTagElement("Explosion");
        if (explosionTag != null) listTag.add(explosionTag);

        CompoundTag lifetimeTag = fireworkStar.getOrCreateTagElement("LifeTime");
        lifetimeTag.putInt("LifeTime", 0);
        fireworksTag.putInt("Flight", -128);

        if (!listTag.isEmpty()) {
            fireworksTag.put("Explosions", listTag);
            lifetimeTag.putInt("LifeTime", 0);
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

                 if (itemStack.is(HamstersTags.HAMSTER_BREEDING_FOOD) && this.getAge() == 0 && this.canFallInLove()) {

                     this.setInLove(player);
                     this.playSound(this.getEatingSound(itemStack));
                     return InteractionResult.SUCCESS;

                 } else if (this.isFood(itemStack)) {

                    // Feeding

                    if (player.getCooldowns().isOnCooldown(itemStack.getItem())) return InteractionResult.FAIL;
                    if (!player.getAbilities().instabuild) itemStack.shrink(1);
                    if (this.getHealth() < this.getMaxHealth()) this.heal(this.getMaxHealth() / 4);

                    else if (this.getAge() < 0) {
                        this.addAgeToHamster();
                    } else {

                        if (!hamstersBurst && this.getCheekLevel() == 3) return InteractionResult.FAIL;

                        if (this.getCheekLevel() < 3) {

                            this.setCheekLevel(this.getCheekLevel() + 1);
                            player.getCooldowns().addCooldown(itemStack.getItem(), 20);

                        } else if (hamstersBurst) {

                            this.setHealth(0);

                            if (hamsterBurstStyle == BurstStyleEnum.CONFETTI) {

                                this.playSound(HamstersSoundEvents.HAMSTER_EXPLODE);
                                ItemStack fireworkStack = getFirework(Util.getRandom(DyeColor.values(), this.getRandom()), this.getRandom().nextInt(3));
                                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(this.level(), this, this.getX(), this.getEyeY(), this.getZ(), fireworkStack);

                                fireworkRocketEntity.setSilent(true);
                                fireworkRocketEntity.setInvisible(true);

                                this.level().addFreshEntity(fireworkRocketEntity);
                                fireworkRocketEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);

                            } else if (hamsterBurstStyle == BurstStyleEnum.EXPLOSION) {
                                this.playSound(SoundEvents.GENERIC_EXPLODE);
                                if (this.level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 5, 0.0D, 0.0D, 0.0D, 0.0D);
                                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, false, Level.ExplosionInteraction.MOB);
                            }

                            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), 3));

                            for (int seedItems = 0; seedItems < 4; seedItems++) {
                                ItemEntity seedsItem = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WHEAT_SEEDS));
                                seedsItem.setDeltaMovement(this.getRandom().nextGaussian() * 0.1, this.getRandom().nextGaussian() * 0.2 + 0.2, this.getRandom().nextGaussian() * 0.1);
                                this.level().addFreshEntity(seedsItem);
                            }
                        }
                    }

                    this.playSound(this.getEatingSound(itemStack));
                    return InteractionResult.SUCCESS;
                }

                else if (this.isOwnedBy(player)) {

                     if (player.isShiftKeyDown()) this.catchHamster(player);

                    // Collar Dyeing

                    if (item instanceof DyeItem dyeItem) {

                        DyeColor dyeColor = dyeItem.getDyeColor();

                        if (dyeColor != this.getCollarColor()) {
                            this.setCollarColor(dyeColor);
                            this.playSound(SoundEvents.DYE_USE);
                            if (!player.getAbilities().instabuild) itemStack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }

                    // Ordering to Sit

                    else if (this.getSquishedTicks() <= 0) {

                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.getNavigation().stop();
                        return InteractionResult.SUCCESS;
                    }
                }

            }

            else if (this.isFood(itemStack)) {

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

    private void addAgeToHamster() {
        this.ageUp(1);
        if (this.level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 5, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor serverLevelAccessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {

        this.populateDefaultEquipmentSlots(this.getRandom(), difficultyInstance);

        if (mobSpawnType == MobSpawnType.SPAWN_EGG) {
            this.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length - 1)]);
            this.setMarking(Marking.BY_ID[this.getRandom().nextInt(Marking.BY_ID.length)]);
        } else if (mobSpawnType == MobSpawnType.CHUNK_GENERATION) {
            this.setVariant(Variant.BY_ID[Variant.WILD.getId()]);
            this.setMarking(Marking.BY_ID[Marking.BLANK.getId()]);
        }

        return spawnGroupData;
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {

        this.setBirthCountdown(60);

        HamsterNew hamster = HamstersEntityType.HAMSTER_NEW.create(serverLevel);
        assert hamster != null;

        if (this.isTame() && ageableMob instanceof HamsterNew hamsterParent && hamsterParent.isTame()) {
            hamster.setTame(true);
            hamster.setOwnerUUID(this.getOwnerUUID());
        }

        if (this.getVariant() == Variant.WILD.getId() && this.getMarking() == Marking.BLANK.getId() && ageableMob instanceof HamsterNew hamsterParent && hamsterParent.getVariant() == Variant.WILD.getId() && hamsterParent.getMarking() == Marking.BLANK.getId()) {
            hamster.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length) - 1]);
            hamster.setMarking(Marking.BY_ID[this.getRandom().nextInt(Marking.BY_ID.length)]);
        } else if (this.getVariant() != Variant.WILD.getId() && ageableMob instanceof HamsterNew hamsterParent && hamsterParent.getVariant() != Variant.WILD.getId()) {
            hamster.setVariant(this.getOffspringVariant(this, hamsterParent));
            hamster.setMarking(this.getOffspringPattern(this, hamsterParent));
        }

        return hamster;
    }

    private Marking getOffspringPattern(HamsterNew hamster, HamsterNew otherParent) {
        Marking marking = Marking.getTypeById(hamster.getMarking());
        Marking otherMarking = Marking.getTypeById(otherParent.getMarking());
        return this.getRandom().nextBoolean() ? marking : otherMarking;
    }

    private Variant getOffspringVariant(HamsterNew hamster, HamsterNew otherParent) {
        Variant variant = Variant.getTypeById(hamster.getVariant());
        Variant otherVariant = Variant.getTypeById(otherParent.getVariant());
        return this.getRandom().nextBoolean() ? variant : otherVariant;
    }

    // region Entity Data

    private final String
        variantTag = "Variant",
        markingTag = "Marking",
        sleepingTag = "Sleeping",
        sleepingCooldownTicksTag = "SleepingCooldownTicks",
        eatingCooldownTicksTag = "EatingCooldownTicks",
        cheekLevelTag = "CheekLevel",
        squishedTicksTag = "SquishedTicks",
        birthCountdownTag = "BirthCountdown",
        collarColorTag = "CollarColor",
        caughtTag = "Caught"
    ;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(VARIANT, 2);
        this.getEntityData().define(MARKING, 0);
        this.getEntityData().define(SLEEPING, false);
        this.getEntityData().define(SLEEPING_COOLDOWN_TICKS, 200);
        this.getEntityData().define(EATING_COOLDOWN_TICKS, 0);
        this.getEntityData().define(CHEEK_LEVEL, 0);
        this.getEntityData().define(SQUISHED_TICKS, 0);
        this.getEntityData().define(BIRTH_COUNTDOWN, 0);
        this.getEntityData().define(COLLAR_COLOR, 0);
        this.getEntityData().define(FROM_HAND, false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(Variant.BY_ID[compoundTag.getInt(this.variantTag)]);
        this.setMarking(Marking.BY_ID[compoundTag.getInt(this.markingTag)]);
        this.setSleeping(compoundTag.getBoolean(this.sleepingTag));
        this.setSleepingCooldownTicks(compoundTag.getInt(this.sleepingCooldownTicksTag));
        this.setEatingCooldownTicks(compoundTag.getInt(this.eatingCooldownTicksTag));
        this.setCheekLevel(compoundTag.getInt(this.cheekLevelTag));
        this.setSquishedTicks(compoundTag.getInt(this.squishedTicksTag));
        this.setBirthCountdown(compoundTag.getInt(this.birthCountdownTag));
        this.setCollarColor(DyeColor.byId(compoundTag.getInt(this.collarColorTag)));
        this.setFromHand(compoundTag.getBoolean(this.caughtTag));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(this.variantTag, this.getVariant());
        compoundTag.putInt(this.markingTag, this.getMarking());
        compoundTag.putBoolean(this.sleepingTag, this.isSleeping());
        compoundTag.putInt(this.sleepingCooldownTicksTag, this.getSleepingCooldownTicks());
        compoundTag.putInt(this.eatingCooldownTicksTag, this.getEatingCooldownTicks());
        compoundTag.putInt(this.cheekLevelTag, this.getCheekLevel());
        compoundTag.putInt(this.squishedTicksTag, this.getSquishedTicks());
        compoundTag.putInt(this.birthCountdownTag, this.getBirthCountdown());
        compoundTag.putInt(this.collarColorTag, this.getCollarColor().getId());
        compoundTag.putBoolean(this.caughtTag, this.getFromHand());
    }

    public int getVariant() {
        return this.getEntityData().get(VARIANT);
    }

    public void setVariant(Variant variant) {
        this.getEntityData().set(VARIANT, variant.getId());
    }

    public int getMarking() {
        return this.getEntityData().get(MARKING);
    }

    public void setMarking(Marking marking) {
        this.getEntityData().set(MARKING, marking.getId());
    }

    @Override
    public boolean isSleeping() {
        return this.getEntityData().get(SLEEPING);
    }

    @Override
    public void setSleeping(boolean isSleeping) {
        this.getEntityData().set(SLEEPING, isSleeping);
        if (!isSleeping) this.setDefaultSleepingCooldown();
    }

    public int getSleepingCooldownTicks() {
        return this.getEntityData().get(SLEEPING_COOLDOWN_TICKS);
    }

    public void setSleepingCooldownTicks(int sleepingCooldownTicks) {
        this.getEntityData().set(SLEEPING_COOLDOWN_TICKS, sleepingCooldownTicks);
    }

    private void setDefaultSleepingCooldown() {
        this.setSleepingCooldownTicks(200);
    }

    public int getEatingCooldownTicks() {
        return this.getEntityData().get(EATING_COOLDOWN_TICKS);
    }

    public void setEatingCooldownTicks(int eatingCooldownTicks) {
        this.getEntityData().set(EATING_COOLDOWN_TICKS, eatingCooldownTicks);
    }

    public int getCheekLevel() {
        return this.getEntityData().get(CHEEK_LEVEL);
    }

    public void setCheekLevel(int cheekLevel) {
        this.getEntityData().set(CHEEK_LEVEL, cheekLevel);
    }

    public int getSquishedTicks() {
        return this.getEntityData().get(SQUISHED_TICKS);
    }

    public void setSquishedTicks(int squishedTicks) {
        this.getEntityData().set(SQUISHED_TICKS, squishedTicks);
    }

    public int getBirthCountdown() {
        return this.getEntityData().get(BIRTH_COUNTDOWN);
    }

    public void setBirthCountdown(int birthCountdown) {
        this.getEntityData().set(BIRTH_COUNTDOWN, birthCountdown);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor dyeColor) {
        this.getEntityData().set(COLLAR_COLOR, dyeColor.getId());
    }

    public boolean getFromHand() {
        return this.getEntityData().get(FROM_HAND);
    }

    public void setFromHand(boolean fromHand) {
        this.getEntityData().set(FROM_HAND, fromHand);
    }

    @Override
    public void setOrderedToSit(boolean orderedToSit) {
        this.setSleeping(false);
        super.setOrderedToSit(orderedToSit);
    }

    @Override
    public boolean startRiding(@NotNull Entity entity) {
        boolean original = super.startRiding(entity);
        if (original) this.setSleeping(false);
        return original;
    }

    @Override
    public void stopRiding() {
        this.setSleeping(false);
        super.stopRiding();
    }

    @Override
    public boolean canSleep() {
        long dayTime = this.level().getDayTime();
        if (dayTime > 12000 && dayTime < 23000 || !getNearbyAvoidedEntities(this).isEmpty()) return false;
        return this.getSleepingCooldownTicks() <= 0 && !this.isInFluid() && !this.isPassenger() && this.getSquishedTicks() <= 0 && !level().isThundering();
    }

    private boolean isInFluid() {
        return this.isInWaterOrBubble() || this.isInLava() || this.isInPowderSnow;
    }

    public static List<LivingEntity> getNearbyAvoidedEntities(LivingEntity livingEntity) {

        List<LivingEntity> emptyList = List.of();
        if (!(livingEntity instanceof HamsterNew hamsterNew)) return emptyList;

        List<LivingEntity> nearbyEntities = hamsterNew.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat().range(10.0D), hamsterNew, hamsterNew.getBoundingBox().inflate(8.0D, 2.0D, 8.0D));
        if (!nearbyEntities.isEmpty() && nearbyEntities.get(0) instanceof Player player && (hamsterNew.isTame() || player.isCreative() || player.isCrouching())) return emptyList;

        return nearbyEntities;
    }

    public boolean isInWheel() {
        return this.isPassenger() && this.getVehicle() instanceof SeatEntity && this.level().getBlockState(this.blockPosition()).getBlock() instanceof HamsterWheelBlock;
    }

    // BLANK, BANDED, SPOTTED, ROAN, WHITEBELLY

    public enum Marking {
        BLANK (0, "blank"),
        BANDED (1, "banded"),
        SPOTS(2, "spotted"),
        ROAN (3, "roan"),
        BELLY (4, "whitebelly");

        public static final Marking[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Marking::getId)).toArray(Marking[]::new);
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

        public static Marking getTypeById(int id) {
            for (Marking type : values()) if (type.id == id) return type;
            return Marking.BLANK;
        }

        public String getName() {
            return this.name;
        }
    }

    // WHITE, CREAM, CHAMPAGNE, SILVER_DOVE, DOVE, CHOCOLATE, BLACK, WILD

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

    // region Catching

    private void catchHamster(Player player) {

        ItemStack output = new ItemStack(HamstersItems.HAMSTER);
        saveDefaultDataToItemTag(this, output);

        if (!player.getInventory().add(output)) {
            ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY() + 0.5, this.getZ(), output);
            itemEntity.setPickUpDelay(0);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            this.level().addFreshEntity(itemEntity);
        }

        this.discard();
        player.getInventory().add(output);
    }

    private static void saveDefaultDataToItemTag(HamsterNew mob, ItemStack itemStack) {

        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (mob.hasCustomName()) itemStack.setHoverName(mob.getCustomName());

        try {
            compoundTag.putShort("Air", (short) mob.getAirSupply());
            compoundTag.putBoolean("Invulnerable", mob.isInvulnerable());
            if (mob.isCustomNameVisible()) compoundTag.putBoolean("CustomNameVisible", mob.isCustomNameVisible());
            if (mob.isSilent()) compoundTag.putBoolean("Silent", mob.isSilent());
            if (mob.isNoGravity()) compoundTag.putBoolean("NoGravity", mob.isNoGravity());
            if (mob.hasGlowingTag()) compoundTag.putBoolean("Glowing", true);
            mob.addAdditionalSaveData(compoundTag);
        }

        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            mob.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getFromHand();
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
            event.setControllerSpeed(1.0F);
            event.setAnimation(SQUISH);
        } else if (this.isSleeping()) {
            event.setControllerSpeed(1.0F);
            event.setAnimation(SLEEP);
        } else if (this.isInSittingPose()) {
            event.setControllerSpeed(1.0F);
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
                    event.setControllerSpeed(1.1F * event.getLimbSwingAmount());
                    event.setAnimation(WALK);
                }
            }
        }  else if (this.isInWheel()) {
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

    static class HamsterTemptGoal extends TemptGoal {

        private final HamsterNew hamster;

        public HamsterTemptGoal(HamsterNew hamster, double speedModifier, Ingredient ingredient, boolean canScare) {
            super(hamster, speedModifier, ingredient, canScare);
            this.hamster = hamster;
        }

        @Override
        protected boolean canScare() {
            return super.canScare() && !this.hamster.isTame();
        }
    }

    public class HamsterGoToBlockGoal extends MoveToBlockGoal {

        public HamsterGoToBlockGoal(PathfinderMob pathfinderMob, double speedModifier, int searchRange) {
            super(pathfinderMob, speedModifier, searchRange);
        }

        @Override
        public boolean canUse() {

            if (this.mob instanceof HamsterNew hamsterNew && (!hamsterNew.isTame() || hamsterNew.level().isRainingAt(hamsterNew.blockPosition()) ||
            hamsterNew.isSleeping() || hamsterNew.isInSittingPose() || hamsterNew.getSquishedTicks() > 0)) return false;

            if (this.nextStartTick > 0) {
                --this.nextStartTick;
                return false;
            }

            if (this.findNearestBlock()) {
                this.nextStartTick = MoveToBlockGoal.reducedTickDelay(20);
                return true;
            }

            this.nextStartTick = this.nextStartTick(this.mob);
            return false;
        }

        public TagKey<Block> getBlockTag() {
            return null;
        }

        @Nullable
        public BlockPos getPosWithBlock(BlockPos blockPos, BlockGetter blockGetter) {

            if (this.getBlockTag() == null) return null;
            if (blockGetter.getBlockState(blockPos).is(this.getBlockTag())) return blockPos;

            for (BlockPos blocksToCheck : new BlockPos[]{blockPos.below(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.below().below()}) {
                if (!blockGetter.getBlockState(blocksToCheck).is(this.getBlockTag())) continue;
                return blocksToCheck;
            }

            return null;
        }

        @Override
        protected boolean isValidTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos) {
            if (this.getBlockTag() == null) return false;
            ChunkAccess chunkAccess = levelReader.getChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()), ChunkStatus.FULL, false);
            if (chunkAccess != null) return chunkAccess.getBlockState(blockPos).is(this.getBlockTag());
            return false;
        }

        @Override
        public void stop() {
            HamsterNew.this.getNavigation().stop();
            super.stop();
        }
    }

    public class HamsterGoToBottleGoal extends HamsterGoToBlockGoal {

        public HamsterGoToBottleGoal(PathfinderMob pathfinderMob, double speedModifier, int searchRange) {
            super(pathfinderMob, speedModifier, searchRange);
        }

        @Override
        public TagKey<Block> getBlockTag() {
            return HamstersTags.HAMSTER_BOTTLES;
        }

        @Override
        public void tick() {

            super.tick();

            BlockPos hamsterBottle = this.getPosWithBlock(this.mob.blockPosition(), this.mob.level());

            if (hamsterBottle != null && this.mob.position().distanceTo(Vec3.atBottomCenterOf(hamsterBottle)) <= 1.0D) {
                HamsterNew.this.setDefaultSleepingCooldown();
                this.stop();
            }
        }
    }

    public class HamsterGoToBowlGoal extends HamsterGoToBlockGoal {

        public HamsterGoToBowlGoal(PathfinderMob pathfinderMob, double speedModifier, int searchRange) {
            super(pathfinderMob, speedModifier, searchRange);
        }

        @Override
        public TagKey<Block> getBlockTag() {
            return HamstersTags.HAMSTER_BOWLS;
        }

        @Override
        public BlockPos getPosWithBlock(BlockPos blockPos, BlockGetter blockGetter) {
            BlockState blockState = blockGetter.getBlockState(blockPos);
            if (blockState.is(this.getBlockTag()) && !HamsterBowlBlock.hasSeeds(blockState)) return null;
            return super.getPosWithBlock(blockPos, blockGetter);
        }

        @Override
        protected boolean isValidTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos) {
            BlockState blockState = levelReader.getBlockState(blockPos);
            if (blockState.is(this.getBlockTag()) && !HamsterBowlBlock.hasSeeds(blockState) || this.isHamsterFull()) return false;
            return super.isValidTarget(levelReader, blockPos);
        }

        private boolean isHamsterFull() {
            return HamsterNew.this.getCheekLevel() >= 3 || HamsterNew.this.getEatingCooldownTicks() > 0;
        }

        @Override
        public void tick() {

            super.tick();

            BlockPos hamsterBowl = this.getPosWithBlock(this.mob.blockPosition(), this.mob.level());

            if (hamsterBowl != null && this.mob.position().distanceTo(Vec3.atBottomCenterOf(hamsterBowl)) <= 1.0D && !this.isHamsterFull()) {

                HamsterNew.this.setDefaultSleepingCooldown();

                BlockState blockState = this.mob.level().getBlockState(hamsterBowl);
                if (blockState.getBlock() instanceof HamsterBowlBlock) HamsterBowlBlock.removeSeeds(this.mob.level(), hamsterBowl, blockState);

                HamsterNew.this.playSound(HamsterNew.this.getEatingSound(Items.AIR.getDefaultInstance()));
                HamsterNew.this.setCheekLevel(HamsterNew.this.getCheekLevel() + 1);
                if (HamsterNew.this.getHealth() < HamsterNew.this.getMaxHealth()) HamsterNew.this.heal(HamsterNew.this.getMaxHealth() / 4);
                if (HamsterNew.this.getAge() < 0) HamsterNew.this.addAgeToHamster();

                HamsterNew.this.setEatingCooldownTicks(200);
                this.stop();
            }
        }
    }

    public class HamsterGoToWheelGoal extends HamsterGoToBlockGoal {

        public HamsterGoToWheelGoal(PathfinderMob pathfinderMob, double speedModifier, int searchRange) {
            super(pathfinderMob, speedModifier, searchRange);
        }

        @Override
        public TagKey<Block> getBlockTag() {
            return HamstersTags.HAMSTER_WHEELS;
        }

        @Override
        public BlockPos getPosWithBlock(BlockPos blockPos, BlockGetter blockGetter) {
            if (blockGetter.getBlockState(blockPos).is(this.getBlockTag()) && HamsterWheelBlock.isOccupied((Level) blockGetter, blockPos)) return null;
            return super.getPosWithBlock(blockPos, blockGetter);
        }

        @Override
        protected boolean isValidTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos) {
            if (HamsterWheelBlock.isOccupied((Level) levelReader, blockPos)) return false;
            return super.isValidTarget(levelReader, blockPos);
        }

        @Override
        public void tick() {

            super.tick();

            BlockPos hamsterWheel = this.getPosWithBlock(this.mob.blockPosition(), this.mob.level());

            if (hamsterWheel != null && this.mob.position().distanceTo(Vec3.atBottomCenterOf(hamsterWheel)) <= 1.0D) {
                HamsterNew.this.setDefaultSleepingCooldown();
                HamsterWheelBlock.sitDown(this.mob.level(), hamsterWheel, this.mob);
                this.stop();
            }
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

        @Override
        public boolean canUse() {
            ServerPlayer serverPlayer = (ServerPlayer) this.entity.getOwner();
            boolean canSitOnPlayer = serverPlayer != null && !serverPlayer.isSpectator() && !serverPlayer.isInWater() && !serverPlayer.isInPowderSnow;
            return !this.entity.isOrderedToSit() && canSitOnPlayer && this.entity.canSitOnShoulder();
        }

        @Override
        public boolean isInterruptable() {
            return !this.isSittingOnShoulder;
        }

        @Override
        public void start() {
            this.owner = (ServerPlayer) this.entity.getOwner();
            this.isSittingOnShoulder = false;
        }

        @Override
        public void tick() {
            if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed() && this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
        }
    }

    public class HamsterSleepGoal extends SleepGoal<HamsterNew> {

        public HamsterSleepGoal(HamsterNew mob) {
            super(mob);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && HamsterNew.this.getCheekLevel() <= 0 && HamsterNew.getNearbyAvoidedEntities(this.mob).isEmpty();
        }
    }

    // endregion
}