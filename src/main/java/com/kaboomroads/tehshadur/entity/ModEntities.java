package com.kaboomroads.tehshadur.entity;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.entity.custom.Erasure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<Erasure> ERASURE = register(
            "erasure",
            EntityType.Builder.<Erasure>of(Erasure::new, MobCategory.MISC)
                    .noLootTable()
                    .fireImmune()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
    );

    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> resourceKey, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceKey, builder.build(resourceKey));
    }

    private static ResourceKey<EntityType<?>> entityId(String string) {
        return ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, string));
    }

    private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
        return register(entityId(key), builder);
    }

    public static void init() {
    }
}
