package com.kaboomroads.tehshadur.block;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.custom.DesolisAntennaBlock;
import com.kaboomroads.tehshadur.block.custom.DesolisCannonBlock;
import com.kaboomroads.tehshadur.block.custom.DivineDominanceBlock;
import com.kaboomroads.tehshadur.block.custom.OmenMonolithBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {
    public static final Block DIVINE_DOMINANCE = register(
            "divine_dominance",
            DivineDominanceBlock::new,
            BlockBehaviour.Properties.of()
                    .lightLevel(state -> 15)
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(-1, Float.MAX_VALUE)
    );
    public static final Block OMEN_MONOLITH = register(
            "omen_monolith",
            OmenMonolithBlock::new,
            BlockBehaviour.Properties.of()
                    .lightLevel(state -> 15)
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(-1, Float.MAX_VALUE)
    );
    public static final Block DESOLIS_CANNON = register(
            "desolis_cannon",
            DesolisCannonBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(6.0F, 6.0F)
    );
    public static final Block DESOLIS_ANTENNA = register(
            "desolis_antenna",
            DesolisAntennaBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
    );

    public static Block register(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        Block block = function.apply(properties.setId(resourceKey));
        Block registered = Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
        for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
            Block.BLOCK_STATE_REGISTRY.add(blockState);
            blockState.initCache();
        }
        block.getLootTable();
        return registered;
    }

    public static Block register(ResourceKey<Block> resourceKey, BlockBehaviour.Properties properties) {
        return register(resourceKey, Block::new, properties);
    }

    private static ResourceKey<Block> blockId(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name));
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return register(blockId(name), function, properties);
    }

    private static Block register(String name, BlockBehaviour.Properties properties) {
        return register(name, Block::new, properties);
    }

    public static void init() {
    }
}
