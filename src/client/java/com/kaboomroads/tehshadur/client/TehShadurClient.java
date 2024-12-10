package com.kaboomroads.tehshadur.client;

import com.kaboomroads.tehshadur.block.ModBlocks;
import com.kaboomroads.tehshadur.block.entity.ModBlockEntities;
import com.kaboomroads.tehshadur.client.mixinducks.CameraDuck;
import com.kaboomroads.tehshadur.client.mixinducks.GameRendererDuck;
import com.kaboomroads.tehshadur.client.mixinducks.GuiDuck;
import com.kaboomroads.tehshadur.client.particle.ErasureResidueParticle;
import com.kaboomroads.tehshadur.client.particle.ExistenceParticle;
import com.kaboomroads.tehshadur.client.renderer.ModRenderTypes;
import com.kaboomroads.tehshadur.client.renderer.ModShaders;
import com.kaboomroads.tehshadur.client.renderer.blockentity.DivineDominanceBlockEntityRenderer;
import com.kaboomroads.tehshadur.client.renderer.blockentity.OmenMonolithBlockEntityRenderer;
import com.kaboomroads.tehshadur.client.renderer.blockentity.desoliscannon.DesolisCannonBlockEntityRenderer;
import com.kaboomroads.tehshadur.client.renderer.blockentity.desoliscannon.DesolisCannonModel;
import com.kaboomroads.tehshadur.client.renderer.entity.erasure.ErasureRenderer;
import com.kaboomroads.tehshadur.entity.ModEntities;
import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import com.kaboomroads.tehshadur.networking.ModPackets;
import com.kaboomroads.tehshadur.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.Entity;

public class TehShadurClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModRenderTypes.init();
        ModShaders.init();
        BlockEntityRenderers.register(ModBlockEntities.DIVINE_DOMINANCE, DivineDominanceBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.OMEN_MONOLITH, OmenMonolithBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.DESOLIS_CANNON, DesolisCannonBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DesolisCannonModel.LAYER_LOCATION, DesolisCannonModel::createBodyLayer);
        ParticleFactoryRegistry.getInstance().register(ModParticles.EXISTENCE, ExistenceParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.ERASURE_RESIDUE, ErasureResidueParticle.Provider::new);
        EntityRendererRegistry.register(ModEntities.ERASURE, ErasureRenderer::new);
        initializeNetworking();
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.ERASURE_FIRE);
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
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.ENTITY_ANIMATION, (payload, context) -> {
            Entity entity = context.client().level.getEntity(payload.entityId());
            if (entity != null) ((EntityDuck) entity).tehshadur$receiveAnimation(payload.animationId());
        });
    }
}
