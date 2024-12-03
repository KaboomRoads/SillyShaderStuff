package com.kaboomroads.tehshadur.item;

import com.kaboomroads.tehshadur.TehShadur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModComponents {
    public static final DataComponentType<List<BlockPos>> BLOCK_POSITIONS = register(
            "block_positions", builder -> builder.persistent(BlockPos.CODEC.listOf()).networkSynchronized(BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list())).cacheEncoding()
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name), builder.apply(DataComponentType.builder()).build());
    }
}
