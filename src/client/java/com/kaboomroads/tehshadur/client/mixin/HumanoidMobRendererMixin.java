package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.HumanoidRenderStateDuck;
import com.kaboomroads.tehshadur.mixinducks.PlayerDuck;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {
    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void extractCustomAnimations(LivingEntity livingEntity, HumanoidRenderState renderState, float f, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        if (livingEntity instanceof PlayerDuck playerDuck)
            ((HumanoidRenderStateDuck) renderState).tehshadur$getNeckStretchAnimationState().copyFrom(playerDuck.tehshadur$getNeckStretchAnimationState());
    }
}
