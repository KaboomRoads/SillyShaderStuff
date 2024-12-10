package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.HumanoidRenderStateDuck;
import com.kaboomroads.tehshadur.client.renderer.entity.player.PlayerAnimation;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel, HeadedModel {
    protected HumanoidModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void setupCustomAnim(T renderState, CallbackInfo ci) {
        HumanoidRenderStateDuck duck = ((HumanoidRenderStateDuck) renderState);
        animate(duck.tehshadur$getNeckStretchAnimationState(), PlayerAnimation.NECK_STRETCH, renderState.ageInTicks);
    }
}
