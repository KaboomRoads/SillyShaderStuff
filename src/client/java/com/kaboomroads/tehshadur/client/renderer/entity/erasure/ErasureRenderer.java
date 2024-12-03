package com.kaboomroads.tehshadur.client.renderer.entity.erasure;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.entity.custom.Erasure;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class ErasureRenderer<T extends Erasure> extends EntityRenderer<T, ErasureRenderState> {
    public static final ResourceLocation GLYPH = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/entity/erasure/glyph.png");
    public static final ResourceLocation SHOCKWAVE = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/entity/erasure/shockwave.png");
    public static final ResourceLocation BEAM = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/entity/erasure/beam.png");

    public ErasureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ErasureRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        if (renderState.time >= 100) {
            RenderSystem.setShaderTexture(0, GLYPH);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.pushPose();
            float time = renderState.time - 100;
            float maxTime = renderState.maxTime - 100;
            float peak = 3.0F;
            float height = 1.0F;
            float scale = time >= maxTime - peak
                    ? -height / Mth.square(peak) * Mth.square(time - (maxTime - peak)) + height
                    : time / (maxTime - peak);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotation(time * 0.025F));
            quad(builder, poseStack.last(),
                    -5, 0, 5,
                    5, 0, 5,
                    5, 0, -5,
                    -5, 0, -5
            );
            poseStack.popPose();
            upload(builder);
        } else {
            RenderSystem.setShaderTexture(0, SHOCKWAVE);
//            poseStack.pushPose();
            float time = renderState.time;
            float maxTime = renderState.maxTime - 100;
            float peak = 100.0F;
            float height = 100.0F;
            float scale = -height / Mth.square(peak) * Mth.square(time - (maxTime - peak)) + height;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - scale / peak);
            quad(builder, poseStack.last(),
                    -scale, 0, scale,
                    scale, 0, scale,
                    scale, 0, -scale,
                    -scale, 0, -scale
            );
            quad(builder, poseStack.last(),
                    -scale, scale, 0,
                    scale, scale, 0,
                    scale, -scale, 0,
                    -scale, -scale, 0
            );
            quad(builder, poseStack.last(),
                    0, scale, -scale,
                    0, scale, scale,
                    0, -scale, scale,
                    0, -scale, -scale
            );
            poseStack.pushPose();
            upload(builder);
            BufferBuilder beamBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            RenderSystem.setShaderTexture(0, BEAM);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, Math.max(0.0F, time * 0.05F)));
            Quaternionf quaternion = new Quaternionf().rotationY(
                    yawFacing(entityRenderDispatcher.camera.getPosition(), new Vec3(renderState.x, renderState.y, renderState.z))
            );
            poseStack.mulPose(quaternion);
            scale = Mth.sin(time * 2.0F) * 0.1F + 0.9F;
            if (time <= 50) scale *= time * 0.02F;
            float minY = -3;
            quad(beamBuilder, poseStack.last(), -scale, time, 0, scale, time, 0, scale, minY, 0, -scale, minY, 0);
            upload(beamBuilder);
            poseStack.popPose();
        }

        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    public static float yawFacing(Vec3 cameraPos, Vec3 pos) {
        Vec3 delta = cameraPos.subtract(pos);
        return (float) -Mth.atan2(delta.z, delta.x) - Mth.HALF_PI;
    }

    public static void upload(BufferBuilder builder) {
        MeshData meshData = builder.build();
        if (meshData != null) BufferUploader.drawWithShader(meshData);
    }

    public static void quad(VertexConsumer consumer, PoseStack.Pose pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        vertex(consumer, pose, x1, y1, z1, 0.0F, 0.0F);
        vertex(consumer, pose, x2, y2, z2, 1.0F, 0.0F);
        vertex(consumer, pose, x3, y3, z3, 1.0F, 1.0F);
        vertex(consumer, pose, x4, y4, z4, 0.0F, 1.0F);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        consumer.addVertex(pose, x, y, z).setUv(u, v);
    }

//    @NotNull
//    @Override
//    protected AABB getBoundingBoxForCulling(T entity) {
//        return super.getBoundingBoxForCulling(entity).setMinY(Double.MIN_VALUE).setMaxY(Double.MAX_VALUE);
//    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @NotNull
    @Override
    public ErasureRenderState createRenderState() {
        return new ErasureRenderState();
    }

    @Override
    public void extractRenderState(T entity, ErasureRenderState renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        long end = entity.getEnd();
        long gameTime = entity.level().getGameTime();
        long time = end - gameTime;
        renderState.time = time - partialTick;
        renderState.maxTime = entity.maxTime;
    }
}
