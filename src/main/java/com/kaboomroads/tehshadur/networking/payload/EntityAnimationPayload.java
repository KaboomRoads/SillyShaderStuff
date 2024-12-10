package com.kaboomroads.tehshadur.networking.payload;

import com.kaboomroads.tehshadur.networking.ModPackets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record EntityAnimationPayload(
        int entityId,
        int animationId
) implements CustomPacketPayload {
    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ModPackets.ENTITY_ANIMATION;
    }
}
