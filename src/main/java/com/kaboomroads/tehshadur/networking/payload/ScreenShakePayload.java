package com.kaboomroads.tehshadur.networking.payload;

import com.kaboomroads.tehshadur.networking.ModPackets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ScreenShakePayload(
        float screenShakeStrength,
        float screenShakeInterval,
        int screenShakeEaseInTime,
        int screenShakeStayTime,
        int screenShakeEaseOutTime
) implements CustomPacketPayload {
    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ModPackets.SCREEN_SHAKE;
    }
}
