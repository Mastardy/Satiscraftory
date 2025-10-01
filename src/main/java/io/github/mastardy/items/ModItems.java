package io.github.mastardy.items;

import io.github.mastardy.Satiscraftory;
import io.github.mastardy.blocks.ModBlocks;
import io.github.mastardy.items.custom.IronRod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final RegistryKey<ItemGroup> MOD_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Satiscraftory.MOD_ID, "item_group"));
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.IRON_PLATE)).displayName(Text.translatable("itemGroup.mod_id")).build();

    public static final Item IRON_ROD = register("iron_rod", IronRod::new, new Item.Settings());
    public static final Item IRON_PLATE = register("iron_plate", Item::new, new Item.Settings());

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, MOD_ITEM_GROUP_KEY, MOD_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(MOD_ITEM_GROUP_KEY).register((itemGroup) -> {
            // INGREDIENTS
            itemGroup.add(ModItems.IRON_ROD);
            itemGroup.add(ModItems.IRON_PLATE);
            // BlOCKS
            itemGroup.add(ModBlocks.CRAFTING_BENCH.asItem());
        });
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Satiscraftory.MOD_ID, name));

        Item item = itemFactory.apply(settings.registryKey(itemKey));

        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }
}
