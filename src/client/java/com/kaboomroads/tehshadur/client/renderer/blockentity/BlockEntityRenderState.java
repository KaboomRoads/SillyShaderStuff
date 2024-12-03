package com.kaboomroads.tehshadur.client.renderer.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderState {
    public BlockPos pos;
    public float ageInTicks;
    public double distanceToCameraSq;
}
