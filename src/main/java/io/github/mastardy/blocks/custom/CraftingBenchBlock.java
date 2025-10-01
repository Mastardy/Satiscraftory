package io.github.mastardy.blocks.custom;

import com.mojang.serialization.MapCodec;
import io.github.mastardy.recipe.ModRecipes;
import io.github.mastardy.recipe.custom.CraftingBenchRecipe;
import io.github.mastardy.screen.custom.CraftingBenchScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CraftingBenchBlock extends Block {
    public static final MapCodec<CraftingBenchBlock> CODEC = createCodec(CraftingBenchBlock::new);
    private static final Text TITLE = Text.of("Crafting Bench");

    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    public CraftingBenchBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        }

        return ActionResult.SUCCESS;
    }


    @Nullable
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        if (world.isClient()) return null;

        return new ExtendedScreenHandlerFactory<List<CraftingBenchRecipe>>() {
            @Override
            public List<CraftingBenchRecipe> getScreenOpeningData(ServerPlayerEntity player) {
                return getRecipes(player);
            }

            public Text getDisplayName() {
                return TITLE;
            }

            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new CraftingBenchScreenHandler(syncId, inv, ScreenHandlerContext.create(world, pos), getRecipes(player));
            }

            private List<CraftingBenchRecipe> getRecipes(PlayerEntity player) {
                var recipeManager = Objects.requireNonNull(player.getServer()).getRecipeManager();
                return recipeManager.getAllOfType(ModRecipes.CRAFTING_BENCH_TYPE).stream().map(RecipeEntry::value).toList();
            }
        };
    }
}

