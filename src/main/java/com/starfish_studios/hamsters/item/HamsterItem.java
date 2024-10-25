package com.starfish_studios.hamsters.item;

import com.starfish_studios.hamsters.entity.Hamster;
import com.starfish_studios.hamsters.registry.HamstersEntityType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
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

        Hamster hamster = HamstersEntityType.HAMSTER.create(useOnContext.getLevel());
        assert hamster != null;

        if (itemStack.hasCustomHoverName()) hamster.setCustomName(itemStack.getHoverName());
        if (itemStack.hasTag() && itemStack.getTag() != null) hamster.load(itemStack.getTag());

        hamster.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, Objects.requireNonNull(useOnContext.getPlayer()).getYRot(), 0.0F);

        hamster.playSound(SoundEvents.CHICKEN_EGG);
        useOnContext.getLevel().addFreshEntity(hamster);
        if (!useOnContext.getPlayer().getAbilities().instabuild) useOnContext.getPlayer().setItemInHand(useOnContext.getHand(), ItemStack.EMPTY);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {

        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) return;

        if (compoundTag.contains("Variant", 3)) list.add(Component.translatable("tooltip.hamsters." + Hamster.Variant.getTypeById(compoundTag.getInt("Variant")).getName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        if (compoundTag.getInt("Age") < 0) list.add(Component.translatable("tooltip.hamsters.baby").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE));
        if (compoundTag.hasUUID("Owner")) list.add(Component.translatable("tooltip.hamsters.tamed").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE));
    }
}