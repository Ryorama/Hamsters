package com.starfish_studios.hamsters.items.recipes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public interface HamstersRecipeSerializer {

    SimpleCraftingRecipeSerializer<ChocolateHamsterRecipe> CHOCOLATE_HAMSTER = register("chocolate_hamster", new SimpleCraftingRecipeSerializer<>(ChocolateHamsterRecipe::new));

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S recipeSerializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, name, recipeSerializer);
    }
}