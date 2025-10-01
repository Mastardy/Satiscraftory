package io.github.mastardy.recipe.custom;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record CraftingBenchRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
