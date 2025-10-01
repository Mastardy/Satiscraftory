package io.github.mastardy;

import io.github.mastardy.blocks.ModBlocks;
import io.github.mastardy.items.ModItems;
import io.github.mastardy.recipe.ModRecipes;
import io.github.mastardy.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Satiscraftory implements ModInitializer {
    public static final String MOD_ID = "satiscraftory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();

        ModRecipes.registerRecipes();
        ModScreenHandlers.registerScreenHandlers();
    }
}