package com.kaboomroads.tehshadur.item;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {
    public static final Item DIVINE_DOMINANCE = registerBlock(ModBlocks.DIVINE_DOMINANCE);
    public static final Item OMEN_MONOLITH = registerBlock(ModBlocks.OMEN_MONOLITH);

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }

    private static ResourceKey<Item> itemId(String string) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, string));
    }

    private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> resourceKey) {
        return ResourceKey.create(Registries.ITEM, resourceKey.location());
    }

    public static Item registerBlock(Block block) {
        return registerBlock(block, BlockItem::new);
    }

    public static Item registerBlock(Block block, Item.Properties properties) {
        return registerBlock(block, BlockItem::new, properties);
    }

    public static Item registerBlock(Block block, UnaryOperator<Item.Properties> unaryOperator) {
        return registerBlock(block, (blockx, properties) -> new BlockItem(blockx, unaryOperator.apply(properties)));
    }

    public static Item registerBlock(Block block, Block... blocks) {
        Item item = registerBlock(block);

        for (Block block2 : blocks) {
            Item.BY_BLOCK.put(block2, item);
        }

        return item;
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction) {
        return registerBlock(block, biFunction, new Item.Properties());
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction, Item.Properties properties) {
        return registerItem(blockIdToItemId(block.builtInRegistryHolder().key()), propertiesx -> biFunction.apply(block, propertiesx), properties.useBlockDescriptionPrefix());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function) {
        return registerItem(itemId(string), function, new Item.Properties());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function, Item.Properties properties) {
        return registerItem(itemId(string), function, properties);
    }

    public static Item registerItem(String string, Item.Properties properties) {
        return registerItem(itemId(string), Item::new, properties);
    }

    public static Item registerItem(String string) {
        return registerItem(itemId(string), Item::new, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function) {
        return registerItem(resourceKey, function, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = function.apply(properties.setId(resourceKey));
        if (item instanceof BlockItem blockItem) blockItem.registerBlocks(Item.BY_BLOCK, item);
        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static void init() {
    }
}
