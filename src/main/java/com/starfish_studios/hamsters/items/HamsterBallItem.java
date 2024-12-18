package com.starfish_studios.hamsters.items;

import com.starfish_studios.hamsters.entities.HamsterBall;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HamsterBallItem extends Item {

    private final DyeColor color;

    public HamsterBallItem(@Nullable DyeColor dyeColor, Properties properties) {
        super(properties);
        this.color = dyeColor;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {

        if (useOnContext.getClickedFace() == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {

            Level level = useOnContext.getLevel();
            BlockPos blockPos = new BlockPlaceContext(useOnContext).getClickedPos();
            ItemStack itemStack = useOnContext.getItemInHand();

            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            AABB aABB = HamstersEntityTypes.HAMSTER_BALL.getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());

            if (level.noCollision(null, aABB) && level.getEntities(null, aABB).isEmpty()) {

                if (level instanceof ServerLevel serverLevel) {

                    HamsterBall hamsterBall = HamstersEntityTypes.HAMSTER_BALL.create(serverLevel, itemStack.getTag(), EntityType.createDefaultStackConfig(serverLevel, itemStack, useOnContext.getPlayer()), blockPos, MobSpawnType.SPAWN_EGG, true, true);
                    if (hamsterBall == null) return InteractionResult.FAIL;
                    hamsterBall.setColor(this.color);

                    float rotation = (float) Mth.floor((Mth.wrapDegrees(useOnContext.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    hamsterBall.moveTo(hamsterBall.getX(), hamsterBall.getY(), hamsterBall.getZ(), rotation, 0.0F);
                    serverLevel.addFreshEntityWithPassengers(hamsterBall);

                    level.playSound(null, hamsterBall.getX(), hamsterBall.getY(), hamsterBall.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                    hamsterBall.gameEvent(GameEvent.ENTITY_PLACE, useOnContext.getPlayer());
                }

                if (useOnContext.getPlayer() != null && !useOnContext.getPlayer().getAbilities().instabuild) itemStack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide());

            } else {
                return InteractionResult.FAIL;
            }
        }
    }
}