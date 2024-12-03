package com.starfish_studios.hamsters.entities;

import com.google.common.collect.Lists;
import com.starfish_studios.hamsters.blocks.HamsterBowlBlock;
import com.starfish_studios.hamsters.blocks.HamsterWheelBlock;
import com.starfish_studios.hamsters.entities.util.MMPathNavigatorGround;
import com.starfish_studios.hamsters.entities.util.SleepGoal;
import com.starfish_studios.hamsters.entities.util.SleepingAnimal;
import com.starfish_studios.hamsters.entities.util.SmartBodyHelper;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
import com.starfish_studios.hamsters.registry.HamstersItems;
import com.starfish_studios.hamsters.registry.HamstersSoundEvents;
import com.starfish_studios.hamsters.registry.HamstersTags;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.gameevent.GameEvent;
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

public class Hamster extends TamableAnimal implements GeoEntity, SleepingAnimal {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.run");
    protected static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.sleep");
    protected static final RawAnimation STANDING = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.standing");
    protected static final RawAnimation SQUISH = RawAnimation.begin().thenPlay("animation.sf_hba.hamster.squish").thenPlayXTimes("animation.sf_hba.hamster.squished", 4).thenPlay("animation.sf_hba.hamster.unsquish");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);


    // region Initialization

    public Hamster(EntityType<? extends TamableAnimal> entityType, Level level) {
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

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0F).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.ATTACK_DAMAGE, 1.0F);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getFromHand();
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D) {
            @Override
            public boolean canUse() {
                if (this.mob instanceof Hamster hamster && (hamster.getSquishedTicks() > 0 || hamster.isInSittingPose())) return false;
                return super.canUse();
            }
        });

        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.3D, 1.5D, livingEntity -> livingEntity instanceof Player ? !this.isTame() : livingEntity.getType().is(HamstersTags.HAMSTER_AVOIDED)));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new HamsterTemptGoal(this, 1.0D, Ingredient.of(HamstersTags.HAMSTER_FOOD), true));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new HamsterGoToWheelGoal(this, 1.2D, 8));
        this.goalSelector.addGoal(7, new HamsterGoToBottleGoal(this, 1.2D, 8));
        this.goalSelector.addGoal(7, new HamsterGoToBowlGoal(this, 1.2D, 8));
        this.goalSelector.addGoal(8, new HamsterDismountGoal(this));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new HamsterSleepGoal(this));

        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public void tick() {
                if (this.mob instanceof Hamster hamster && hamster.canUseMovementGoals()) super.tick();
            }
        });

        this.goalSelector.addGoal(12, new HamsterLookAroundGoal(this));
    }

    // endregion

    // region Spawning

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor serverLevelAccessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {

        this.populateDefaultEquipmentSlots(this.getRandom(), difficultyInstance);

        if (mobSpawnType == MobSpawnType.SPAWN_EGG) {
            this.setVariant(Variant.BY_ID[this.getRandom().nextInt(Variant.BY_ID.length - 1)]);
            this.setMarking(Marking.BY_ID[this.getRandom().nextInt(Marking.BY_ID.length)]);
        } else if (mobSpawnType == MobSpawnType.CHUNK_GENERATION) {
            this.setMarking(Marking.BY_ID[Marking.BLANK.getId()]);
        }

        return spawnGroupData;
    }

    @SuppressWarnings("unused")
    public static boolean checkHamsterSpawnRules(EntityType<? extends TamableAnimal> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return levelAccessor.getBlockState(blockPos.below()).is(HamstersTags.HAMSTERS_SPAWNABLE_ON) && isBrightEnoughToSpawn(levelAccessor, blockPos);
    }

    // endregion

    // region Navigation

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new MMPathNavigatorGround(this, level);
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new SmartBodyHelper(this);
    }

    // endregion

    // region Entity Data

    private static final EntityDataAccessor<Integer>
        VARIANT = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        MARKING = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        COLLAR_COLOR = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        CHEEK_LEVEL = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        EATING_COOLDOWN_TICKS = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        SLEEPING_COOLDOWN_TICKS = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        MOUNT_COOLDOWN_TICKS = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        DISMOUNT_COOLDOWN_TICKS = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        BIRTH_COUNTDOWN = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT),
        SQUISHED_TICKS = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.INT)
    ;

    private static final EntityDataAccessor<Boolean>
        SLEEPING = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.BOOLEAN),
        FROM_HAND = SynchedEntityData.defineId(Hamster.class, EntityDataSerializers.BOOLEAN)
    ;

    private final String
        variantTag = "variant",
        markingTag = "marking",
        collarColorTag = "collar_color",
        cheekLevelTag = "cheek_level",
        sleepingCooldownTicksTag = "sleeping_cooldown_ticks",
        mountCooldownTicksTag = "mount_cooldown_ticks",
        dismountCooldownTicksTag = "dismount_cooldown_ticks",
        birthCountdownTag = "birth_countdown",
        squishedTicksTag = "squished_ticks",
        sleepingTag = "sleeping",
        fromHandTag = "from_hand"
    ;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(VARIANT, 2);
        this.getEntityData().define(MARKING, 0);
        this.getEntityData().define(COLLAR_COLOR, 0);
        this.getEntityData().define(CHEEK_LEVEL, 0);
        this.getEntityData().define(EATING_COOLDOWN_TICKS, 0);
        this.getEntityData().define(SLEEPING_COOLDOWN_TICKS, 200);
        this.getEntityData().define(MOUNT_COOLDOWN_TICKS, 0);
        this.getEntityData().define(DISMOUNT_COOLDOWN_TICKS, 0);
        this.getEntityData().define(BIRTH_COUNTDOWN, 0);
        this.getEntityData().define(SQUISHED_TICKS, 0);
        this.getEntityData().define(SLEEPING, false);
        this.getEntityData().define(FROM_HAND, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt(this.variantTag, this.getVariant());
        compoundTag.putInt(this.markingTag, this.getMarking());
        compoundTag.putInt(this.collarColorTag, this.getCollarColor().getId());
        compoundTag.putInt(this.cheekLevelTag, this.getCheekLevel());
        compoundTag.putInt(this.sleepingCooldownTicksTag, this.getSleepingCooldownTicks());
        compoundTag.putInt(this.mountCooldownTicksTag, this.getMountCooldownTicks());
        compoundTag.putInt(this.dismountCooldownTicksTag, this.getDismountCooldownTicks());
        compoundTag.putInt(this.birthCountdownTag, this.getBirthCountdown());
        compoundTag.putInt(this.squishedTicksTag, this.getSquishedTicks());
        compoundTag.putBoolean(this.sleepingTag, this.isSleeping());
        compoundTag.putBoolean(this.fromHandTag, this.getFromHand());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(Variant.BY_ID[compoundTag.getInt(this.variantTag)]);
        this.setMarking(Marking.BY_ID[compoundTag.getInt(this.markingTag)]);
        this.setCollarColor(DyeColor.byId(compoundTag.getInt(this.collarColorTag)));
        this.setCheekLevel(compoundTag.getInt(this.cheekLevelTag));
        this.setSleepingCooldownTicks(compoundTag.getInt(this.sleepingCooldownTicksTag));
        this.setMountCooldownTicks(compoundTag.getInt(this.mountCooldownTicksTag));
        this.setDismountCooldownTicks(compoundTag.getInt(this.dismountCooldownTicksTag));
        this.setBirthCountdown(compoundTag.getInt(this.birthCountdownTag));
        this.setSquishedTicks(compoundTag.getInt(this.squishedTicksTag));
        this.setSleeping(compoundTag.getBoolean(this.sleepingTag));
        this.setFromHand(compoundTag.getBoolean(this.fromHandTag));
    }

    public int getVariant() {
        return this.getEntityData().get(VARIANT);
    }

    private void setVariant(Variant variant) {
        this.getEntityData().set(VARIANT, variant.getId());
    }

    public int getMarking() {
        return this.getEntityData().get(MARKING);
    }

    private void setMarking(Marking marking) {
        this.getEntityData().set(MARKING, marking.getId());
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
    }

    private void setCollarColor(DyeColor dyeColor) {
        this.getEntityData().set(COLLAR_COLOR, dyeColor.getId());
    }

    public int getCheekLevel() {
        return this.getEntityData().get(CHEEK_LEVEL);
    }

    private void setCheekLevel(int cheekLevel) {
        this.getEntityData().set(CHEEK_LEVEL, cheekLevel);
    }

    private int getSleepingCooldownTicks() {
        return this.getEntityData().get(SLEEPING_COOLDOWN_TICKS);
    }

    private void setSleepingCooldownTicks(int sleepingCooldownTicks) {
        this.getEntityData().set(SLEEPING_COOLDOWN_TICKS, sleepingCooldownTicks);
    }

    private void setDefaultSleepingCooldown() {
        this.setSleepingCooldownTicks(200);
    }

    private int getMountCooldownTicks() {
        return this.getEntityData().get(MOUNT_COOLDOWN_TICKS);
    }

    private void setMountCooldownTicks(int mountCooldownTicks) {
        this.getEntityData().set(MOUNT_COOLDOWN_TICKS, mountCooldownTicks);
    }

    private int getDismountCooldownTicks() {
        return this.getEntityData().get(DISMOUNT_COOLDOWN_TICKS);
    }

    private void setDismountCooldownTicks(int dismountCooldownTicks) {
        this.getEntityData().set(DISMOUNT_COOLDOWN_TICKS, dismountCooldownTicks);
    }

    private int getBirthCountdown() {
        return this.getEntityData().get(BIRTH_COUNTDOWN);
    }

    private void setBirthCountdown(int birthCountdown) {
        this.getEntityData().set(BIRTH_COUNTDOWN, birthCountdown);
    }

    private int getSquishedTicks() {
        return this.getEntityData().get(SQUISHED_TICKS);
    }

    private void setSquishedTicks(int squishedTicks) {
        this.getEntityData().set(SQUISHED_TICKS, squishedTicks);
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

    private boolean getFromHand() {
        return this.getEntityData().get(FROM_HAND);
    }

    private void setFromHand(boolean fromHand) {
        this.getEntityData().set(FROM_HAND, fromHand);
    }

    @Override
    public void setOrderedToSit(boolean orderedToSit) {
        this.setSleeping(false);
        super.setOrderedToSit(orderedToSit);
    }

    public enum Variant {
        WHITE (0, "white"),
        CREAM (1, "cream"),
        CHAMPAGNE (2, "champagne"),
        SILVER_DOVE (3, "silver_dove"),
        DOVE (4, "dove"),
        CHOCOLATE (5, "chocolate"),
        BLACK (6, "black");

        public static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public static Variant getTypeById(int id) {
            for (Variant type : values()) if (type.id == id) return type;
            return Variant.CHAMPAGNE;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum Marking {
        BLANK (0, "blank"),
        BANDED (1, "banded"),
        SPOTTED(2, "spotted"),
        ROAN (3, "roan"),
        WHITEBELLY (4, "whitebelly");

        public static final Marking[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Marking::getId)).toArray(Marking[]::new);
        private final int id;
        private final String name;

        Marking(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private int getId() {
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

    // endregion

    // region GeckoLib

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    private <E extends Hamster> PlayState animController(final AnimationState<E> event) {

        if (this.getSquishedTicks() > 0) { // Squished Animation
            event.setAnimation(SQUISH);
        } else if (this.isSleeping()) { // Sleeping Animation
            event.setAnimation(SLEEP);
        } else if (this.isInSittingPose()) { // Sitting Animation
            event.setAnimation(STANDING);
        } else if (event.isMoving()) { // Moving Animations
            if (this.isSprinting()) {
                event.setAnimation(RUN);
            } else {
                event.setAnimation(WALK);
            }
        } else if (this.isInWheel()) { // Wheel Animation
            event.setAnimation(RUN);
        } else { // Idle Animation
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    // endregion

    // region Ticking, AI, and Entity Events

    @Override
    public void aiStep() {

        super.aiStep();

        if (hamstersSquish) {
            for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox())) {
                if (!player.onGround() && player.getDeltaMovement().y() < 0.0D) {
                    if (jumpHurtsHamsters && EnchantmentHelper.getEnchantmentLevel(Enchantments.FALL_PROTECTION, player) == 0 && this.getSquishedTicks() <= 0 && !this.hasCustomName()) this.hurt(damageSources().generic(), 1.0F);
                    this.squishHamster();
                }
            }
        }

        if (this.getSquishedTicks() > 0) {
            this.setSquishedTicks(this.getSquishedTicks() - 1);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.getSquishedTicks() == 10) this.playSound(HamstersSoundEvents.HAMSTER_UNSQUISH);
        }

        if (this.isInWheel() && this.getCheekLevel() > 0 && this.tickCount % 100 == 0) this.setCheekLevel(this.getCheekLevel() - 1);
        if (getNearbyAvoidedEntities(this).isEmpty() && this.getSleepingCooldownTicks() > 0) this.setSleepingCooldownTicks(this.getSleepingCooldownTicks() - 1);
        if (this.getMountCooldownTicks() > 0) this.setMountCooldownTicks(this.getMountCooldownTicks() - 1);
        if (this.getDismountCooldownTicks() > 0) this.setDismountCooldownTicks(this.getDismountCooldownTicks() - 1);
        if (this.getBirthCountdown() > 0) this.setBirthCountdown(this.getBirthCountdown() - 1);

        if (hamstersBurst) {
            if (this.level().isClientSide()) {
                if (this.getCheekLevel() == 2 && this.tickCount % 10 == 0) {
                    this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(1.2), this.getZ(), 0.0D, 0.2D, 0.0D);
                    this.playSound(HamstersSoundEvents.HAMSTER_BEG);
                } else if (this.getCheekLevel() == 3 && this.tickCount % 5 == 0) {
                    this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY(1.2), this.getZ(), 0.0D, 0.2D, 0.0D);
                }
            } else {
                if (this.isAlive() && this.getCheekLevel() >= 2 && this.tickCount % (80 - (this.getCheekLevel() * 10)) == 0) this.playSound(HamstersSoundEvents.HAMSTER_BEG);
            }
        }
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
    public boolean hurt(@NotNull DamageSource damageSource, float damageAmount) {

        boolean original = super.hurt(damageSource, damageAmount);

        if (original) {
            if (damageSource.is(HamstersTags.SQUISHES_HAMSTERS)) this.squishHamster();
            else this.setSleeping(false);
        }

        return original;
    }

    public void squishHamster() {

        this.setSleeping(false);

        if (this.getSquishedTicks() <= 0) {
            this.setSquishedTicks(120);
            this.playSound(HamstersSoundEvents.HAMSTER_SQUISH);
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

    private void addAgeToHamster() {
        this.ageUp(1);
        if (this.level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 5, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean startRiding(@NotNull Entity entity) {

        boolean original = super.startRiding(entity);

        if (original) {
            this.setSleeping(false);
            this.setOrderedToSit(false);
            this.setDismountCooldownTicks(200);
        }

        return original;
    }

    @Override
    public void stopRiding() {
        this.setSleeping(false);
        this.setMountCooldownTicks(200);
        super.stopRiding();
    }

    // endregion

    // region Mob Interactions

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(HamstersTags.HAMSTER_FOOD) && this.canUseMovementGoals();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand interactionHand) {

        ItemStack itemStack = player.getItemInHand(interactionHand);
        Item item = itemStack.getItem();

        if (this.level().isClientSide()) {

            boolean canInteract = this.isOwnedBy(player) || this.isTame() || this.isFood(itemStack) && !this.isTame();
            return canInteract ? InteractionResult.CONSUME : InteractionResult.PASS;

        } else {

            if (player.getCooldowns().isOnCooldown(itemStack.getItem())) return InteractionResult.FAIL;

            if (this.isTame()) {

                if (itemStack.is(HamstersTags.HAMSTER_BREEDING_FOOD) && this.getAge() == 0 && this.canFallInLove()) {

                    this.feedHamster(itemStack, player, true);
                    this.setInLove(player);
                    return InteractionResult.SUCCESS;

                } else if (this.isFood(itemStack)) {

                    // Feeding

                    this.feedHamster(itemStack, player, !hamstersBurst || this.getCheekLevel() < 3);

                    if (this.getAge() < 0) {
                        this.addAgeToHamster();
                    } else {

                        if (this.getCheekLevel() >= 3 && hamstersBurst) {

                            this.setHealth(0);
                            this.playSound(HamstersSoundEvents.HAMSTER_EXPLODE);

                            if (hamsterBurstStyle == BurstStyleEnum.CONFETTI) {
                                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(this.level(), this, this.getX(), this.getEyeY(), this.getZ(), getFirework());
                                fireworkRocketEntity.setSilent(true);
                                fireworkRocketEntity.setInvisible(true);
                                this.level().addFreshEntity(fireworkRocketEntity);
                                fireworkRocketEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
                            } else if (hamsterBurstStyle == BurstStyleEnum.EXPLOSION) {
                                if (this.level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 5, 0.0D, 0.0D, 0.0D, 0.0D);
                                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, false, Level.ExplosionInteraction.MOB);
                            }

                            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), 3));

                            for (int seedItems = 0; seedItems < 4; seedItems++) {
                                ItemEntity seedsItem = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WHEAT_SEEDS));
                                seedsItem.setDeltaMovement(this.getRandom().nextGaussian() * 0.1D, this.getRandom().nextGaussian() * 0.2D + 0.2D, this.getRandom().nextGaussian() * 0.1D);
                                this.level().addFreshEntity(seedsItem);
                            }
                        } else {
                            if (this.getCheekLevel() < 3) {
                                this.setCheekLevel(this.getCheekLevel() + 1);
                                player.getCooldowns().addCooldown(itemStack.getItem(), 20);
                            } else {
                                return InteractionResult.FAIL;
                            }
                        }
                    }

                    return InteractionResult.SUCCESS;

                } else if (this.isOwnedBy(player)) {

                    if (player.isShiftKeyDown() && itemStack.isEmpty()) this.catchHamster(player);

                    // Collar Dyeing

                    if (item instanceof DyeItem dyeItem) {

                        DyeColor dyeColor = dyeItem.getDyeColor();

                        if (dyeColor != this.getCollarColor()) {
                            this.setCollarColor(dyeColor);
                            this.playSound(SoundEvents.DYE_USE);
                            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
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

                this.feedHamster(itemStack, player, false);

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

    private void feedHamster(ItemStack itemStack, Player player, boolean shouldHeal) {

        if (player.getCooldowns().isOnCooldown(itemStack.getItem())) return;

        this.playSound(this.getEatingSound(itemStack));
        this.gameEvent(GameEvent.EAT);
        if (this.getHealth() < this.getMaxHealth() && shouldHeal) this.heal(this.getMaxHealth() / 4);

        if (!player.getAbilities().instabuild) itemStack.shrink(1);
    }

    private void catchHamster(Player player) {

        ItemStack itemStack = new ItemStack(HamstersItems.HAMSTER);
        saveDefaultDataToItemTag(this, itemStack);

        if (!player.getInventory().add(itemStack)) {
            ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY() + 0.5, this.getZ(), itemStack);
            itemEntity.setPickUpDelay(0);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            this.level().addFreshEntity(itemEntity);
        }

        this.discard();
        player.getInventory().add(itemStack);
    }

    private static void saveDefaultDataToItemTag(Hamster mob, ItemStack itemStack) {

        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (mob.hasCustomName()) itemStack.setHoverName(mob.getCustomName());

        try {
            compoundTag.putBoolean("PersistenceRequired", true);
            if (mob.isNoAi()) compoundTag.putBoolean("NoAI", true);
            if (mob.isSilent()) compoundTag.putBoolean("Silent", true);
            if (mob.isNoGravity()) compoundTag.putBoolean("NoGravity", true);
            if (mob.hasGlowingTag()) compoundTag.putBoolean("Glowing", true);
            if (mob.isInvulnerable()) compoundTag.putBoolean("Invulnerable", true);
            compoundTag.putFloat("Health", mob.getHealth());
            compoundTag.putInt("Age", mob.getAge());
            compoundTag.putInt("InLove", mob.getInLoveTime());
            mob.addAdditionalSaveData(compoundTag);
        }

        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Saving entity data");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            mob.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    public static ItemStack getFirework() {

        String explosionTag = "Explosion";
        String lifeTimeTag = "LifeTime";

        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        ItemStack fireworkStar = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundTag = fireworkStar.getOrCreateTagElement(explosionTag);

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

        CompoundTag explosionTagElement = fireworkStar.getTagElement(explosionTag);
        if (explosionTagElement != null) listTag.add(explosionTagElement);

        CompoundTag lifeTimeTagElement = fireworkStar.getOrCreateTagElement(lifeTimeTag);
        lifeTimeTagElement.putInt(lifeTimeTag, 0);
        fireworksTag.putInt("Flight", -128);

        if (!listTag.isEmpty()) {
            fireworksTag.put("Explosions", listTag);
            lifeTimeTagElement.putInt(lifeTimeTag, 0);
        }

        return itemStack;
    }

    // endregion

    // region Breeding

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {

        this.setBirthCountdown(60);

        Hamster hamster = HamstersEntityTypes.HAMSTER.create(serverLevel);
        assert hamster != null;

        if (ageableMob instanceof Hamster hamsterParent) {

            if (this.isTame() && hamsterParent.isTame()) {
                hamster.setTame(true);
                hamster.setOwnerUUID(this.getOwnerUUID());
            }

            hamster.setVariant(this.getOffspringVariant(this, hamsterParent));
            hamster.setMarking(this.getOffspringPattern(this, hamsterParent));
        }

        return hamster;
    }

    private Variant getOffspringVariant(Hamster parent, Hamster otherParent) {
        Variant variant = Variant.getTypeById(parent.getVariant());
        Variant otherVariant = Variant.getTypeById(otherParent.getVariant());
        return this.getRandom().nextBoolean() ? variant : otherVariant;
    }

    private Marking getOffspringPattern(Hamster parent, Hamster otherParent) {
        Marking marking = Marking.getTypeById(parent.getMarking());
        Marking otherMarking = Marking.getTypeById(otherParent.getMarking());
        return this.getRandom().nextBoolean() ? marking : otherMarking;
    }

    // endregion

    // region Sounds

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
    public @NotNull SoundEvent getEatingSound(@NotNull ItemStack itemStack) {
        return HamstersSoundEvents.HAMSTER_EAT;
    }

    // endregion

    // region Miscellaneous

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, @NotNull EntityDimensions dimensions) {
        return 0.3F;
    }

    private boolean isInFluid() {
        return this.isInWaterOrBubble() || this.isInLava() || this.isInPowderSnow;
    }

    public boolean isInWheel() {
        return this.isPassenger() && this.getVehicle() instanceof SeatEntity && this.level().getBlockState(this.blockPosition()).getBlock() instanceof HamsterWheelBlock;
    }

    @Override
    public boolean canSleep() {
        long dayTime = this.level().getDayTime();
        if (dayTime > 12000 && dayTime < 23000 || !getNearbyAvoidedEntities(this).isEmpty()) return false;
        return this.getSleepingCooldownTicks() <= 0 && this.canUseMovementGoals() && !this.isInFluid() && !this.isPassenger() && !level().isThundering();
    }

    public static List<LivingEntity> getNearbyAvoidedEntities(LivingEntity livingEntity) {

        List<LivingEntity> emptyList = List.of();
        if (!(livingEntity instanceof Hamster hamster)) return emptyList;

        List<LivingEntity> nearbyEntities = hamster.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat().range(10.0D), hamster, hamster.getBoundingBox().inflate(8.0D, 2.0D, 8.0D));
        if (!nearbyEntities.isEmpty() && nearbyEntities.get(0) instanceof Player player && (hamster.isTame() || player.isCreative() || player.isCrouching())) return emptyList;

        return nearbyEntities;
    }

    private boolean canUseMovementGoals() {
        return this.getSquishedTicks() <= 0 && !this.isSleeping();
    }

    // endregion

    // region Goals

    public static class HamsterLookAroundGoal extends RandomLookAroundGoal {

        private final Hamster hamster;

        public HamsterLookAroundGoal(Hamster hamster) {
            super(hamster);
            this.hamster = hamster;
        }

        @Override
        public void tick() {
            if (this.hamster.canUseMovementGoals()) super.tick();
        }
    }

    public static class HamsterTemptGoal extends TemptGoal {

        private final Hamster hamster;

        public HamsterTemptGoal(Hamster hamster, double speedModifier, Ingredient ingredient, boolean canScare) {
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

            if (this.mob instanceof Hamster hamster && (!hamster.canUseMovementGoals() || !hamster.isTame() ||
            hamster.level().isRainingAt(hamster.blockPosition()) || hamster.isInSittingPose() || hamster.getVehicle() != null)) return false;

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
            Hamster.this.getNavigation().stop();
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
                Hamster.this.setDefaultSleepingCooldown();
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

        private boolean canEatFromBowl() {
            return Hamster.this.getCheekLevel() <= 0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.canEatFromBowl();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.canEatFromBowl();
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
            if (blockState.is(this.getBlockTag()) && !HamsterBowlBlock.hasSeeds(blockState)) return false;
            return super.isValidTarget(levelReader, blockPos);
        }

        @Override
        public void tick() {

            super.tick();
            if (!this.canEatFromBowl()) return;

            BlockPos hamsterBowl = this.getPosWithBlock(this.mob.blockPosition(), this.mob.level());

            if (hamsterBowl != null && this.mob.position().distanceTo(Vec3.atBottomCenterOf(hamsterBowl)) <= 1.0D) {

                Hamster.this.setDefaultSleepingCooldown();

                BlockState blockState = this.mob.level().getBlockState(hamsterBowl);
                if (blockState.getBlock() instanceof HamsterBowlBlock) HamsterBowlBlock.removeSeeds(this.mob.level(), hamsterBowl, blockState);

                Hamster.this.playSound(Hamster.this.getEatingSound(Items.AIR.getDefaultInstance()));
                Hamster.this.setCheekLevel(Hamster.this.getCheekLevel() + 1);
                if (Hamster.this.getHealth() < Hamster.this.getMaxHealth()) Hamster.this.heal(Hamster.this.getMaxHealth() / 4);
                if (Hamster.this.getAge() < 0) Hamster.this.addAgeToHamster();

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
        public boolean canUse() {
            return super.canUse() && Hamster.this.getMountCooldownTicks() <= 0;
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
                Hamster.this.setDefaultSleepingCooldown();
                HamsterWheelBlock.sitDown(this.mob.level(), hamsterWheel, this.mob);
                this.stop();
            }
        }
    }

    public static class HamsterDismountGoal extends Goal {

        private final Hamster hamster;

        public HamsterDismountGoal(Hamster hamster) {
            this.hamster = hamster;
        }

        @Override
        public boolean canUse() {
            return this.hamster.getVehicle() != null && this.hamster.getDismountCooldownTicks() <= 0 && this.hamster.getCheekLevel() <= 0;
        }

        @Override
        public void tick() {
            this.hamster.stopRiding();
            this.stop();
        }

        @Override
        public void stop() {
            this.hamster.getNavigation().stop();
            super.stop();
        }
    }

    public class HamsterSleepGoal extends SleepGoal<Hamster> {

        public HamsterSleepGoal(Hamster mob) {
            super(mob);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Hamster.this.getCheekLevel() <= 0 && Hamster.getNearbyAvoidedEntities(this.mob).isEmpty();
        }
    }

    // endregion
}