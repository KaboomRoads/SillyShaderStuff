package com.kaboomroads.tehshadur.mixin;

import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    private void restrictToDivineBounds(Entity entity, CallbackInfo ci) {
        ((EntityDuck) entity).tehshadur$collideWithActiveBorder();
    }
}
