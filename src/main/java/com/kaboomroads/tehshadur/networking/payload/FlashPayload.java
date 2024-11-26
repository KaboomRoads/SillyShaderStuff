package com.kaboomroads.tehshadur.networking.payload;

import com.kaboomroads.tehshadur.networking.ModPackets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record FlashPayload(
        int flashColor,
        int flashFadeInTime,
        int flashStayTime,
        int flashFadeOutTime
) implements CustomPacketPayload {
    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ModPackets.FLASH;
    }
}
