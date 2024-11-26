package com.kaboomroads.tehshadur.border;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BorderProviderType<T extends BorderProvider> {
    @NotNull
    public abstract CompoundTag saveData(T borderProvider, HolderLookup.Provider registries);

    @NotNull
    public abstract T loadData(CompoundTag tag, HolderLookup.Provider registries);

    @NotNull
    public CompoundTag save(T borderProvider, HolderLookup.Provider registries) {
        CompoundTag finalTag = new CompoundTag();
        finalTag.putString("type", BorderProviderTypes.REGISTRY.getKey(borderProvider.getBorderProviderType()).toString());
        finalTag.put("data", saveData(borderProvider, registries));
        return finalTag;
    }

    @NotNull
    public static <T extends BorderProvider> T load(CompoundTag tag, HolderLookup.Provider registries) {
        HolderGetter<BorderProviderType<?>> holderGetter = registries.lookupOrThrow(BorderProviderTypes.REGISTRY_KEY);
        Holder.Reference<BorderProviderType<?>> reference = holderGetter.getOrThrow(ResourceKey.create(BorderProviderTypes.REGISTRY_KEY, ResourceLocation.parse(tag.getString("type"))));
        BorderProviderType<T> type = (BorderProviderType<T>) reference.value();
        return type.loadData(tag.getCompound("data"), registries);
    }

    public abstract void encode(RegistryFriendlyByteBuf buf, T borderProvider);

    @NotNull
    public abstract T decode(RegistryFriendlyByteBuf buf);
}
