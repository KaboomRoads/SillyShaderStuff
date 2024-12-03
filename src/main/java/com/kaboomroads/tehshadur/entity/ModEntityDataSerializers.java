package com.kaboomroads.tehshadur.entity;

import com.kaboomroads.tehshadur.border.BorderProvider;
import com.kaboomroads.tehshadur.border.BorderProviderType;
import com.kaboomroads.tehshadur.border.BorderProviderTypes;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModEntityDataSerializers {
    public static final EntityDataSerializer<BorderProvider> BORDER_PROVIDER = EntityDataSerializer.forValueType(new StreamCodec<>() {
        @NotNull
        @Override
        public BorderProvider decode(RegistryFriendlyByteBuf buf) {
            Registry<BorderProviderType<?>> lookup = buf.registryAccess().lookupOrThrow(BorderProviderTypes.REGISTRY_KEY);
            BorderProviderType<?> type = lookup.getValue(ResourceLocation.parse(buf.readUtf()));
            return type.decode(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, BorderProvider borderProvider) {
            buf.writeUtf(buf.registryAccess().lookupOrThrow(BorderProviderTypes.REGISTRY_KEY).getKey(borderProvider.getBorderProviderType()).toString());
            ((BorderProviderType<BorderProvider>) borderProvider.getBorderProviderType()).encode(buf, borderProvider);
        }
    });

    public static void init() {
        EntityDataSerializers.registerSerializer(BORDER_PROVIDER);
    }
}
