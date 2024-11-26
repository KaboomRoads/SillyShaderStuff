package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.GuiDuck;
import com.kaboomroads.tehshadur.client.renderer.ModRenderTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(Gui.class)
public abstract class GuiMixin implements GuiDuck {
    @Unique
    public int flashColor;
    @Unique
    private int flashTime;
    @Unique
    private int flashFadeInTime;
    @Unique
    private int flashStayTime;
    @Unique
    private int flashFadeOutTime;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 1))
    private LayeredDraw addModOverlays(LayeredDraw instance, LayeredDraw layeredDraw, BooleanSupplier renderInner, Operation<LayeredDraw> original) {
        return original.call(instance, layeredDraw, renderInner).add(this::renderFlash);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void modTick(CallbackInfo ci) {
        if (this.flashTime > 0) flashTime--;
    }

    @Unique
    private void renderFlash(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (flashTime > 0) {
            Profiler.get().push("flashOverlay");
            float f = (float) flashTime - deltaTracker.getGameTimeDeltaPartialTick(false);
            float i = 1.0F;
            if (flashTime > flashFadeOutTime + flashStayTime) {
                float g = (float) (flashFadeInTime + flashStayTime + flashFadeOutTime) - f;
                i = (g / (float) flashFadeInTime);
            }
            if (flashTime <= flashFadeOutTime) i = (f / (float) flashFadeOutTime);
            i = Mth.clamp(i, 0.0F, 1.0F);
            guiGraphics.fill(ModRenderTypes.GUI_OVERLAY_ADDITIVE, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), ARGB.color(Math.round(ARGB.alpha(flashColor) * i), ARGB.red(flashColor), ARGB.green(flashColor), ARGB.blue(flashColor)));
            Profiler.get().pop();
        }
    }

    @Override
    public void tehshadur$setTimes(int flashFadeInTime, int flashStayTime, int flashFadeOutTime) {
        this.flashFadeInTime = flashFadeInTime;
        this.flashStayTime = flashStayTime;
        this.flashFadeOutTime = flashFadeOutTime;
        flashTime = this.flashFadeInTime + this.flashStayTime + this.flashFadeOutTime;
    }

    @Override
    public int tehshadur$getFlashColor() {
        return flashColor;
    }

    @Override
    public void tehshadur$setFlashColor(int flashColor) {
        this.flashColor = flashColor;
    }

    @Override
    public int tehshadur$getFlashTime() {
        return flashTime;
    }

    @Override
    public void tehshadur$setFlashTime(int flashTime) {
        this.flashTime = flashTime;
    }

    @Override
    public int tehshadur$getFlashFadeInTime() {
        return flashFadeInTime;
    }

    @Override
    public void tehshadur$setFlashFadeInTime(int flashFadeInTime) {
        this.flashFadeInTime = flashFadeInTime;
    }

    @Override
    public int tehshadur$getFlashStayTime() {
        return flashStayTime;
    }

    @Override
    public void tehshadur$setFlashStayTime(int flashStayTime) {
        this.flashStayTime = flashStayTime;
    }

    @Override
    public int tehshadur$getFlashFadeOutTime() {
        return flashFadeOutTime;
    }

    @Override
    public void tehshadur$setFlashFadeOutTime(int flashFadeOutTime) {
        this.flashFadeOutTime = flashFadeOutTime;
    }
}
