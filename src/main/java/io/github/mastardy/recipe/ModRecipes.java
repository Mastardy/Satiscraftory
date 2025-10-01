package io.github.mastardy.recipe;

import io.github.mastardy.Satiscraftory;
import io.github.mastardy.recipe.custom.CraftingBenchRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<CraftingBenchRecipe> CRAFTING_BENCH_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(Satiscraftory.MOD_ID, "crafting_bench"), new CraftingBenchRecipe.Serializer());
    public static final RecipeType<CraftingBenchRecipe> CRAFTING_BENCH_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(Satiscraftory.MOD_ID, "crafting_bench"), new RecipeType<CraftingBenchRecipe>() {
        @Override
        public String toString() {
            return "crafting_bench";
        }
    });

    public static void registerRecipes() {
    }
}
