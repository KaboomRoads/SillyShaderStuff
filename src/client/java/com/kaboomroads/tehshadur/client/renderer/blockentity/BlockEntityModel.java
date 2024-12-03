package com.kaboomroads.tehshadur.client.renderer.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityModel<T extends BlockEntityRenderState> extends Model {
    public BlockEntityModel(ModelPart modelPart) {
        this(modelPart, RenderType::entityCutoutNoCull);
    }

    public BlockEntityModel(ModelPart modelPart, Function<ResourceLocation, RenderType> function) {
        super(modelPart, function);
    }

    public void setupAnim(T renderState) {
        resetPose();
    }
}