package com.starfish_studios.hamsters.item.recipe;

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
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasHamster = false;
        boolean hasCocoaBeans = false;
        boolean hasSugar = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.COCOA_BEANS)) {
                hasCocoaBeans = true;
            } else if (stack.is(HamstersItems.HAMSTER)) {
                CompoundTag compoundTag = stack.getTag();
                hasHamster = compoundTag != null && compoundTag.contains("Variant") && compoundTag.getInt("Variant") == 5
                        && compoundTag.contains("Marking") && compoundTag.getInt("Marking") == 0;
            } else if (stack.is(Items.SUGAR)) {
                hasSugar = true;
            }
        }
        return hasHamster && hasCocoaBeans && hasSugar;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return new ItemStack(HamstersItems.CHOCOLATE_HAMSTER);
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return HamstersRecipeSerializer.CHOCOLATE_HAMSTER;
    }
}
