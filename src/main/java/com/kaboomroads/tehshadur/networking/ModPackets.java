package com.kaboomroads.tehshadur.networking;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.networking.payload.EntityAnimationPayload;
import com.kaboomroads.tehshadur.networking.payload.FlashPayload;
import com.kaboomroads.tehshadur.networking.payload.FovPayload;
import com.kaboomroads.tehshadur.networking.payload.ScreenShakePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ModPackets {
    public static final CustomPacketPayload.Type<FlashPayload> FLASH = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "flash"));
    public static final CustomPacketPayload.Type<FovPayload> FOV = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "fov"));
    public static final CustomPacketPayload.Type<ScreenShakePayload> SCREEN_SHAKE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "screen_shake"));
    public static final CustomPacketPayload.Type<EntityAnimationPayload> ENTITY_ANIMATION = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "entity_animation"));

    public static void init() {
        PayloadTypeRegistry.playS2C().register(FLASH, CustomPacketPayload.codec((payload, buf) -> {
            buf.writeInt(payload.flashColor());
            buf.writeInt(payload.flashFadeInTime());
            buf.writeInt(payload.flashStayTime());
            buf.writeInt(payload.flashFadeOutTime());
        }, buf -> new FlashPayload(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt())));
        PayloadTypeRegistry.playS2C().register(FOV, CustomPacketPayload.codec((payload, buf) -> {
            buf.writeFloat(payload.fov());
            buf.writeInt(payload.fovEaseInTime());
            buf.writeInt(payload.fovStayTime());
            buf.writeInt(payload.fovEaseOutTime());
        }, buf -> new FovPayload(buf.readFloat(), buf.readInt(), buf.readInt(), buf.readInt())));
        PayloadTypeRegistry.playS2C().register(SCREEN_SHAKE, CustomPacketPayload.codec((payload, buf) -> {
            buf.writeFloat(payload.screenShakeStrength());
            buf.writeFloat(payload.screenShakeInterval());
            buf.writeInt(payload.screenShakeEaseInTime());
            buf.writeInt(payload.screenShakeStayTime());
            buf.writeInt(payload.screenShakeEaseOutTime());
        }, buf -> new ScreenShakePayload(buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readInt())));
        PayloadTypeRegistry.playS2C().register(ENTITY_ANIMATION, CustomPacketPayload.codec((payload, buf) -> {
            buf.writeInt(payload.entityId());
            buf.writeInt(payload.animationId());
        }, buf -> new EntityAnimationPayload(buf.readInt(), buf.readInt())));
    }
}
