package io.github.mastardy.screen.custom;

import io.github.mastardy.Satiscraftory;
import io.github.mastardy.blocks.ModBlocks;
import io.github.mastardy.recipe.custom.CraftingBenchRecipe;
import io.github.mastardy.screen.ModScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class CraftingBenchScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;

    final Property selectedRecipe;
    private final ArrayList<CraftingBenchRecipe> availableRecipes;

    public CraftingBenchScreenHandler(int syncId, PlayerInventory inventory, List<CraftingBenchRecipe> recipes) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY, recipes);
    }

    public CraftingBenchScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, List<CraftingBenchRecipe> recipes) {
        super(ModScreenHandlers.CRAFTING_BENCH_SCREEN_HANDLER, syncId);
        this.context = context;
        this.selectedRecipe = Property.create();
        this.availableRecipes = new ArrayList<>(recipes);

        this.addPlayerSlots(playerInventory, 8, 84);
        this.addProperty(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public ArrayList<CraftingBenchRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, ModBlocks.CRAFTING_BENCH);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        Satiscraftory.LOGGER.info("Called {} ID", id);

        if (id == -1) {
            return craftItem(player);
        }

        if (this.selectedRecipe.get() == id) {
            return false;
        } else {
            if (this.isInBounds(id)) {
                this.selectedRecipe.set(id);
            }

            return true;
        }
    }

    private boolean craftItem(PlayerEntity player) {
        if (player.getWorld().isClient) return false;
        if (this.selectedRecipe.get() < 0 || this.selectedRecipe.get() >= this.availableRecipes.size()) return false;

        CraftingBenchRecipe recipe = this.availableRecipes.get(this.selectedRecipe.get());

        if (!hasRequiredItems(player, recipe)) return false;
        if (!consumeIngredients(player, recipe)) return false;

        ItemStack output = recipe.output().copy();
        if (!player.getInventory().insertStack(output)) {
            player.dropItem(output, false);
        }

        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.getInventory().markDirty();

        return true;
    }

    private boolean hasRequiredItems(PlayerEntity player, CraftingBenchRecipe recipe) {
        int requiredCount = recipe.inputCount();
        int foundCount = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!recipe.inputItem().test(stack)) continue;

            foundCount += stack.getCount();
            if (foundCount < requiredCount) continue;

            return true;
        }

        return false;
    }

    private boolean consumeIngredients(PlayerEntity player, CraftingBenchRecipe recipe) {
        int remainingToConsume = recipe.inputCount();

        for (int i = 0; i < player.getInventory().size() && remainingToConsume > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (!recipe.inputItem().test(stack)) continue;

            int toRemove = Math.min(remainingToConsume, stack.getCount());
            stack.decrement(toRemove);
            remainingToConsume -= toRemove;

            if (stack.isEmpty()) player.getInventory().setStack(i, ItemStack.EMPTY);
        }

        return remainingToConsume == 0;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }
}
