package com.kaboomroads.tehshadur.mixin;

import com.kaboomroads.tehshadur.mixinducks.PlayerDuck;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDuck {
    @Unique
    private AnimationState neckStretchAnimationState = new AnimationState();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationState tehshadur$getNeckStretchAnimationState() {
        return neckStretchAnimationState;
    }

    @Override
    public void tehshadur$setNeckStretchAnimationState(AnimationState neckStretchAnimationState) {
        this.neckStretchAnimationState = neckStretchAnimationState;
    }

    @Override
    public void tehshadur$receiveAnimation(int id) {
        if (id == 0) tehshadur$getNeckStretchAnimationState().start(tickCount);
    }
}
