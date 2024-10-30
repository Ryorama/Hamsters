package com.starfish_studios.hamsters.item;

import com.starfish_studios.hamsters.entity.HamsterNew;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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

        BlockPos blockPos = new BlockPlaceContext(useOnContext).getClickedPos();
        ItemStack itemStack = useOnContext.getItemInHand();

        HamsterNew hamster = HamstersEntityType.HAMSTER_NEW.create(useOnContext.getLevel());
        assert hamster != null;

        if (itemStack.hasCustomHoverName()) hamster.setCustomName(itemStack.getHoverName());
        if (itemStack.hasTag() && itemStack.getTag() != null) hamster.load(itemStack.getTag());

        hamster.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, Objects.requireNonNull(useOnContext.getPlayer()).getYRot(), 0.0F);
        hamster.setOwnerUUID(useOnContext.getPlayer().getUUID());

        hamster.playSound(SoundEvents.CHICKEN_EGG);
        useOnContext.getLevel().addFreshEntity(hamster);
        if (!useOnContext.getPlayer().getAbilities().instabuild) useOnContext.getPlayer().setItemInHand(useOnContext.getHand(), ItemStack.EMPTY);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {

        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) return;

        if (compoundTag.contains("Marking", 3) && compoundTag.getInt("Marking") != 0) {
            if (compoundTag.contains("Variant", 3) && compoundTag.getInt("Variant") == HamsterNew.Variant.getTypeById(0).getId()) {
                list.add(Component.translatable("tooltip.hamsters.recessive").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)
                        .append(" ")
                        .append(Component.translatable("tooltip.hamsters." + HamsterNew.Marking.getTypeById(compoundTag.getInt("Marking")).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)));
            } else {
                list.add(Component.translatable("tooltip.hamsters." + HamsterNew.Marking.getTypeById(compoundTag.getInt("Marking")).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }
        }

        if (compoundTag.contains("Variant", 3)) list.add(Component.translatable("tooltip.hamsters." + HamsterNew.Variant.getTypeById(compoundTag.getInt("Variant")).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        if (compoundTag.getInt("Age") < 0) list.add(Component.translatable("tooltip.hamsters.baby").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE));
        if (compoundTag.hasUUID("Owner")) list.add(Component.translatable("tooltip.hamsters.tamed").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE));
    }
}