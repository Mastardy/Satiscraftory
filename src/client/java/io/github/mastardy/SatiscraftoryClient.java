package io.github.mastardy;

import io.github.mastardy.screen.CraftingBenchScreen;
import io.github.mastardy.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SatiscraftoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(
                ModScreenHandlers.CRAFTING_BENCH_SCREEN_HANDLER,
                CraftingBenchScreen::new
        );
    }
}