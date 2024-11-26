package com.kaboomroads.tehshadur.border;

import com.kaboomroads.tehshadur.TehShadur;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class BorderProviderTypes {
    public static final ResourceKey<Registry<BorderProviderType<?>>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "border_provider"));
    public static final MappedRegistry<BorderProviderType<?>> REGISTRY = FabricRegistryBuilder
            .createSimple(REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final BorderProviderType<SimpleBorderProvider> SIMPLE = register("simple", new BorderProviderType<>() {
        @NotNull
        @Override
        public CompoundTag saveData(SimpleBorderProvider borderProvider, HolderLookup.Provider registries) {
            CompoundTag tag = new CompoundTag();
            CompoundTag boundsTag = new CompoundTag();
            AABB bounds = borderProvider.getBounds();
            if (bounds != null) {
                boundsTag.putFloat("minX", (float) bounds.minX);
                boundsTag.putFloat("minY", (float) bounds.minY);
                boundsTag.putFloat("minZ", (float) bounds.minZ);
                boundsTag.putFloat("maxX", (float) bounds.maxX);
                boundsTag.putFloat("maxY", (float) bounds.maxY);
                boundsTag.putFloat("maxZ", (float) bounds.maxZ);
                tag.put("bounds", boundsTag);
            }
            return tag;
        }

        @NotNull
        @Override
        public SimpleBorderProvider loadData(CompoundTag tag, HolderLookup.Provider registries) {
            AABB bounds;
            if (tag.contains("bounds", Tag.TAG_COMPOUND)) {
                CompoundTag boundsTag = tag.getCompound("bounds");
                bounds = new AABB(
                        boundsTag.getFloat("minX"),
                        boundsTag.getFloat("minY"),
                        boundsTag.getFloat("minZ"),
                        boundsTag.getFloat("maxX"),
                        boundsTag.getFloat("maxY"),
                        boundsTag.getFloat("maxZ")
                );
            } else bounds = null;
            return new SimpleBorderProvider(bounds);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SimpleBorderProvider borderProvider) {
            AABB bounds = borderProvider.getBounds();
            buf.writeNullable(bounds, (b, bb) -> {
                b.writeFloat((float) bb.minX);
                b.writeFloat((float) bb.minY);
                b.writeFloat((float) bb.minZ);
                b.writeFloat((float) bb.maxX);
                b.writeFloat((float) bb.maxY);
                b.writeFloat((float) bb.maxZ);
            });
        }

        @NotNull
        @Override
        public SimpleBorderProvider decode(RegistryFriendlyByteBuf buf) {
            AABB bounds = buf.readNullable(b -> new AABB(
                    b.readFloat(),
                    b.readFloat(),
                    b.readFloat(),
                    b.readFloat(),
                    b.readFloat(),
                    b.readFloat()
            ));
            return new SimpleBorderProvider(bounds);
        }
    });

    public static final BorderProviderType<ScaleChangingBorderProvider> SCALE_CHANGING = register("scaling", new BorderProviderType<>() {
        @NotNull
        @Override
        public CompoundTag saveData(ScaleChangingBorderProvider borderProvider, HolderLookup.Provider registries) {
            CompoundTag tag = SIMPLE.saveData(borderProvider, registries);
            tag.putLong("timeOfCollapse", borderProvider.timeOfCollapse);
            tag.putLong("maxTime", borderProvider.maxTime);
            tag.putFloat("threshold", borderProvider.threshold);
            tag.putFloat("minRadius", borderProvider.minRadius);
            tag.putFloat("maxRadius", borderProvider.maxRadius);
            return tag;
        }

        @NotNull
        @Override
        public ScaleChangingBorderProvider loadData(CompoundTag tag, HolderLookup.Provider registries) {
            SimpleBorderProvider simple = SIMPLE.loadData(tag, registries);
            return new ScaleChangingBorderProvider(
                    simple.bounds,
                    tag.getLong("timeOfCollapse"),
                    tag.getLong("maxTime"),
                    tag.getFloat("threshold"),
                    tag.getFloat("minRadius"),
                    tag.getFloat("maxRadius")
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ScaleChangingBorderProvider borderProvider) {
            SIMPLE.encode(buf, borderProvider);
            buf.writeLong(borderProvider.timeOfCollapse);
            buf.writeLong(borderProvider.maxTime);
            buf.writeFloat(borderProvider.threshold);
            buf.writeFloat(borderProvider.minRadius);
            buf.writeFloat(borderProvider.maxRadius);
        }

        @NotNull
        @Override
        public ScaleChangingBorderProvider decode(RegistryFriendlyByteBuf buf) {
            return new ScaleChangingBorderProvider(SIMPLE.decode(buf).bounds, buf.readLong(), buf.readLong(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    });


    public static <T extends BorderProvider> BorderProviderType<T> register(String name, BorderProviderType<T> type) {
        return Registry.register(REGISTRY, ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, name), type);
    }

    public static void init() {
    }
}
