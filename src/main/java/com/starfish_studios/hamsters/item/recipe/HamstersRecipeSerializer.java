package com.starfish_studios.hamsters.item.recipe;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public interface HamstersRecipeSerializer {
    SimpleCraftingRecipeSerializer<ChocolateHamsterRecipe> CHOCOLATE_HAMSTER = register("chocolate_hamster", new SimpleCraftingRecipeSerializer<>(ChocolateHamsterRecipe::new));


    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String string, S recipeSerializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, string, recipeSerializer);
    }
}
