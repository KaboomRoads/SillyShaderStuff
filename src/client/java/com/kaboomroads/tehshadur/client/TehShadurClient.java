package com.kaboomroads.tehshadur.client;

import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.client.mixinducks.CameraDuck;
import com.kaboomroads.tehshadur.client.mixinducks.GameRendererDuck;
import com.kaboomroads.tehshadur.client.mixinducks.GuiDuck;
import com.kaboomroads.tehshadur.client.particle.ExistenceParticle;
import com.kaboomroads.tehshadur.client.particle.OmenRemnantParticle;
import com.kaboomroads.tehshadur.client.renderer.ModRenderTypes;
import com.kaboomroads.tehshadur.client.renderer.ModShaders;
import com.kaboomroads.tehshadur.client.renderer.blockentity.DivineDominanceBlockEntityRenderer;
import com.kaboomroads.tehshadur.client.renderer.blockentity.OmenMonolithBlockEntityRenderer;
import com.kaboomroads.tehshadur.networking.ModPackets;
import com.kaboomroads.tehshadur.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class TehShadurClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModRenderTypes.init();
        ModShaders.init();
        BlockEntityRenderers.register(ModBlockEntities.DIVINE_DOMINANCE, DivineDominanceBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.OMEN_MONOLITH, OmenMonolithBlockEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.EXISTENCE, ExistenceParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.OMEN_REMNANT, OmenRemnantParticle.Provider::new);
        initializeNetworking();
    }

    public void initializeNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.FLASH, (payload, context) -> {
            GuiDuck gui = (GuiDuck) context.client().gui;
            gui.tehshadur$setFlashColor(payload.flashColor());
            gui.tehshadur$setTimes(payload.flashFadeInTime(), payload.flashStayTime(), payload.flashFadeOutTime());
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.FOV, (payload, context) -> {
            GameRendererDuck renderer = (GameRendererDuck) context.client().gameRenderer;
            renderer.tehshadur$setFovOverride(payload.fov());
            renderer.tehshadur$setFovTimes(payload.fovEaseInTime(), payload.fovStayTime(), payload.fovEaseOutTime());
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SCREEN_SHAKE, (payload, context) -> {
            CameraDuck camera = (CameraDuck) context.client().gameRenderer.getMainCamera();
            camera.tehshadur$setScreenShake(payload.screenShakeStrength(), payload.screenShakeInterval());
            camera.tehshadur$setScreenShakeTimes(payload.screenShakeEaseInTime(), payload.screenShakeStayTime(), payload.screenShakeEaseOutTime());
        });
    }
}
