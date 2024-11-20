package com.starfish_studios.hamsters.items.recipes;

import com.starfish_studios.hamsters.registry.HamstersItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChocolateHamsterRecipe extends CustomRecipe {

    public ChocolateHamsterRecipe(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory) {
        super(resourceLocation, craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {

        boolean hasHamster = false;
        boolean hasCocoaBeans = false;
        boolean hasSugar = false;

        for (int containerSize = 0; containerSize < container.getContainerSize(); containerSize++) {

            ItemStack itemStack = container.getItem(containerSize);
            if (itemStack.isEmpty()) continue;

            if (itemStack.is(Items.COCOA_BEANS)) {
                hasCocoaBeans = true;
            } else if (itemStack.is(HamstersItems.HAMSTER)) {

                CompoundTag compoundTag = itemStack.getTag();
                String variantTag = "Variant";
                String markingTag = "Marking";

                hasHamster = compoundTag != null && compoundTag.contains(variantTag) && compoundTag.getInt(variantTag) == 5 && compoundTag.contains(markingTag) && compoundTag.getInt(markingTag) == 0;

            } else if (itemStack.is(Items.SUGAR)) {
                hasSugar = true;
            }
        }

        return hasHamster && hasCocoaBeans && hasSugar;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return HamstersRecipeSerializer.CHOCOLATE_HAMSTER;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        return new ItemStack(HamstersItems.CHOCOLATE_HAMSTER);
    }
}