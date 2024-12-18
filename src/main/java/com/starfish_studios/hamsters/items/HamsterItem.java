package com.starfish_studios.hamsters.items;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entities.Hamster;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
import com.starfish_studios.hamsters.registry.HamstersSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

public class HamsterItem extends Item {

    public HamsterItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        this.spawnHamster(useOnContext.getLevel(), new BlockPlaceContext(useOnContext).getClickedPos(), useOnContext.getItemInHand(), useOnContext.getPlayer(), null);
        return InteractionResult.SUCCESS;
    }

    public void spawnHamster(Level level, BlockPos blockPos, ItemStack itemStack, Player player, Entity entityToMount) {

        Hamster hamster = HamstersEntityTypes.HAMSTER.create(level);
        assert hamster != null;

        if (itemStack.hasCustomHoverName()) hamster.setCustomName(itemStack.getHoverName());
        if (itemStack.hasTag() && itemStack.getTag() != null) hamster.load(itemStack.getTag());

        hamster.moveTo(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, Objects.requireNonNull(player).getYRot(), 0.0F);

        if (!hamster.isTame()) hamster.setTame(true);
        hamster.setOwnerUUID(player.getUUID());
        hamster.setOrderedToSit(false);
        hamster.setInSittingPose(false);

        if (entityToMount != null) hamster.startRiding(entityToMount);
        level.addFreshEntity(hamster);

        hamster.playSound(HamstersSoundEvents.HAMSTER_PLACE);
        hamster.gameEvent(GameEvent.ENTITY_PLACE, player);

        if (!player.getAbilities().instabuild) player.setItemInHand(player.getUsedItemHand(), ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {

        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) return;

        String tooltipString = "tooltip." + Hamsters.MOD_ID + ".";
        String markingTag = "marking";
        String variantTag = "variant";

        if (compoundTag.contains(markingTag, 3) && compoundTag.getInt(markingTag) != 0) {
            if (compoundTag.contains(variantTag, 3) && compoundTag.getInt(variantTag) == Hamster.Variant.getTypeById(0).getId()) {
                list.add(Component.translatable(tooltipString + "recessive").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)
                .append(CommonComponents.space())
                .append(Component.translatable(tooltipString + Hamster.Marking.getTypeById(compoundTag.getInt(markingTag)).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
            } else {
                list.add(Component.translatable(tooltipString + Hamster.Marking.getTypeById(compoundTag.getInt(markingTag)).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }
        }

        if (compoundTag.contains(variantTag, 3)) list.add(Component.translatable(tooltipString + Hamster.Variant.getTypeById(compoundTag.getInt(variantTag)).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        if (compoundTag.getInt("Age") < 0) list.add(Component.translatable(tooltipString + "baby").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE));
    }
}