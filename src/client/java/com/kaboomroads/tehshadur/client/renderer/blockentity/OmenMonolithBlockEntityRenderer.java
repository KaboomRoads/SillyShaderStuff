package com.kaboomroads.tehshadur.client.renderer.blockentity;

import com.kaboomroads.tehshadur.TehShadur;
import com.kaboomroads.tehshadur.block.entity.custom.OmenMonolithBlockEntity;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public record OmenMonolithBlockEntityRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<OmenMonolithBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TehShadur.MOD_ID, "textures/misc/omen_border.png");

    @Override
    public void render(OmenMonolithBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
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

        AABB blockBounds = new AABB(blockEntity.getBlockPos());
        blockBounds = blockBounds.setMaxY(blockBounds.maxY + 3).inflate(0.25);
        blockBounds = blockBounds.setMaxY(blockBounds.maxY + 1);
        float minBlockX = (float) blockBounds.minX - (float) camera.x;
        float minBlockY = (float) blockBounds.minY - (float) camera.y;
        float minBlockZ = (float) blockBounds.minZ - (float) camera.z;
        float maxBlockX = (float) blockBounds.maxX - (float) camera.x;
        float maxBlockY = (float) blockBounds.maxY - (float) camera.y;
        float maxBlockZ = (float) blockBounds.maxZ - (float) camera.z;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.setShader(ModShaders.OMEN_MONOLITH);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        quad(builder, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, minY, maxZ, minX, minY, maxZ);
        quad(builder, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ);
        quad(builder, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ);
        quad(builder, minX, maxY, minZ, minX, maxY, maxZ, minX, minY, maxZ, minX, minY, minZ);
        quad(builder, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ);
        quad(builder, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ);

        MeshData meshData = builder.build();
        if (meshData != null) BufferUploader.drawWithShader(meshData);

        RenderSystem.setShader(ModShaders.OMEN_RINGS);
        BufferBuilder builder2 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, ModShaders.POSITION_TEX_NORMAL);

        quad(builder2, minBlockX, maxBlockY, maxBlockZ, maxBlockX, maxBlockY, maxBlockZ, maxBlockX, minBlockY, maxBlockZ, minBlockX, minBlockY, maxBlockZ, 0.0F, 0.0F, 1.0F);
        quad(builder2, minBlockX, maxBlockY, minBlockZ, maxBlockX, maxBlockY, minBlockZ, maxBlockX, minBlockY, minBlockZ, minBlockX, minBlockY, minBlockZ, 0.0F, 0.0F, -1.0F);
        quad(builder2, maxBlockX, maxBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ, maxBlockX, minBlockY, maxBlockZ, maxBlockX, minBlockY, minBlockZ, 1.0F, 0.0F, 0.0F);
        quad(builder2, minBlockX, maxBlockY, minBlockZ, minBlockX, maxBlockY, maxBlockZ, minBlockX, minBlockY, maxBlockZ, minBlockX, minBlockY, minBlockZ, -1.0F, 0.0F, 0.0F);

        float lowerRingY = blockEntity.getBlockPos().getY() - (float) camera.y;
        float offset = 1.0F;
        quad(builder2, minBlockX - offset, lowerRingY, maxBlockZ + offset, maxBlockX + offset, lowerRingY, maxBlockZ + offset, maxBlockX + offset, lowerRingY, minBlockZ - offset, minBlockX - offset, lowerRingY, minBlockZ - offset, 0.0F, 1.0F, 0.0F);

        MeshData meshData2 = builder2.build();
        if (meshData2 != null) BufferUploader.drawWithShader(meshData2);

        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    public static void quad(VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        vertex(consumer, x1, y1, z1, 0.0F, 0.0F);
        vertex(consumer, x2, y2, z2, 1.0F, 0.0F);
        vertex(consumer, x3, y3, z3, 1.0F, 1.0F);
        vertex(consumer, x4, y4, z4, 0.0F, 1.0F);
    }

    public static void quad(VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float nx, float ny, float nz) {
        vertex(consumer, x1, y1, z1, 0.0F, 0.0F, nx, ny, nz);
        vertex(consumer, x2, y2, z2, 1.0F, 0.0F, nx, ny, nz);
        vertex(consumer, x3, y3, z3, 1.0F, 1.0F, nx, ny, nz);
        vertex(consumer, x4, y4, z4, 0.0F, 1.0F, nx, ny, nz);
    }

    public static void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v) {
        consumer.addVertex(x, y, z).setUv(u, v);
    }

    public static void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        consumer.addVertex(x, y, z).setUv(u, v).setNormal(nx, ny, nz);
    }


    @Override
    public int getViewDistance() {
        return 512;
    }

    @Override
    public boolean shouldRenderOffScreen(OmenMonolithBlockEntity blockEntity) {
        return true;
    }
}