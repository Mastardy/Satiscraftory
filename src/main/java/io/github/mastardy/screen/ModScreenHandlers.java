package io.github.mastardy.screen;

import io.github.mastardy.Satiscraftory;
import io.github.mastardy.recipe.custom.CraftingBenchRecipe;
import io.github.mastardy.screen.custom.CraftingBenchScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CraftingBenchScreenHandler> CRAFTING_BENCH_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(Satiscraftory.MOD_ID, "crafting_bench_screen_handler"),
            new ExtendedScreenHandlerType<>(CraftingBenchScreenHandler::new,
                    PacketCodecs.collection(ArrayList::new, CraftingBenchRecipe.Serializer.STREAM_CODEC)));

    public static void registerScreenHandlers() {

    }
}

