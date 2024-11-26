package com.kaboomroads.tehshadur.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private Set<BlockEntity> globalBlockEntities;

    @WrapOperation(method = "renderBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", ordinal = 0))
    private void fixDoubleRendering(BlockEntityRenderDispatcher instance, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Operation<Void> original) {
        if (!globalBlockEntities.contains(blockEntity))
            original.call(instance, blockEntity, partialTick, poseStack, bufferSource);
    }
}
