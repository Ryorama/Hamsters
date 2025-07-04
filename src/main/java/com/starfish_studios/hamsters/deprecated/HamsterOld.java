//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.starfish_studios.hamsters.deprecated;

import com.starfish_studios.hamsters.blocks.HamsterWheelBlock;
import com.starfish_studios.hamsters.entities.SeatEntity;
import com.starfish_studios.hamsters.registry.*;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
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
import java.util.function.Predicate;

import static com.starfish_studios.hamsters.blocks.HamsterWheelBlock.FACING;

public class HamsterOld extends TamableAnimal implements GeoEntity {
    // region
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.run");
    protected static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.sleep");
    protected static final RawAnimation STANDING = RawAnimation.begin().thenLoop("animation.sf_hba.hamster.standing");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> EAT_COUNTER = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(HamstersTags.HAMSTER_FOOD);
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_HAND = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WAIT_TIME_BEFORE_RUN = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAIT_TIME_WHEN_RUNNING = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);

    HamsterOld.HamsterGoToWheelGoal hamsterGoToWheelGoal;
    // endregion

    public HamsterOld(EntityType<? extends HamsterOld> entityType, Level level) {
        super(entityType, level);
        if (!this.isSleeping()) {
            this.setCanPickUpLoot(true);
        }
        this.lookControl = new HamsterOld.HamsterLookControl();
        // region PATHFINDING
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CAUTIOUS, 1.0F);
        this.setPathfindingMalus((BlockPathTypes.DANGER_POWDER_SNOW), 1.0F);
        this.setPathfindingMalus((BlockPathTypes.DANGER_OTHER), 1.0F);
        this.setPathfindingMalus((BlockPathTypes.DAMAGE_OTHER), 1.0F);
        this.setPathfindingMalus((BlockPathTypes.WATER_BORDER), 1.0F);
        // endregion
    }

    // region BASIC ENTITY

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.3));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new SleepGoal());
        this.hamsterGoToWheelGoal = new HamsterGoToWheelGoal();
        this.goalSelector.addGoal(4, this.hamsterGoToWheelGoal);
        this.goalSelector.addGoal(4, new SearchForItemsGoal(this, 1.25F, FOOD_ITEMS, 8.0D, 8.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25, FOOD_ITEMS, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new RunInWheelGoal());
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

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 1.5);
    }

    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {

        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.level().isClientSide) {
            if (this.isTame() && this.isOwnedBy(player)) {
                return InteractionResult.SUCCESS;
            } else {
                return !this.isFood(itemStack) || !(this.getHealth() < this.getMaxHealth()) && this.isTame() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
        } else {
            InteractionResult interactionResult;
            if (this.isTame()) {
                if (this.isOwnedBy(player) && this.isFood(itemStack) && (this.getHealth() < this.getMaxHealth())) {
                    this.usePlayerItem(player, interactionHand, itemStack);
                    this.heal(2.0F);
                    return InteractionResult.CONSUME;
                } else if (this.isOwnedBy(player)) {
                    interactionResult = super.mobInteract(player, interactionHand);
                    if (!interactionResult.consumesAction() || this.isBaby()) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                    if (this.isOwnedBy(player) && player.isShiftKeyDown()) {
                        this.catchHamster(player);
                    }
                    return interactionResult;
                }
            } else if (this.isFood(itemStack)) {
                this.usePlayerItem(player, interactionHand, itemStack);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }

                this.setPersistenceRequired();
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, interactionHand);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return this.isBaby() ? 0.2F : 0.3F;
    }

    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    // endregion

    // region CATCHING

    public InteractionResult catchHamster(Player player) {
        ItemStack output = this.getCaughtItemStack();
        saveDefaultDataToItemTag(this, output);
        if (!player.getInventory().add(output)) {
            ItemEntity itemEntity = new ItemEntity(level(), this.getX(), this.getY() + 0.5, this.getZ(), output);
            itemEntity.setPickUpDelay(0);
            itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));
            level().addFreshEntity(itemEntity);
        }
        this.discard();
        player.getInventory().add(output);
        return InteractionResult.sidedSuccess(true);
    }

    private static void saveDefaultDataToItemTag(HamsterOld mob, ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (mob.hasCustomName()) {
            itemStack.setHoverName(mob.getCustomName());
        }
        try {
            compoundTag.putShort("Air", (short)mob.getAirSupply());
            compoundTag.putBoolean("Invulnerable", mob.isInvulnerable());
            if (mob.isCustomNameVisible()) compoundTag.putBoolean("CustomNameVisible", mob.isCustomNameVisible());
            if (mob.isSilent()) compoundTag.putBoolean("Silent", mob.isSilent());
            if (mob.isNoGravity()) compoundTag.putBoolean("NoGravity", mob.isNoGravity());
            if (mob.hasGlowingTag()) compoundTag.putBoolean("Glowing", true);
            mob.addAdditionalSaveData(compoundTag);
        }
        catch (Throwable var9) {
            CrashReport crashReport = CrashReport.forThrowable(var9, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being saved");
            mob.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromHand();
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public ItemStack getCaughtItemStack() {
        return new ItemStack(HamstersItems.HAMSTER);
    }
    // endregion

    // region SOUNDS



    protected SoundEvent getAmbientSound() {
        return HamstersSoundEvents.HAMSTER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return HamstersSoundEvents.HAMSTER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return HamstersSoundEvents.HAMSTER_DEATH;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 3.0F);
    }

    protected void playBegSound() {
        this.playSound(HamstersSoundEvents.HAMSTER_BEG, this.getSoundVolume(), this.getVoicePitch());
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    // endregion

    // region DATA

    void clearStates() {
        this.setIsInterested(false);
        this.setInSittingPose(false);
        this.setSleeping(false);
    }

    public void setIsInterested(boolean bl) {
        this.entityData.set(DATA_INTERESTED, bl);
    }
    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EAT_COUNTER, 0);
        this.entityData.define(DATA_INTERESTED, false);
        this.entityData.define(DATA_VARIANT, 2);
        this.entityData.define(WAIT_TIME_BEFORE_RUN, 0);
        this.entityData.define(WAIT_TIME_WHEN_RUNNING, 0);
        this.entityData.define(FROM_HAND, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(HamsterOld.Variant.BY_ID[compoundTag.getInt("Variant")]);
        this.setWaitTimeBeforeRunTicks(compoundTag.getInt("RunTicks"));
        this.setWaitTimeWhenRunningTicks(compoundTag.getInt("RunningTicks"));
        this.setFromHand(compoundTag.getBoolean("FromHand"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", getVariant());
        compoundTag.putInt("RunTicks", this.getWaitTimeBeforeRunTicks());
        compoundTag.putInt("RunningTicks", this.getWaitTimeWhenRunningTicks());
        compoundTag.putBoolean("FromHand", this.fromHand());
    }

    public int getWaitTimeBeforeRunTicks() {
        return this.entityData.get(WAIT_TIME_BEFORE_RUN);
    }
    public void setWaitTimeBeforeRunTicks(int ticks) {
        this.entityData.set(WAIT_TIME_BEFORE_RUN, ticks);
    }

    public int getWaitTimeWhenRunningTicks() {
        return this.entityData.get(WAIT_TIME_WHEN_RUNNING);
    }
    public void setWaitTimeWhenRunningTicks(int ticks) {
        this.entityData.set(WAIT_TIME_WHEN_RUNNING, ticks);
    }


    public boolean isSleeping() {
        return this.getFlag(32);
    }

    public void setSleeping(boolean bl) {
        this.setFlag(32, bl);
    }

    private void setFlag(int i, boolean bl) {
        if (bl) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~i));
        }

    }

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MARKING = SynchedEntityData.defineId(HamsterOld.class, EntityDataSerializers.INT);

    private boolean getFlag(int i) {
        return (this.entityData.get(DATA_FLAGS_ID) & i) != 0;
    }

    public int getMarking() {
        return this.entityData.get(MARKING);
    }

    public void setMarking(int i) {
        this.entityData.set(MARKING, i);
    }


    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(HamsterOld.Variant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    public enum Marking {
        BLANK (0, "blank"),
        BANDED (1, "banded"),
        DOMINANT_SPOTS (2, "dominant_spots"),
        ROAN (3, "roan"),
        BELLY (4, "belly");

        private static final IntFunction<HamsterOld.Marking> BY_ID = ByIdMap.continuous(HamsterOld.Marking::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
        private final String name;

        private Marking(int j, String string2) {
            this.id = j;
            this.name = string2;
        }

        public int getId() {
            return this.id;
        }

        public static HamsterOld.Marking byId(int i) {
            return BY_ID.apply(i);
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
        BLACK (6, "black");

        public static final HamsterOld.Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(HamsterOld.Variant::getId)).toArray(HamsterOld.Variant[]::new);
        private final int id;
        private final String name;

        private Variant(int j, String string2) {
            this.id = j;
            this.name = string2;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static HamsterOld.Variant getTypeById(int id) {
            for (HamsterOld.Variant type : values()) {
                if (type.id == id) return type;
            }
            return HamsterOld.Variant.CHAMPAGNE;
        }
    }

    void wakeUp() {
        this.setSleeping(false);
    }

    public void tick() {
        super.tick();
        if (this.isEffectiveAi()) {
            if (this.isInWater() || this.getTarget() != null || this.level().isThundering()) {
                this.wakeUp();
            }

            if (this.isInWater() || this.isSleeping()) {
                this.setInSittingPose(false);
            }
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.level();

        this.blockPosition();
        if (this.level().getBlockState(this.blockPosition()).is(HamstersBlocks.HAMSTER_WHEEL)) {
            this.setDeltaMovement(0, 0, 0);
        }

        // System.out.println(getWaitTimeWhenRunningTicks());

        if (this.getWaitTimeWhenRunningTicks() > 0) {
            this.setWaitTimeWhenRunningTicks(this.getWaitTimeWhenRunningTicks() - 1);
        }
        if (this.isPassenger() && this.getVehicle() instanceof SeatEntity && this.getWaitTimeWhenRunningTicks() == 0) {
            this.setWaitTimeBeforeRunTicks(this.random.nextInt(400) + 1200);
            this.stopRiding();
            clearStates();
        }

        if (this.isInterested()) {
            if (this.tickCount % 40 == 0) {
                this.playBegSound();
            }
        }

        if (this.isAlive() && !this.isInterested() && !this.isSleeping() && !this.isImmobile() && this.getTarget() == null) {
            List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            for (Player player : list) {
                if (!player.isSpectator() && player.isHolding(FOOD_ITEMS) && distanceToSqr(player) < 2.0D) {
                    this.setIsInterested(true);
                    this.getNavigation().stop();
                }
            }
        }

        if (this.isInterested() && (this.getTarget() == null)) {
            List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            for (Player player : list) {
                if (!player.isSpectator() && player.isHolding(FOOD_ITEMS) && distanceToSqr(player) > 2.0D) {
                    this.setIsInterested(false);
                }
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }
    }

    // endregion

    // region BREEDING / VARIANTS / MIXING

    public boolean canMate(Animal animal) {
        if (!this.isTame()) {
            return false;
        } else if (!(animal instanceof HamsterOld)) {
            return false;
        } else {
            HamsterOld hamsterOld = (HamsterOld) animal;
            return hamsterOld.isTame() && super.canMate(animal);
        }
    }


    public boolean fromHand() {
        return this.entityData.get(FROM_HAND);
    }

    public void setFromHand(boolean fromHand) {
        this.entityData.set(FROM_HAND, fromHand);
    }

    // endregion


    // region PICK UP ITEMS

    @Override
    public boolean canTakeItem(ItemStack pItemstack) {
        EquipmentSlot slot = getEquipmentSlotForItem(pItemstack);
        if (!this.getItemBySlot(slot).isEmpty()) {
            return false;
        } else {
            return slot == EquipmentSlot.MAINHAND && super.canTakeItem(pItemstack);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack stack = pItemEntity.getItem();
        if (!this.isSleeping()) {
            if (this.getMainHandItem().isEmpty() && FOOD_ITEMS.test(stack)) {
                this.onItemPickup(pItemEntity);
                this.setItemSlot(EquipmentSlot.MAINHAND, stack);
                this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
                this.take(pItemEntity, stack.getCount());
                pItemEntity.discard();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.getMainHandItem().isEmpty() && !this.level().isClientSide) {
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, this.getMainHandItem());
            itemEntity.setPickUpDelay(40);
            itemEntity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level().addFreshEntity(itemEntity);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        return super.hurt(pSource, pAmount);
    }

    // endregion

    // region SPAWNING

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(random, pDifficulty);
        if (pSpawnData == null) {
            RandomSource randomSource = pLevel.getRandom();
            this.setVariant(Variant.values()[randomSource.nextInt(Variant.values().length)]);
        }
        return pSpawnData;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance pDifficulty) {
        if (random.nextFloat() < 0.2F) {
            float chance = random.nextFloat();
            ItemStack stack;
            if (chance < 0.1F) {
                stack = new ItemStack(Items.BEETROOT_SEEDS);
            } else if (chance < 0.15F) {
                stack = new ItemStack(Items.PUMPKIN_SEEDS);
            } else if (chance < 0.3F) {
                stack = new ItemStack(Items.MELON_SEEDS);
            } else {
                stack = new ItemStack(Items.WHEAT_SEEDS);
            }

            this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }
    }

    // endregion


    // region GECKOLIB

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends HamsterOld> PlayState animController(final AnimationState<E> event) {
        if (this.isSleeping()) {
            event.setAnimation(SLEEP);
        } else if (this.isInterested() || this.isInSittingPose()) {
            event.setAnimation(STANDING);
        }  else if (event.isMoving()) {
            if (this.isSprinting()) {
                event.setControllerSpeed(1.3F);
                event.setAnimation(RUN);
            } else {
                event.setControllerSpeed(1.1F);
                event.setAnimation(WALK);
            }
        }  else if (this.isPassenger() && this.getVehicle() instanceof SeatEntity ) {
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

    // region GOALS

    public class HamsterLookControl extends LookControl {
        public HamsterLookControl() {
            super(HamsterOld.this);
        }

        public void tick() {
            if (!(HamsterOld.this.getVehicle() instanceof SeatEntity)) {
                super.tick();
            } else {
                BlockState state = HamsterOld.this.level().getBlockState(HamsterOld.this.blockPosition());
                if (state.is(HamstersBlocks.HAMSTER_WHEEL)) {
                    BlockPos pos1;

                    if (state.getValue(FACING) == Direction.SOUTH) {
                        pos1 = HamsterOld.this.blockPosition().east(1);
                    } else if (state.getValue(FACING) == Direction.NORTH) {
                        pos1 = HamsterOld.this.blockPosition().west(1);
                    } else if (state.getValue(FACING) == Direction.EAST) {
                        pos1 = HamsterOld.this.blockPosition().north(1);
                    } else {
                        pos1 = HamsterOld.this.blockPosition().south(1);
                    }
                    HamsterOld.this.setSleeping(false);
                    HamsterOld.this.setInSittingPose(false);
                    HamsterOld.this.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(pos1.getX() + 0.5f, pos1.getY() + 0.5f, pos1.getZ() + 0.5f));
                }
            }
        }

        protected boolean resetXRotOnTick() {
            return !(HamsterOld.this.getVehicle() instanceof SeatEntity);
        }
    }

    class HamsterGoToWheelGoal extends Goal {
        private final Predicate<BlockState> VALID_GATHERING_BLOCKS;
        @Nullable
        private Vec3 wheelPos;

        HamsterGoToWheelGoal() {
            this.VALID_GATHERING_BLOCKS = blockState -> {
                if (blockState.is(HamstersBlocks.HAMSTER_WHEEL)) {
                    return !blockState.hasProperty(BlockStateProperties.WATERLOGGED) || !blockState.getValue(BlockStateProperties.WATERLOGGED);
                }
                return false;
            };
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {

            Optional<BlockPos> optional = this.findNearbyResource();
            if (optional.isPresent() && !HamsterWheelBlock.isOccupied(HamsterOld.this.level(), optional.get()) && HamsterOld.this.getWaitTimeBeforeRunTicks() == 0) {
                HamsterOld.this.navigation.moveTo((double) optional.get().getX() + 0.5, optional.get().getY(), (double) optional.get().getZ() + 0.5, 1.2f);
                return !HamsterOld.this.level().isRaining() && !HamsterOld.this.isSleeping() && !HamsterOld.this.isInSittingPose();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            Optional<BlockPos> optional = this.findNearbyResource();
            if (optional.isPresent() && !HamsterWheelBlock.isOccupied(HamsterOld.this.level(), optional.get()) && HamsterOld.this.getWaitTimeBeforeRunTicks() == 0) {
                return !HamsterOld.this.level().isRaining() && !HamsterOld.this.isSleeping() && !HamsterOld.this.isInSittingPose();
            }
            return false;
        }

        @Override
        public void stop() {
            HamsterOld.this.navigation.stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            Optional<BlockPos> optional = this.findNearbyResource();

            if (HamsterWheelBlock.isOccupied(HamsterOld.this.level(), optional.get())) {
                stop();
            }

            if (!HamsterWheelBlock.isOccupied(HamsterOld.this.level(), optional.get()) && HamsterOld.this.getWaitTimeBeforeRunTicks() == 0) {

                Vec3 vec3 = Vec3.atBottomCenterOf(optional.get());
                if (vec3.distanceTo(HamsterOld.this.position()) > 1.4) {
                    wheelPos = vec3;
                    this.setWantedPos();
                    return;
                }
                if (wheelPos == null) {
                    this.wheelPos = vec3;
                }
                if (HamsterOld.this.position().distanceTo(this.wheelPos) <= 1.4) {
                    HamsterOld.this.setWaitTimeWhenRunningTicks(HamsterOld.this.random.nextInt(300) + 100);
                    HamsterWheelBlock.sitDown(HamsterOld.this.level(), optional.get(), HamsterOld.this);
                    this.stop();
                }
            }

        }

        private void setWantedPos() {
            HamsterOld.this.getMoveControl().setWantedPosition(this.wheelPos.x(), this.wheelPos.y(), this.wheelPos.z(), 0.7f);
        }


        private Optional<BlockPos> findNearbyResource() {
            return this.findNearestBlock(this.VALID_GATHERING_BLOCKS);
        }

        private Optional<BlockPos> findNearestBlock(Predicate<BlockState> predicate) {
            BlockPos blockPos = HamsterOld.this.blockPosition();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            int i = 0;
            while ((double)i <= 5.0) {
                int j = 0;
                while ((double)j < 5.0) {
                    int k = 0;
                    while (k <= j) {
                        int l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            mutableBlockPos.setWithOffset(blockPos, k, i - 1, l);
                            if (blockPos.closerThan(mutableBlockPos, 5.0) && predicate.test(HamsterOld.this.level().getBlockState(mutableBlockPos))) {
                                return Optional.of(mutableBlockPos);
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                    ++j;
                }
                i = i > 0 ? -i : 1 - i;
            }
            return Optional.empty();
        }
    }

    private class RunInWheelGoal extends Goal {

        public RunInWheelGoal() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        public boolean canUse() {
            return !HamsterOld.this.isSleeping() && !HamsterOld.this.isInPowderSnow && (HamsterOld.this.isPassenger() && HamsterOld.this.getVehicle() instanceof SeatEntity);
        }

        public boolean canContinueToUse() {
            return (HamsterOld.this.isPassenger() && HamsterOld.this.getVehicle() instanceof SeatEntity);
        }
    }

    private class SleepGoal extends Goal {
        private final TargetingConditions alertableTargeting = TargetingConditions.forNonCombat().range(6.0).ignoreLineOfSight().selector(new HamsterAlertableEntitiesSelector());
        private final int WAIT_TIME_BEFORE_SLEEP = random.nextInt(100) + 100;
        private int countdown;

        public SleepGoal() {
            super();
            this.countdown = HamsterOld.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        public boolean canUse() {
            if (HamsterOld.this.xxa == 0.0F && HamsterOld.this.yya == 0.0F && HamsterOld.this.zza == 0.0F) {
                return this.canSleep() || HamsterOld.this.isSleeping();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return ((HamsterOld.this.level().getDayTime() >= 1000 && HamsterOld.this.level().getDayTime() <= 10000) || (HamsterOld.this.level().getDayTime() >= 16000 && HamsterOld.this.level().getDayTime() <= 21000)) && !HamsterOld.this.isInPowderSnow && !this.alertable() && !HamsterOld.this.isPassenger();
            }
        }

        public void stop() {
            this.countdown = HamsterOld.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
            clearStates();
        }

        public void start() {
            HamsterOld.this.setInSittingPose(false);
            HamsterOld.this.setIsInterested(false);
            HamsterOld.this.setJumping(false);
            HamsterOld.this.setSleeping(true);
            HamsterOld.this.getNavigation().stop();
            HamsterOld.this.getMoveControl().setWantedPosition(HamsterOld.this.getX(), HamsterOld.this.getY(), HamsterOld.this.getZ(), 0.0);
        }


        protected boolean alertable() {
            return !HamsterOld.this.level().getNearbyEntities(LivingEntity.class, this.alertableTargeting, HamsterOld.this, HamsterOld.this.getBoundingBox().inflate(12.0, 6.0, 12.0)).isEmpty();
        }
    }

    public static class HamsterAlertableEntitiesSelector implements Predicate<LivingEntity> {
        public HamsterAlertableEntitiesSelector() {
        }

        public boolean test(LivingEntity livingEntity) {
            if (livingEntity instanceof HamsterOld) {
                return false;
            } else if (livingEntity instanceof Player && (livingEntity.isSpectator() || ((Player)livingEntity).isCreative())) {
                return false;
            } else {
                return !livingEntity.isSleeping() && !livingEntity.isDiscrete();
            }
        }
    }
    // endregion
}
