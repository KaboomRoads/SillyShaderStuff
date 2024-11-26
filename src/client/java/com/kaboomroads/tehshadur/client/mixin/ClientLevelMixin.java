package com.kaboomroads.tehshadur.client.mixin;

import com.kaboomroads.tehshadur.mixinducks.EntityDuck;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    private void restrictToDivineBounds(Entity entity, CallbackInfo ci) {
        ((EntityDuck) entity).tehshadur$collideWithActiveBorder();
    }
}
