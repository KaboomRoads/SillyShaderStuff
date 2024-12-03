package com.kaboomroads.tehshadur.client.renderer.blockentity.desoliscannon;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.entity.custom.DesolisCannonBlockEntity;
import com.kaboomroads.tehshadur.client.renderer.entity.erasure.ErasureRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public final class DesolisCannonBlockEntityRenderer implements BlockEntityRenderer<DesolisCannonBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/block/desolis_cannon.png");
    public final BlockEntityRendererProvider.Context context;
    public final DesolisCannonModel model;
    public final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public DesolisCannonBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        this.model = new DesolisCannonModel(context.bakeLayer(DesolisCannonModel.LAYER_LOCATION));
        this.blockEntityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(DesolisCannonBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        DesolisCannonRenderState renderState = new DesolisCannonRenderState();
        renderState.pos = blockEntity.getBlockPos();
        renderState.ageInTicks = blockEntity.tickCount + partialTick;
        renderState.distanceToCameraSq = blockEntityRenderDispatcher.camera.getPosition().distanceToSqr(Vec3.atBottomCenterOf(blockEntity.getBlockPos()));
        renderState.fireAnimationState.copyFrom(blockEntity.fireAnimationState);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);
        model.setupAnim(renderState);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(poseStack, vertexConsumer, blockEntity.getLevel() == null ? 15728880 : LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above()), packedOverlay);

        long end = blockEntity.beamTime;
        long gameTime = blockEntity.getLevel().getGameTime();
        long longTime = end - gameTime;
        float maxBeamTime = blockEntity.maxBeamTime;
        float time = (longTime - partialTick) / maxBeamTime;
        if (time > 0) {
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

            RenderSystem.setShaderTexture(0, ErasureRenderer.BEAM);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, time < 0.25F ? time * 4.0F : 1.0F);

            BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            float scale = 0.25F;
            poseStack.pushPose();
            time = 1.0F - time;
            Vec3 pos = Vec3.atBottomCenterOf(blockEntity.getBlockPos());
            Quaternionf quaternion = new Quaternionf().rotationY(
                    yawFacing(blockEntityRenderDispatcher.camera.getPosition(), new Vec3(pos.x, pos.y, pos.z))
            );
            poseStack.mulPose(quaternion);
            quad(builder, poseStack.last(),
                    -scale, time * -100 - 3, 0, scale, time * -100 - 3, 0, scale, time * -80 + 3, 0, -scale, time * -80 + 3, 0
            );
            poseStack.popPose();
            upload(builder);

            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
        }
        poseStack.popPose();
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

    @Override
    public int getViewDistance() {
        return 256;
    }
}