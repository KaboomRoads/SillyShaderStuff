package com.kaboomroads.tehshadur.client.renderer.blockentity;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.entity.custom.DivineDominanceBlockEntity;
import com.kaboomroads.tehshadur.client.renderer.ModShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public record DivineDominanceBlockEntityRenderer(BlockEntityRendererProvider.Context context)
        implements BlockEntityRenderer<DivineDominanceBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/misc/divine_force.png");

    @Override
    public void render(DivineDominanceBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.borderProvider.bounds == null) return;
        float radius = blockEntity.borderProvider.getRadius(blockEntity.getLevel().getGameTime(), partialTick);
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 pos = blockEntity.borderProvider.bounds.getCenter();
        float minX = (float) pos.x - radius - (float) camera.x;
        float minY = (float) pos.y - radius - (float) camera.y;
        float minZ = (float) pos.z - radius - (float) camera.z;
        float maxX = (float) pos.x + radius - (float) camera.x;
        float maxY = (float) pos.y + radius - (float) camera.y;
        float maxZ = (float) pos.z + radius - (float) camera.z;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
//        RenderSystem.blendFuncSeparate(
//                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
//        );
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(ModShaders.DIVINE_DOMINANCE);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, ModShaders.POSITION_TEX_NORMAL);

        quad(builder, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, minY, maxZ, minX, minY, maxZ, 0.0F, 0.0F, 1.0F);
        quad(builder, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, 0.0F, 0.0F, -1.0F);
        quad(builder, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, 1.0F, 0.0F, 0.0F);
        quad(builder, minX, maxY, minZ, minX, maxY, maxZ, minX, minY, maxZ, minX, minY, minZ, -1.0F, 0.0F, 0.0F);
        quad(builder, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, 0.0F, 1.0F, 0.0F);
        quad(builder, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, 0.0F, -1.0F, 0.0F);

        MeshData meshData = builder.build();
        if (meshData != null) BufferUploader.drawWithShader(meshData);
        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    public static void quad(VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float nx, float ny, float nz) {
        vertex(consumer, x1, y1, z1, 0.0F, 0.0F, nx, ny, nz);
        vertex(consumer, x2, y2, z2, 1.0F, 0.0F, nx, ny, nz);
        vertex(consumer, x3, y3, z3, 1.0F, 1.0F, nx, ny, nz);
        vertex(consumer, x4, y4, z4, 0.0F, 1.0F, nx, ny, nz);
    }

    public static void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        consumer.addVertex(x, y, z).setUv(u, v).setNormal(nx, ny, nz);
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    @Override
    public boolean shouldRenderOffScreen(DivineDominanceBlockEntity blockEntity) {
        return true;
    }
}