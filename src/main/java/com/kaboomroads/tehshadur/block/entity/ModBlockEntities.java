package com.kaboomroads.tehshadur.block.entity;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.entity.custom.DesolisCannonBlockEntity;
import com.kaboomroads.tehshadur.block.entity.custom.DivineDominanceBlockEntity;
import com.kaboomroads.tehshadur.block.entity.custom.OmenMonolithBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

import java.util.Set;

public class ModBlockEntities {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final BlockEntityType<DivineDominanceBlockEntity> DIVINE_DOMINANCE = register("divine_dominance", DivineDominanceBlockEntity::new, ModBlocks.DIVINE_DOMINANCE);
    public static final BlockEntityType<OmenMonolithBlockEntity> OMEN_MONOLITH = register("omen_monolith", OmenMonolithBlockEntity::new, ModBlocks.OMEN_MONOLITH);
    public static final BlockEntityType<DesolisCannonBlockEntity> DESOLIS_CANNON = register("desolis_cannon", DesolisCannonBlockEntity::new, ModBlocks.DESOLIS_CANNON);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntitySupplier<? extends T> blockEntitySupplier, Block... blocks
    ) {
        if (blocks.length == 0) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", name);
        }

        Util.fetchChoiceType(References.BLOCK_ENTITY, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name), new BlockEntityType<>(blockEntitySupplier, Set.of(blocks)));
    }

    public static void init() {
    }
}
