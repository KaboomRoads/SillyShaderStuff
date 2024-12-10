package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.client.mixinducks.HumanoidRenderStateDuck;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public abstract class HumanoidRenderStateMixin implements HumanoidRenderStateDuck {
    @Unique
    private AnimationState neckStretchAnimationState = new AnimationState();

    @Override
    public AnimationState tehshadur$getNeckStretchAnimationState() {
        return neckStretchAnimationState;
    }

    @Override
    public void tehshadur$setNeckStretchAnimationState(AnimationState neckStretchAnimationState) {
        this.neckStretchAnimationState = neckStretchAnimationState;
    }
}
