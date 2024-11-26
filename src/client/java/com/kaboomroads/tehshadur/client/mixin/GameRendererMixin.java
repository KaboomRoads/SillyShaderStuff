package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.GameRendererDuck;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererDuck {
    @Unique
    private float fovOverride;
    @Unique
    private int fovTime;
    @Unique
    private int fovEaseInTime;
    @Unique
    private int fovStayTime;
    @Unique
    private int fovEaseOutTime;

    @Inject(method = "tickFov", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getFieldOfViewModifier(ZF)F", ordinal = 0))
    private void fovThyself(CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef fovMod) {
        if (fovTime > 0) {
            fovTime--;
            float i = 1.0F;
            if (fovTime > fovEaseOutTime + fovStayTime) {
                float g = (float) (fovEaseInTime + fovStayTime + fovEaseOutTime) - fovTime;
                i = (g / (float) fovEaseInTime);
            }
            if (fovTime <= fovEaseOutTime) i = (fovTime / (float) fovEaseOutTime);
            fovMod.set(Mth.lerp(i, fovMod.get(), fovOverride));
        }
    }

    @Override
    public void tehshadur$setFovTimes(int fovEaseInTime, int fovStayTime, int fovEaseOutTime) {
        this.fovEaseInTime = fovEaseInTime;
        this.fovStayTime = fovStayTime;
        this.fovEaseOutTime = fovEaseOutTime;
        fovTime = this.fovEaseInTime + this.fovStayTime + this.fovEaseOutTime;
    }

    @Override
    public void tehshadur$setFovOverride(float fovOverride) {
        this.fovOverride = fovOverride;
    }
}
