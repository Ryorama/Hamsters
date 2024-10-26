package com.starfish_studios.hamsters.entity.common;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepGoal<E extends PathfinderMob & SleepingAnimal> extends Goal {
    public final E mob;

    public SleepGoal(E mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.mob = mob;
    }

    @Override
    public void start() {
        // After the goal is activated, makes the mob stop jumping, start sleeping, and stop moving.
        mob.setJumping(false);
        mob.setSleeping(true);
        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0D);
    }

    @Override
    public boolean canUse() {
        // If the mob is not moving and can sleep or is already sleeping, return true
        if (mob.xxa == 0.0F && mob.yya == 0.0F && mob.zza == 0.0F) {
            return mob.canSleep() || mob.isSleeping();
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return mob.canSleep();
    } // Allows the mob to continue sleeping if it can sleep (i.e., if it is not interrupted)

    @Override
    public void stop() {
        mob.setSleeping(false);
    } // Wakes up the mob when the goal ends
}
