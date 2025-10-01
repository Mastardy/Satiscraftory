package io.github.mastardy.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mastardy.recipe.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public record CraftingBenchRecipe(Ingredient inputItem, int inputCount, ItemStack output) implements Recipe<CraftingBenchRecipeInput> {
    @Override
    public boolean matches(CraftingBenchRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }

        var inputStack = input.getStackInSlot(0);
        return inputItem.test(inputStack) && inputStack.getCount() >= inputCount;
    }

    @Override
    public ItemStack craft(CraftingBenchRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public RecipeType<CraftingBenchRecipe> getType() {
        return ModRecipes.CRAFTING_BENCH_TYPE;
    }

    @Override
    public RecipeSerializer<CraftingBenchRecipe> getSerializer() {
        return ModRecipes.CRAFTING_BENCH_SERIALIZER;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forSingleSlot(inputItem);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class Serializer implements RecipeSerializer<CraftingBenchRecipe> {
        public static final MapCodec<CraftingBenchRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CraftingBenchRecipe::inputItem),
                Codec.INT.fieldOf("count").forGetter(CraftingBenchRecipe::inputCount),
                ItemStack.CODEC.fieldOf("result").forGetter(CraftingBenchRecipe::output)
        ).apply(inst, CraftingBenchRecipe::new));

        public static final PacketCodec<RegistryByteBuf, CraftingBenchRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, CraftingBenchRecipe::inputItem,
                PacketCodecs.VAR_INT, CraftingBenchRecipe::inputCount,
                ItemStack.PACKET_CODEC, CraftingBenchRecipe::output,
                CraftingBenchRecipe::new);

        @Override
        public MapCodec<CraftingBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CraftingBenchRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
