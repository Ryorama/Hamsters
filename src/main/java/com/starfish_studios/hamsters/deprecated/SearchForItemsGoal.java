package com.starfish_studios.hamsters.deprecated;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.EnumSet;
import java.util.List;

public class SearchForItemsGoal extends Goal {

    private final HamsterOld mob;
    private final double speedModifier;
    private final double horizontalSearchRange;
    private final double verticalSearchRange;
    private final Ingredient ingredient;

    public SearchForItemsGoal(HamsterOld mob, double speedModifier, Ingredient ingredient, double horizontalSearchRange, double verticalSearchRange) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.ingredient = ingredient;
        this.horizontalSearchRange = horizontalSearchRange;
        this.verticalSearchRange = verticalSearchRange;
    }

    @Override
    public boolean canUse() {
        if (this.mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && !this.mob.isInSittingPose()) return !this.getNearbyItems().isEmpty() && this.mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !mob.isInSittingPose();
    }

    @Override
    public void tick() {
        if (this.mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && !this.getNearbyItems().isEmpty()) this.mob.getNavigation().moveTo(this.getNearbyItems().get(0), this.speedModifier);
    }

    @Override
    public void start() {
        if (!this.getNearbyItems().isEmpty()) this.mob.getNavigation().moveTo(this.getNearbyItems().get(0), this.speedModifier);
    }

    private List<ItemEntity> getNearbyItems() {
        return this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(this.horizontalSearchRange, this.verticalSearchRange, this.horizontalSearchRange), itemEntity -> this.ingredient.test(itemEntity.getItem()));
    }
}