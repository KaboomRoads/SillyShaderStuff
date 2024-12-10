package com.kaboomroads.tehshadur.mixinducks;

import net.minecraft.world.entity.AnimationState;

public interface PlayerDuck extends EntityDuck {
    AnimationState tehshadur$getNeckStretchAnimationState();

    void tehshadur$setNeckStretchAnimationState(AnimationState neckStretchAnimationState);
}
